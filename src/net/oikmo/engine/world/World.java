package net.oikmo.engine.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JOptionPane;

import org.lwjgl.util.vector.Vector3f;

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
import net.oikmo.engine.world.chunk.MasterChunk;
import net.oikmo.main.Main;
import net.oikmo.toolbox.FastMath;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Logger.LogLevel;
import net.oikmo.toolbox.Maths;
import net.oikmo.toolbox.noise.OpenSimplexNoise;

public class World {

	public static final int WORLD_HEIGHT = 128;
	public static final int WORLD_SIZE = 4*8;

	public Map<Vector3f, MasterChunk> chunkMap = new HashMap<>();
	public List<Vector3f> usedPositions = new ArrayList<>();
	
	private List<MasterChunk> currentMasterChunks = Collections.synchronizedList(new ArrayList<MasterChunk>());
	private List<Entity> entities = new ArrayList<>();
	
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
						
						if(isInValidRange(master.getOrigin())) {
							if(master.getEntity() == null) {
								if(master.getMesh() != null) {
									if(master.getMesh().hasMeshInfo()) {
										RawModel raw = Loader.getInstance().loadToVAO(master.getMesh().positions, master.getMesh().uvs, master.getMesh().normals);
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
						
						if(master != null) {
							if(!currentMasterChunks.contains(master)) {
								currentMasterChunks.add(master);
							}
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
	}
	public void tick() {
		if(Main.thePlayer != null) {
			Main.thePlayer.tick();
		}

		ItemEntity.updateOscillation();
		synchronized(entities) {
			for(int i = 0; i < entities.size(); i++) {
				Entity entity = entities.get(i);
				if(entity != null) {
					if(isInValidRange(entity.getPosition())) {
						entity.tick();
					}
				}
			}
		}
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
	
	public boolean setBlock(Vector3f position, Block block) {
		Vector3f chunkPos = new Vector3f();
		Maths.calculateChunkPosition(position, chunkPos);
		MasterChunk m = getChunkFromPosition(chunkPos);
		if(m != null) {
			m.setBlock(position, block);
			if(Main.network != null) {
				Main.network.updateChunk(m);
			}
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
		if(master.getMesh() != null) {
			master.getMesh().removeMeshInfo();
			master.destroyMesh();
			master.setEntity(null);
			master.createMesh();
		}
	}
	public void refreshChunks() {
		Logger.log(LogLevel.INFO, "Clearing " + currentMasterChunks.size() + " chunks!");
		for(MasterChunk master : currentMasterChunks) {
			if(master.getMesh() != null) {
				master.getMesh().removeMeshInfo();
				master.destroyMesh();
				master.setEntity(null);
				master.createMesh();
			}
			
		}
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
				if(usedPositions.get(i) != null) {
					if((int)usedPositions.get(i).x == (int)pos.x && (int)usedPositions.get(i).z == (int)pos.z) {
						result = usedPositions.get(i);
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
	public static World loadWorld(String world) {
		SaveData data = SaveSystem.load(world);
		if(data != null) {
			Logger.log(LogLevel.WARN, "Loading world!");
			
			World w = new World(data.seed);
			
			for(ChunkSaveData s : data.chunks) {
				w.addChunk(new MasterChunk(new Vector3f(s.x,0,s.z), s.blocks));
			}
			
			w.handleLoad(data);
			
			new Thread(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(null, "World has loaded!");
				}
			}).start();
			
			w.startChunkCreator();
			
			return w;
		} else {
			Logger.log(LogLevel.WARN, "World couldn't be loaded!");
			return new World();
		}
	}
}
