package net.oikmo.engine.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import com.mojang.minecraft.particle.Particle;
import com.mojang.minecraft.particle.ParticleEngine;
import com.mojang.minecraft.phys.AABB;

import net.oikmo.engine.Loader;
import net.oikmo.engine.entity.Camera;
import net.oikmo.engine.entity.Entity;
import net.oikmo.engine.entity.ItemEntity;
import net.oikmo.engine.entity.Player;
import net.oikmo.engine.inventory.Container;
import net.oikmo.engine.models.RawModel;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.engine.save.ChunkSaveData;
import net.oikmo.engine.save.SaveData;
import net.oikmo.engine.save.SaveSystem;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.engine.world.chunk.Chunk;
import net.oikmo.engine.world.chunk.MasterChunk;
import net.oikmo.main.Main;
import net.oikmo.network.shared.PacketRequestChunk;
import net.oikmo.toolbox.FastMath;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Logger.LogLevel;
import net.oikmo.toolbox.Maths;
import net.oikmo.toolbox.noise.OpenSimplexNoise;

public class World {

	public static final int WORLD_HEIGHT = 128;
	public static int RENDER_SIZE = 4;
	public static final int WORLD_SIZE = RENDER_SIZE*8;

	public Map<Vector3f, MasterChunk> chunkMap = new HashMap<>();
	public List<Vector3f> usedPositions = new ArrayList<>();

	private List<MasterChunk> currentMasterChunks = Collections.synchronizedList(new ArrayList<MasterChunk>());
	private List<Entity> entities = new ArrayList<>();

	public ParticleEngine particleEngine;

	private long seed;
	private OpenSimplexNoise noise;

	private Thread chunkCreator;

	public World(String seed) {
		init(Maths.getSeedFromName(seed));
	}
	public World(long seed) {
		init(seed);
	}
	public World() {
		init(new Random().nextLong());
	}

	private void init(long seed) {
		this.seed = seed;
		this.noise = new OpenSimplexNoise(this.seed);
		particleEngine = new ParticleEngine();
	}	
	public void update(Camera camera) {
		currentMasterChunks.clear();
		for (int x = (int) (Main.camPos.x - WORLD_SIZE) / 16; x < (Main.camPos.x + WORLD_SIZE) / 16; x++) {
			for (int z = (int) (Main.camPos.z - WORLD_SIZE) / 16; z < (Main.camPos.z + WORLD_SIZE) / 16; z++) {
				int chunkX = x * 16;
				int chunkZ = z * 16;
				Vector3f chunkPos = new Vector3f(chunkX, 0, chunkZ);

				synchronized(usedPositions) {
					if(isPositionUsed(chunkPos)) {
						MasterChunk master = getChunkFromPosition(chunkPos);
						if(master != null) {
							if(isInValidRange(master.getOrigin())) {
								if(Main.theNetwork != null) {
									master.timer = MasterChunk.maxTime;
								}
								if(master.getEntity() == null) {
									if(master.getMesh() != null) {
										if(master.getMesh().hasMeshInfo()) {
											RawModel raw = Loader.loadToVAO(master.getMesh().positions, master.getMesh().uvs, master.getMesh().normals);
											TexturedModel texModel = new TexturedModel(raw, MasterRenderer.currentTexturePack);
											Entity entity = new Entity(texModel, master.getOrigin(), new Vector3f(0,0,0),1);
											master.setEntity(entity);
											master.getMesh().removeMeshInfo();
										}
									} else {
										master.createMesh();
									}
								}
							} 
						}

						if(master != null) {
							if(!currentMasterChunks.contains(master)) {
								currentMasterChunks.add(master);
							}
						}
					}
				}	
			}
		}

		for(int m = 0; m < chunkMap.values().size(); m++) {
			MasterChunk master = (MasterChunk) chunkMap.values().toArray()[m];

			if(master != null) {
				if(!isInValidRange(master.getOrigin())) {
					if(Main.theNetwork != null) {
						if(master.timer > 0) {
							master.timer--;
						}
						if(master.timer <= 0) {
							System.out.println("removing chunk at " + master.getOrigin() + " as 5s has passed");
							chunkMap.remove(master.getOrigin());
							hasAsked.remove(master.getOrigin());
						}
					}

				}
			}
		}

		for(MasterChunk master : currentMasterChunks) {
			if(master.getEntity() != null) {
				MasterRenderer.getInstance().addEntity(master.getEntity());
			}
		}

		for(Entity entity : entities) {
			if(isInValidRange(entity.getPosition())) {
				MasterRenderer.getInstance().addEntity(entity);
			}
		}
		MasterRenderer.getInstance().render(camera);
		particleEngine.render(Main.thePlayer, 1f);
	}
	public void tick() {
		if(Main.thePlayer != null) {
			Main.thePlayer.tick();
		}
		particleEngine.tick();

		ItemEntity.updateOscillation();
		synchronized(entities) {
			for(int i = 0; i < entities.size(); i++) {
				Entity entity = entities.get(i);
				if(entity != null) {
					if(!entity.shouldBeRemoved()) {
						if(isInValidRange(entity.getPosition())) {
							entity.tick();
						}
					} else {
						entities.remove(entity);
					}

				}
			}
		}
	}

