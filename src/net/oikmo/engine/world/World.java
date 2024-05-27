package net.oikmo.engine.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import net.oikmo.engine.models.RawModel;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.nbt.NBTTagCompound;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.engine.renderers.chunk.ChunkEntity;
import net.oikmo.engine.sound.SoundMaster;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.engine.world.chunk.Chunk;
import net.oikmo.engine.world.chunk.ChunkLoader;
import net.oikmo.engine.world.chunk.MasterChunk;
import net.oikmo.engine.world.chunk.coordinate.ChunkCoordHelper;
import net.oikmo.engine.world.chunk.coordinate.ChunkCoordinates;
import net.oikmo.main.Main;
import net.oikmo.main.gui.GuiMainMenu;
import net.oikmo.network.shared.PacketRequestChunk;
import net.oikmo.toolbox.CompressedStreamTools;
import net.oikmo.toolbox.FastMath;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Logger.LogLevel;
import net.oikmo.toolbox.Maths;
import net.oikmo.toolbox.noise.OpenSimplexNoise;

public class World {

	public static final int WORLD_HEIGHT = 128;
	public static int RENDER_SIZE = 4;
	public static int WORLD_SIZE = RENDER_SIZE*8;

	public Map<ChunkCoordinates, MasterChunk> chunkMap = new HashMap<>();

	private List<MasterChunk> currentMasterChunks = Collections.synchronizedList(new ArrayList<MasterChunk>());
	private List<Entity> entities = new ArrayList<>();
	private List<ChunkCoordinates> hasAsked = new ArrayList<>();

	public ParticleEngine particleEngine;

	private long seed;
	private OpenSimplexNoise noise;

	private Thread chunkCreator;

	private File worldDir;
	private ChunkLoader provider;

	private long lockTimestamp;
	public long sizeOnDisk;
	public boolean superFlat = false;

	public static void updateRenderSize(int size) {
		RENDER_SIZE = size;
		WORLD_SIZE = RENDER_SIZE*8;
	}

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