	public void spawnParticle(Particle p) {
		this.particleEngine.particles.add(p);
	}

	public List<AABB> getSurroundingAABBsPhys(AABB aabb, int aabbOffset) {

		List<AABB> surroundingAABBs = new ArrayList<>();

		int x0 = Maths.roundFloat(aabb.minX - aabbOffset);
		int x1 = Maths.roundFloat(aabb.maxX + aabbOffset);
		int y0 = Maths.roundFloat(aabb.minY - aabbOffset);
		int y1 = Maths.roundFloat(aabb.maxY + aabbOffset);
		int z0 = Maths.roundFloat(aabb.minZ - aabbOffset);
		int z1 = Maths.roundFloat(aabb.maxZ + aabbOffset);

		for(int x = x0; x < x1; ++x) {
			for(int y = y0; y < y1; ++y) {
				for(int z = z0; z < z1; ++z) {
					if(getBlock(new Vector3f(x,y,z)) != null) {
						AABB other = new AABB(x-0.5f, y-0.5f, z-0.5f, x+0.5f, y+0.5f, z+0.5f);
						surroundingAABBs.add(other);
					}

				}
			}
		}


		return surroundingAABBs;
	}

	/*  if(entity instanceof ItemEntity) {
			if(!((ItemEntity)entity).dontTick && entity.getPosition().y > 0) {
				entity.tick();
			} else {
				if(Main.thePlayer.getInventory().addItem(((ItemEntity)entity).getItem())) {
					entities.remove(entity);
				} else {
					((ItemEntity)entity).dontTick = false;
				}
				continue;
			}
		}
	 */

	public void addEntity(Entity ent) {
		if(!entities.contains(ent)) {
			entities.add(ent);
		}
	}

	public Vector3f getBlockPositionFromCalculatedChunk(int x, int y, int z) {
		Vector3f chunkPos = new Vector3f();
		Maths.calculateChunkPosition(new Vector3f(x,y,z), chunkPos);
		MasterChunk m = getChunkFromPosition(chunkPos);

		if(m != null) {
			int localX = (int)(x + m.getOrigin().x)%16;
			int localY = (int) y;
			int localZ = (int)(z + m.getOrigin().z)%16;

			if(localX < 0) {
				localX = localX+16;
			}
			if(localZ < 0) {
				localZ = localZ+16;
			}

			return new Vector3f(localX,localY,localZ);
		}
		return null;
	}


	public MasterChunk setBlockNoUpdate(Vector3f position, Block block) {
		Vector3f chunkPos = new Vector3f();
		Maths.calculateChunkPosition(position, chunkPos);
		MasterChunk m = getChunkFromPosition(chunkPos);
		if(m != null) {
			m.setBlockNoUpdate(position, block);
			if(Main.theNetwork != null) {
				Main.theNetwork.updateChunk(position,block, false, "no update");
			}

		}
		return m;
	}
	public MasterChunk setBlockNoUpdateNoNet(Vector3f position, Block block) {
		Vector3f chunkPos = new Vector3f();
		Maths.calculateChunkPosition(position, chunkPos);
		MasterChunk m = getChunkFromPosition(chunkPos);
		if(m != null) {
			m.setBlockNoUpdate(position, block);
		}
		return m;
	}
	public boolean setBlock(Vector3f position, Block block) {
		Vector3f chunkPos = new Vector3f();
		Maths.calculateChunkPosition(position, chunkPos);
		MasterChunk m = getChunkFromPosition(chunkPos);
		if(m != null) {
			m.setBlock(position, block);
			if(Main.theNetwork != null) {
				Main.theNetwork.updateChunk(position,block, true, "update");
			}
			return true;
		}
		return false;
	}
	public boolean setBlockNoNet(Vector3f position, Block block) {
		Vector3f chunkPos = new Vector3f();
		Maths.calculateChunkPosition(position, chunkPos);
		MasterChunk m = getChunkFromPosition(chunkPos);
		if(m != null) {
			m.setBlock(position, block);
			return true;
		}
		return false;
		
	}
	public Block getBlock(Vector3f position) {
		Vector3f chunkPos = new Vector3f();
		Maths.calculateChunkPosition(position, chunkPos);
		MasterChunk m = getChunkFromPosition(chunkPos);
		if(m != null) {
			return m.getBlock(position);
		}

		return null;
	}