	public void initLevelLoader(String world) {
		GuiMainMenu.stopMusic();
		SoundMaster.stopMusic();

		lockTimestamp = System.currentTimeMillis();
		worldDir = new File(Main.getWorkingDirectory()+"/saves/"+world+"/");
		worldDir.mkdirs();
		try {
			File lockFile = new File(worldDir, "session.lock");
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(lockFile));
			try {
				dos.writeLong(lockTimestamp);
			} finally {
				dos.close();
			}
		} catch(IOException ioexception) {
			ioexception.printStackTrace();
			throw new RuntimeException("Failed to check session lock, aborting");
		}
		File level = new File(worldDir, "level.dat");
		boolean isNewWorld = !level.exists();
		if(level.exists()) {
			try {
				NBTTagCompound baseTag = CompressedStreamTools.readCompressed(new FileInputStream(level));
				NBTTagCompound nbttagcompound1 = baseTag.getCompoundTag("Data");
				this.seed = nbttagcompound1.getLong("Seed");
				this.sizeOnDisk = nbttagcompound1.getLong("SizeOnDisk");
				this.noise = new OpenSimplexNoise(this.seed);
				this.superFlat = nbttagcompound1.getBoolean("Superflat");

				Main.thePlayer = new Player(new Vector3f(), new Vector3f());

				Main.thePlayer.tick = false;
				Main.thePlayer.readFromNBT(nbttagcompound1.getCompoundTag("Player"));

				//worldTime = nbttagcompound1.getLong("Time");
			} catch(Exception exception1) {
				exception1.printStackTrace();
			}
		} else {
			Main.thePlayer = new Player(new Vector3f(0, 120, 0), new Vector3f());
		}
		provider = new ChunkLoader(worldDir, !isNewWorld);
	}

	private void saveLevel() {
		checkSessionLock();
		NBTTagCompound base = new NBTTagCompound();
		base.setLong("Seed", seed);
		base.setLong("SizeOnDisk", sizeOnDisk);
		base.setBoolean("Superflat", superFlat);

		//nbttagcompound.setLong("Time", worldTime);*/
		base.setLong("LastPlayed", System.currentTimeMillis());
		/*EntityPlayer entityplayer = null;
		if(playerEntities.size() > 0)
		{
			entityplayer = (EntityPlayer)playerEntities.get(0);
		}*/
		if(Main.thePlayer != null) {
			NBTTagCompound playerCompound = new NBTTagCompound();
			Main.thePlayer.writeToNBT(playerCompound);
			base.setCompoundTag("Player", playerCompound);
		}
		NBTTagCompound dataTag = new NBTTagCompound();
		dataTag.setTag("Data", base);
		try {
			File newLevel = new File(worldDir, "level.dat_new");
			File oldLevel = new File(worldDir, "level.dat_old");
			File currentLevel = new File(worldDir, "level.dat");
			CompressedStreamTools.writeGzippedCompoundToOutputStream(dataTag, new FileOutputStream(newLevel));
			if(oldLevel.exists()) {
				oldLevel.delete();
			}
			currentLevel.renameTo(oldLevel);
			if(currentLevel.exists()) {
				currentLevel.delete();
			}
			newLevel.renameTo(currentLevel);
			if(newLevel.exists()) {
				newLevel.delete();
			}
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}

	public void checkSessionLock() {
		try {
			File file = new File(worldDir, "session.lock");
			DataInputStream datainputstream = new DataInputStream(new FileInputStream(file));
			try {
				if(datainputstream.readLong() != lockTimestamp) {
					Main.error("The save is being accessed from another location, aborting", new Exception());
				}
			} finally {
				datainputstream.close();
			}
		} catch(IOException ioexception) {
			Main.error("Failed to check session lock, aborting", new Exception());
		}
	}

	public void update(Camera camera) {
		currentMasterChunks.clear();
		for (int x = (int) (Main.camPos.x - WORLD_SIZE) / 16; x < (Main.camPos.x + WORLD_SIZE) / 16; x++) {
			for (int z = (int) (Main.camPos.z - WORLD_SIZE) / 16; z < (Main.camPos.z + WORLD_SIZE) / 16; z++) {
				int chunkX = x * 16;
				int chunkZ = z * 16;
				ChunkCoordinates chunkPos = ChunkCoordHelper.create(chunkX, chunkZ);

				synchronized(chunkMap) {
					if(chunkMap.get(chunkPos) != null) {
						MasterChunk master = getChunkFromPosition(chunkPos);
						if(master != null) {
							if(isInValidRange(master.getOrigin())) {
								if(Main.theNetwork != null) {
									master.timer = MasterChunk.networkMaxTime;
								} else {
									master.timer = MasterChunk.localMaxTime;
								}

								if(master.dirty) {
									if(master.getMesh() != null) {
										if(master.getMesh().hasMeshInfo()) {
											RawModel raw = Loader.loadToVAO(master.getMesh().positions, master.getMesh().uvs, master.getMesh().normals);
											TexturedModel texModel = new TexturedModel(raw, MasterRenderer.currentTexturePack);
											ChunkEntity entity = new ChunkEntity(texModel, master.getOrigin());
											master.getChunk().calcLightDepths(0, 0, Chunk.CHUNK_SIZE, Chunk.CHUNK_SIZE);
											master.setEntity(entity);
											if(master.getMesh() != null) {
												master.getMesh().removeMeshInfo();
											}
											master.destroyMesh();
											master.dirty = false;
										}
									} else {
										master.getChunk().calcLightDepths(0, 0, Chunk.CHUNK_SIZE, Chunk.CHUNK_SIZE);
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

		if(Main.theNetwork != null) {
			try {
				for(int m = 0; m < chunkMap.values().size(); m++) {
					MasterChunk master = (MasterChunk) chunkMap.values().toArray()[m];

					if(master != null) {
						if(!isInValidRange(master.getOrigin())) {
							if(master.timer > 0) {
								master.timer--;
							}
							if(master.timer <= 0) {
								chunkMap.remove(master.getOrigin());
								hasAsked.remove(master.getOrigin());
							}
						}
					}
				}
			} catch(java.util.ConcurrentModificationException e) {}
		} else {
			try {
				for(int m = 0; m < chunkMap.values().size(); m++) {
					MasterChunk master = (MasterChunk) chunkMap.values().toArray()[m];

					if(master != null) {
						if(!isInValidRange(master.getOrigin())) {
							if(master.timer > 0) {
								master.timer--;
							}
							if(master.timer <= 0) {
								provider.saveChunk(master);
								chunkMap.remove(master.getOrigin());
							}
						}
					}
				}
			} catch(java.util.ConcurrentModificationException e) {}
		}


		for(MasterChunk master : currentMasterChunks) {
			if(master.getEntity() != null) {
				MasterRenderer.getInstance().addChunkEntity(master.getEntity());

			}
		}

		for(Entity entity : entities) {
			MasterRenderer.getInstance().addEntity(entity);
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
						AABB other = new AABB(x, y, z, x+1f, y+1f, z+1f);
						surroundingAABBs.add(other);
					}

				}
			}
		}
		return surroundingAABBs;
	}

	public Vector3f raycast(Vector3f position, Vector3f direction, float distance, boolean isPlace) {
		float xPos = (float) Math.floor(position.x);
		float yPos = (float) Math.floor(position.y);
		float zPos = (float) Math.floor(position.z);

		if (direction.length() == 0)
			return null;

		direction = (Vector3f)direction.normalise();

		int stepX = Maths.signum(direction.x);
		int stepY = Maths.signum(direction.y);
		int stepZ = Maths.signum(direction.z);
		Vector3f tMax = new Vector3f(Maths.intbound(position.x, direction.x), Maths.intbound(position.y, direction.y), Maths.intbound(position.z, direction.z));
		Vector3f tDelta = new Vector3f((float)stepX / direction.x, (float)stepY / direction.y, (float)stepZ / direction.z);
		float faceX = 0;
		float faceY = 0;
		float faceZ = 0;

		do {
			if (getBlock((int)xPos, (int)yPos, (int)zPos) != null) {
				if (!isPlace) {
					return new Vector3f(xPos, yPos, zPos);
				} else {
					return new Vector3f((int)(xPos + faceX), (int)(yPos + faceY), (int)(zPos + faceZ));
				}
			}
			if (tMax.x < tMax.y) {
				if (tMax.x < tMax.z) {
					if (tMax.x > distance) break;

					xPos += stepX;
					tMax.x += tDelta.x;

					faceX = -stepX;
					faceY = 0;
					faceZ = 0;
				} else {
					if (tMax.z > distance) break;
					zPos += stepZ;
					tMax.z += tDelta.z;
					faceX = 0;
					faceY = 0;
					faceZ = -stepZ;
				}
			} else {
				if (tMax.y < tMax.z) {
					if (tMax.y > distance) break;
					yPos += stepY;
					tMax.y += tDelta.y;
					faceX = 0;
					faceY = -stepY;
					faceZ = 0;
				} else {
					if (tMax.z > distance) break;
					zPos += stepZ;
					tMax.z += tDelta.z;
					faceX = 0;
					faceY = 0;
					faceZ = -stepZ;
				}
			}
		} while (true);

		return null;
	}

	public Block getBlock(int xPos, int yPos, int zPos) {
		return getBlock(new Vector3f(xPos, yPos, zPos));
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


	public MasterChunk setBlockNoUpdate(Vector3f position, Block block) {
		ChunkCoordinates chunkPos = Maths.calculateChunkPosition(position);
		MasterChunk m = getChunkFromPosition(chunkPos);
		if(m != null) {
			m.setBlockNoUpdate(position, block);
			if(Main.theNetwork != null) {
				Main.theNetwork.updateChunk(position,block, false);
			}

		}
		return m;
	}
	public MasterChunk setBlockNoUpdateNoNet(Vector3f position, Block block) {
		ChunkCoordinates chunkPos = Maths.calculateChunkPosition(position);
		MasterChunk m = getChunkFromPosition(chunkPos);
		if(m != null) {
			m.setBlockNoUpdate(position, block);
		}
		return m;
	}
	public boolean setBlock(Vector3f position, Block block) {
		ChunkCoordinates chunkPos = Maths.calculateChunkPosition(position);
		MasterChunk m = getChunkFromPosition(chunkPos);
		if(m != null) {
			m.setBlock(position, block, true);
			if(Main.theNetwork != null) {
				Main.theNetwork.updateChunk(position,block, true);
			}
			return true;
		}
		return false;
	}
	public boolean setBlockNoNet(Vector3f position, Block block) {
		ChunkCoordinates chunkPos = Maths.calculateChunkPosition(position);
		MasterChunk m = getChunkFromPosition(chunkPos);
		if(m != null) {
			m.setBlock(position, block, false);
			return true;
		}
		return false;

	}
	public Block getBlock(Vector3f position) {
		ChunkCoordinates chunkPos = Maths.calculateChunkPosition(position);
		MasterChunk m = getChunkFromPosition(chunkPos);
		if(m != null) {
			return m.getBlock(position);
		}

		return null;
	}
	public Vector3f getBlockPositionFromCalculatedChunk(int x, int y, int z) {
		ChunkCoordinates chunkPos = Maths.calculateChunkPosition(new Vector3f(x,y,z));

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

	public boolean blockHasNeighbours(Vector3f position) {
		ChunkCoordinates chunkPos = Maths.calculateChunkPosition(position);
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
							if(m.getChunk().getBlock(x, y, z) != -1) {
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
						if(getBlock(holder) != null && getBlock(holder).getStrength() == Integer.MAX_VALUE) {
							continue;
						}
						MasterChunk m = setBlockNoUpdate(holder,null);
						if(!chunksToRefresh.contains(m)) {
							chunksToRefresh.add(m);
						}
					}
				}
			}
		}
		for(MasterChunk m : chunksToRefresh) {
			m.dirty = true;
			//setBlock(new Vector3f(m.getOrigin().x, -1, m.getOrigin().z),null);
		}
	}
	public void startChunkCreator() {
		this.chunkCreator = new Thread(new Runnable() { 
			public void run() {
				boolean debug = false;

				if(debug) {
					MasterChunk m = new MasterChunk(noise, ChunkCoordHelper.create(0,0));
					addChunk(m);
				} else {
					while (!Main.displayRequest) {
						if(Main.thePlayer != null && Main.thePlayer.getCurrentChunk() != null && !Main.thePlayer.tick) {
							Main.thePlayer.tick = true;
						}
						for (int x = (int) (Main.camPos.x - WORLD_SIZE) / 16; x < (Main.camPos.x + WORLD_SIZE) / 16; x++) {
							for (int z = (int) (Main.camPos.z - WORLD_SIZE) / 16; z < (Main.camPos.z + WORLD_SIZE) / 16; z++) {
								int chunkX = x * 16;
								int chunkZ = z * 16;

								ChunkCoordinates chunkPos = ChunkCoordHelper.create(chunkX, chunkZ);
								synchronized(chunkMap) {
									if(chunkMap.get(chunkPos) == null && getChunkFromPosition(chunkPos) == null) {
										if(provider.loadChunk(chunkX, chunkZ) != null) {
											MasterChunk m = provider.loadChunk(chunkX, chunkZ);
											addChunk(m);
										} else {
											//System.out.println(provider.loadChunk(chunkX, chunkZ) + " " + chunkX + " " + chunkZ);
											MasterChunk m = new MasterChunk(noise, chunkPos);
											addChunk(m);
										}
									}

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
	public void startChunkRetriever() {
		this.chunkCreator = new Thread(new Runnable() { 
			public void run() {
				while (!Main.displayRequest) {
					if(Main.thePlayer != null) {
						if(Main.thePlayer.getCurrentChunk() != null && !Main.thePlayer.tick) {
							Main.thePlayer.tick = true;
						}
					}
					
					for (int x = (int) (Main.camPos.x - WORLD_SIZE) / 16; x < (Main.camPos.x + WORLD_SIZE) / 16; x++) {
						for (int z = (int) (Main.camPos.z - WORLD_SIZE) / 16; z < (Main.camPos.z + WORLD_SIZE) / 16; z++) {
							int chunkX = x * 16;
							int chunkZ = z * 16;

							ChunkCoordinates chunkPos = ChunkCoordHelper.create(chunkX, chunkZ);
							synchronized(hasAsked) {
								if(!hasAsked.contains(chunkPos)) {
									hasAsked.add(chunkPos);
									System.out.println(chunkPos);
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
		chunkMap.put(m.getOrigin(), m);
	}

	public void refreshChunks() {
		Logger.log(LogLevel.INFO, "Clearing " + currentMasterChunks.size() + " chunks!");
		try {
			for(int i = 0; i < currentMasterChunks.size(); i++) {
				MasterChunk master = currentMasterChunks.get(i);
				if(master != null) {
					master.getChunk().calcLightDepths(0, 0, Chunk.CHUNK_SIZE, Chunk.CHUNK_SIZE);
					master.dirty = true;
				}
			}
		} catch(Exception e) {e.printStackTrace();}
	}

	public int getHeightFromPosition(Vector3f position) {
		ChunkCoordinates chunkPos = Maths.calculateChunkPosition(position);
		MasterChunk m = getChunkFromPosition(chunkPos);

		if(m != null) {
			return m.getChunk().getHeightFromPosition(chunkPos, position);
		}
		return -1;
	}

	public boolean isInValidRange(Vector3f origin) {
		return isInValidRange(1, origin.x, origin.z);
	}

	public boolean isInValidRange(ChunkCoordinates origin) {
		return isInValidRange(1, origin.x, origin.z);
	}

	public boolean isInValidRange(int size, ChunkCoordinates origin) {
		return isInValidRange(size, origin.x, origin.z);
	}


	public boolean isInValidRange(int size, float x, float z) {
		int distX = (int) FastMath.abs((Main.camPos.x) - x);
		int distZ = (int) FastMath.abs((Main.camPos.z) - z);

		//int renderSize = size*RENDER_SIZE;

		if((distX <= WORLD_SIZE*size) && (distZ <= WORLD_SIZE*size)) {
			return true;
		}

		return false;
	}

	public MasterChunk getChunkFromPosition(Vector3f position) {
		return getChunkFromPosition(ChunkCoordHelper.create((int)position.x, (int)position.z));
	}


	public MasterChunk getChunkFromPosition(ChunkCoordinates position) {
		return chunkMap.get(position);
	}

	public void saveWorld() {
		for(Map.Entry<ChunkCoordinates, MasterChunk> entry : chunkMap.entrySet()) {
			MasterChunk master = entry.getValue();
			//System.out.println(master.getOrigin());
			provider.saveChunk(master);
		}
		
		saveLevel();
	}

	public void saveWorldAndQuit() {
		saveWorld();
		quitWorld();
	}

	public void quitWorld() {
		if(this.chunkCreator != null) {
			this.chunkCreator.interrupt();
		}
		
		Main.inGameGUI = null;
		Main.thePlayer = null;
		ChunkCoordHelper.cleanUp();
		System.gc();
		Main.theWorld = null;

	}
	public static World loadWorld(String world) {
		Logger.log(LogLevel.WARN, "Loading world!");

		World w = null;
		w = new World();
		w.initLevelLoader(world);
		w.startChunkCreator();

		return w;
	}
}