	public boolean blockHasNeighbours(Vector3f position) {
		Vector3f chunkPos = new Vector3f();
		Maths.calculateChunkPosition(position, chunkPos);
		MasterChunk m = getChunkFromPosition(chunkPos);

		if(m != null) {
			int localX = (int)(position.x + m.getOrigin().x)%16;
			int localY = (int) position.y;
			int localZ = (int)(position.z + m.getOrigin().z)%16;

			if(localX < 0) {
				localX = localX+16;
			}
			if(localZ < 0) {
				localZ = localZ+16;
			}

			for(int dx = -1; dx <= 1; dx++) {
				for(int dy = -1; dy <= 1; dy++) {
					for(int dz= -1; dz <= 1; dz++) {

						if (dx == 0 && dy == 0 && dz == 0) {
							continue; // Skip the current block
						}
						int x = localX + dx;
						int y = localY + dy;
						int z = localZ + dz;

						if((x != localX && y != localY && z != localZ) && (x != localX || y != localY || z != localZ)) {
							continue;
						}

						if(Maths.isWithinChunk(x, y, z)) {
							if(m.getChunk().blocks[x][y][z] != -1) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}	
	public boolean anyBlockInSpecificLocation(int x, int y, int z) {
		return getBlock(new Vector3f(x,y,z)) != null;
	}


	Vector3f holder = new Vector3f();
	public void createRadiusFromBlock(int r, Block block, int x, int y, int z) {
		List<MasterChunk> chunksToRefresh = new ArrayList<>();

		for(int tx=-r; tx< r+1; tx++){
			for(int ty=-r; ty< r+1; ty++){
				for(int tz=-r; tz< r+1; tz++){
					if(FastMath.sqrt(FastMath.pow(tx, 2)  +  FastMath.pow(ty, 2)  +  FastMath.pow(tz, 2)) <= r-2){
						holder.x = tx+x;
						holder.y = ty+y;
						holder.z =  tz+z;
						MasterChunk m = setBlockNoUpdate(holder,null);
						if(!chunksToRefresh.contains(m)) {
							chunksToRefresh.add(m);
						}
					}
				}
			}
		}
		for(MasterChunk m : chunksToRefresh) {
			refreshChunk(m);
			setBlock(new Vector3f(m.getOrigin().x, -1, m.getOrigin().z),null);
		}
	}

	public void startChunkCreator() {
		this.chunkCreator = new Thread(new Runnable() { 
			public void run() {
				while (!Main.displayRequest) {
					for (int x = (int) (Main.camPos.x - WORLD_SIZE) / 16; x < (Main.camPos.x + WORLD_SIZE) / 16; x++) {
						for (int z = (int) (Main.camPos.z - WORLD_SIZE) / 16; z < (Main.camPos.z + WORLD_SIZE) / 16; z++) {
							int chunkX = x * 16;
							int chunkZ = z * 16;

							Vector3f chunkPos = new Vector3f(chunkX, 0, chunkZ);
							synchronized(usedPositions) {
								if(!isPositionUsed(chunkPos) && getChunkFromPosition(chunkPos) == null) {
									MasterChunk m = new MasterChunk(noise, chunkPos);
									addChunk(m);
								}
							}
						}
					}
				}
			}
		});
		this.chunkCreator.setName("Chunk Creator");
		this.chunkCreator.start();
	}
	private List<Vector3f> hasAsked = new ArrayList<>();
	public void startChunkRetriever() {
		this.chunkCreator = new Thread(new Runnable() { 
			public void run() {
				while (!Main.displayRequest) {
					if(Main.thePlayer != null && Main.thePlayer.getCurrentChunk() != null && !Main.thePlayer.tick) {
						Main.thePlayer.tick = true;
					}

					for (int x = (int) (Main.camPos.x - WORLD_SIZE) / 16; x < (Main.camPos.x + WORLD_SIZE) / 16; x++) {
						for (int z = (int) (Main.camPos.z - WORLD_SIZE) / 16; z < (Main.camPos.z + WORLD_SIZE) / 16; z++) {
							int chunkX = x * 16;
							int chunkZ = z * 16;

							Vector3f chunkPos = new Vector3f(chunkX, 0, chunkZ);
							synchronized(hasAsked) {
								if(!hasAsked.contains(chunkPos)) {
									hasAsked.add(chunkPos);
									PacketRequestChunk packet = new PacketRequestChunk();
									packet.x = chunkX;
									packet.z = chunkZ;
									Main.theNetwork.client.sendTCP(packet);
								} else {
									continue;
								}	
							}
						}
					}

				}
			}
		});

		this.chunkCreator.setName("Chunk Creator");
		this.chunkCreator.start();
	}
	public boolean isChunkThreadRunning() {
		if(this.chunkCreator != null) {
			return this.chunkCreator.isAlive();
		}
		return false;
	}

	public void addChunk(MasterChunk m) {
		usedPositions.add(m.getOrigin());
		chunkMap.put(m.getOrigin(), m);
	}

	public void handleLoad(SaveData data) {
		if(Main.thePlayer == null) {
			Main.thePlayer = new Player(new Vector3f(data.x, data.y, data.z), new Vector3f(data.rotX, data.rotY, data.rotZ));
		}

		Main.thePlayer.setInventory(Container.loadSavedContainer(data.cont));
		Main.thePlayer.resetMotion();

		Vector3f v = Main.thePlayer.getPosition();
		v.y = getHeightFromPosition(Main.thePlayer.getRoundedPosition());
		if(v.y != -1) {
			v.y++;
			Main.thePlayer.setPosition(v);
		}

		Main.thePlayer.getCamera().setRotation(data.rotX, data.rotY, data.rotZ);
	}	

	public void refreshChunk(MasterChunk master) {
		master.getChunk().calcLightDepths(0, 0, Chunk.CHUNK_SIZE, Chunk.CHUNK_SIZE);
		if(master.getMesh() != null) {
			master.getMesh().removeMeshInfo();
			master.destroyMesh();
			master.setEntity(null);
			master.createMesh();
		}
	}
	public void refreshChunks() {
		Logger.log(LogLevel.INFO, "Clearing " + currentMasterChunks.size() + " chunks!");
		try {
			for(int i = 0; i < currentMasterChunks.size(); i++) {
				MasterChunk master = currentMasterChunks.get(i);
				if(master != null) {
					master.getChunk().calcLightDepths(0, 0, Chunk.CHUNK_SIZE, Chunk.CHUNK_SIZE);
					if(master.getMesh() != null) {
						master.getMesh().removeMeshInfo();
						master.destroyMesh();
						master.setEntity(null);
						master.createMesh();
						
					}
				}
			}
		} catch(Exception e) {e.printStackTrace();}

	}

	public int getHeightFromPosition(Vector3f position) {
		Vector3f chunkPos = new Vector3f();
		Maths.calculateChunkPosition(position, chunkPos);
		MasterChunk m = getChunkFromPosition(chunkPos);

		if(m != null) {
			return m.getChunk().getHeightFromPosition(chunkPos, position);
		}
		return -1;
	}
	public boolean isInValidRange(Vector3f origin) {
		return isInValidRange(1, origin);
	}
	public boolean isInValidRange(int size, Vector3f origin) {
		int distX = (int) FastMath.abs((Main.camPos.x * size) - origin.x);
		int distZ = (int) FastMath.abs((Main.camPos.z * size) - origin.z);

		if((distX <= WORLD_SIZE) && (distZ <= WORLD_SIZE)) {
			return true;
		}

		return false;
	}

	public MasterChunk getChunkFromPosition(Vector3f position) {
		return chunkMap.get(getPosition(position));
	}

	private MasterChunk prevChunk;
	public MasterChunk getChunkFromPositionOther(Vector3f position) {
		if(prevChunk == null) {
			for(MasterChunk chunk : chunkMap.values()) {
				if((int)chunk.getOrigin().x == (int)position.x && (int)chunk.getOrigin().z == (int)position.z) {
					prevChunk = chunk;
				}
			}
		} else if(!((int)prevChunk.getOrigin().x == (int)position.x && (int)prevChunk.getOrigin().z == (int)position.z)){
			for(MasterChunk chunk : chunkMap.values()) {
				if((int)chunk.getOrigin().x == (int)position.x && (int)chunk.getOrigin().z == (int)position.z) {
					prevChunk = chunk;
				}
			}
		}

		return prevChunk;
	}
	public boolean isPositionUsed(Vector3f pos) {
		boolean result = false;
		synchronized(usedPositions) {
			result = usedPositions.contains(new Vector3f(pos.x, 0, pos.z));
		}
		return result;
	}
	public Vector3f getPosition(Vector3f pos) {
		Vector3f result = null;
		synchronized(usedPositions) {
			for(int i = 0; i < usedPositions.size(); i++) {

				try {
					usedPositions.get(i);
				} catch(IndexOutOfBoundsException e) {

					continue;
				}
				if(usedPositions.get(i) != null) {
					if((int)usedPositions.get(i).x == (int)pos.x && (int)usedPositions.get(i).z == (int)pos.z) {
						result = usedPositions.get(i);
					}
				}

			}
		}
		return result;
	}
	public Vector3f getPositionFromMap(Vector3f pos) {
		Vector3f result = null;
		synchronized(this.hasAsked) {
			for(int i = 0; i < this.hasAsked.size(); i++) {
				Vector3f key = this.hasAsked.get(i);
				if(key != null) {
					if((int)key.x == (int)pos.x && key.z == (int)pos.z) {
						result = key;
					}
				}
			}
		}
		return result;
	}

	public void saveWorld(String world) {
		List<ChunkSaveData> chunks = new ArrayList<>();

		for(Map.Entry<Vector3f, MasterChunk> entry : chunkMap.entrySet()) {
			MasterChunk master = entry.getValue();

			chunks.add(new ChunkSaveData(master.getOrigin(), master.getChunk().blocks));
		}

		SaveSystem.save(world, new SaveData(seed, chunks, Main.thePlayer));
		chunks.clear();
	}
	@SuppressWarnings("deprecation")
	public void saveWorldAndQuit(String world) {
		saveWorld(world);

		this.chunkCreator.interrupt();
		this.chunkCreator.stop();

		Main.inGameGUI = null;
		Main.thePlayer = null;
		Main.theWorld = null;
	}
	@SuppressWarnings("deprecation")
	public void quitWorld() {
		this.chunkCreator.interrupt();
		this.chunkCreator.stop();

		Main.inGameGUI = null;
		Main.thePlayer = null;
		Main.theWorld = null;
	}
	public static World loadWorld(String world) {
		SaveData data = SaveSystem.load(world);
		if(data != null) {
			Logger.log(LogLevel.WARN, "Loading world!");

			World w = new World(data.seed);

			for(ChunkSaveData s : data.chunks) {
				w.addChunk(new MasterChunk(new Vector3f(s.x,0,s.z), s.blocks));
			}

			w.handleLoad(data);

			w.startChunkCreator();

			return w;
		} else {
			Logger.log(LogLevel.WARN, "World couldn't be loaded!");
			return new World();
		}
	}
	
}
