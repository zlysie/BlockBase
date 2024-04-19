package net.oikmo.engine.world;

import java.util.ArrayList;
import java.util.Collections;
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
import net.oikmo.engine.models.RawModel;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.engine.save.ChunkSaveData;
import net.oikmo.engine.save.SaveData;
import net.oikmo.engine.save.SaveSystem;
import net.oikmo.engine.textures.ModelTexture;
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
	private ModelTexture tex;
	private long seed;
	private OpenSimplexNoise noise;

	private List<Entity> chunkEntities = Collections.synchronizedList(new ArrayList<Entity>());
	public List<Entity> entities = new ArrayList<Entity>();
	private List<MasterChunk> masterChunks = Collections.synchronizedList(new ArrayList<MasterChunk>());
	private Thread chunkCreator;

	private boolean canCreateChunks = true;

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
		this.tex = MasterRenderer.currentTexturePack;
		this.noise = new OpenSimplexNoise(seed);
		this.chunkCreator = new Thread(new Runnable() { 
			public void run() {
				while (!Main.displayRequest) {
					if(canCreateChunks) {
						for (int x = (int) (Main.camPos.x - WORLD_SIZE) / 16; x < (Main.camPos.x + WORLD_SIZE) / 16; x++) {
							for (int z = (int) (Main.camPos.z - WORLD_SIZE) / 16; z < (Main.camPos.z + WORLD_SIZE) / 16; z++) {
								int chunkX = x * 16;
								int chunkZ = z * 16;

								if(chunkX < 0 || chunkZ < 0) {
									//continue;
								}
								Vector3f chunkPos = new Vector3f(chunkX, 0, chunkZ);
								synchronized(MasterChunk.usedPositions) {
									if(!MasterChunk.isPositionUsed(chunkPos)) {
										masterChunks.add(new MasterChunk(noise, chunkPos));
									}
								}
							}
						}
					}
				}
			}
		});
		
		//this uh double checks in case a chunk has been occupied but literally hasn't been added here
		new Thread(new Runnable() { 
			public void run() {
				while (!Main.displayRequest) {
					if(canCreateChunks) {
						synchronized(MasterChunk.chunkMap) {
							try {
								for(Map.Entry<Vector3f, MasterChunk> entry : MasterChunk.chunkMap.entrySet()) {
									MasterChunk m = entry.getValue();

									if(!masterChunks.contains(m)) {
										masterChunks.add(m);
									}
								}
							} catch(java.util.ConcurrentModificationException e) {
								//humbly fuck off, i want chunks to load after player saves it :sob:
							}
						}
					}
				}
			}
		}).start();
		this.chunkCreator.setName("Chunk Creator");
		this.chunkCreator.start();
	}

	public void update(Camera camera) {
		if(canCreateChunks) {
			synchronized(masterChunks) {
				for(MasterChunk master : masterChunks) {
					if(master.getEntity() == null) {
						if(master.getMesh() != null) {
							if(master.getMesh().hasMeshInfo()) {
								RawModel raw = Loader.getInstance().loadToVAO(master.getMesh().positions, master.getMesh().uvs, master.getMesh().normals);
								TexturedModel texModel = new TexturedModel(raw, tex);
								Entity entity = new Entity(texModel, master.getOrigin(), new Vector3f(0,0,0),1);
								master.setEntity(entity);
								chunkEntities.add(entity);
								master.getMesh().removeMeshInfo();
							}
						} else {
							if(isInValidRange(master.getOrigin())) {
								master.createMesh();
							}
						}
					} else {
						if(!isInValidRange(master.getOrigin())) {
							master.destroyMesh();
						}
					}
				}
			}
		}

		for(Entity entity : chunkEntities) {
			if(isInValidRange(entity.getPosition())) {
				MasterRenderer.getInstance().addEntity(entity);
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
						if(entity instanceof ItemEntity) {
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
						} else {
							entity.tick();
						}
					}
				}
			}
		}
	}

	public Block getBlock(Vector3f position) {
		Vector3f chunkPos = new Vector3f();
		Maths.calculateChunkPosition(position, chunkPos);
		MasterChunk m = MasterChunk.getChunkFromPosition(chunkPos);
		if(m != null) {
			return m.getBlock(position);
		}

		return null;
	}

	public boolean anyBlockInSpecificLocation(int x, int y, int z) {
		return getBlock(new Vector3f(x,y,z)) != null;
	}

	public void setBlock(Vector3f position, Block block) {
		Vector3f chunkPos = new Vector3f();
		Maths.calculateChunkPosition(position, chunkPos);
		MasterChunk m = MasterChunk.getChunkFromPosition(chunkPos);
		if(m != null) {
			m.setBlock(position, block);
			refreshChunk(m);
		}
	}

	public boolean blockHasNeighbours(Vector3f position) {
		Vector3f chunkPos = new Vector3f();
		Maths.calculateChunkPosition(position, chunkPos);
		MasterChunk m = MasterChunk.getChunkFromPosition(chunkPos);

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

	public void saveWorld(String world) {
		List<ChunkSaveData> chunks = new ArrayList<>();

		for(MasterChunk master : masterChunks) {
			chunks.add(new ChunkSaveData(master.getOrigin(), master.getChunk().blocks));
		}

		SaveSystem.save(world, new SaveData(seed, chunks, Main.thePlayer));
		chunks.clear();
	}

	public void saveWorldAndQuit(String world) {
		List<ChunkSaveData> chunks = new ArrayList<>();

		for(MasterChunk master : masterChunks) {
			chunks.add(new ChunkSaveData(master.getOrigin(), master.getChunk().blocks));
		}

		SaveSystem.save(world, new SaveData(seed, chunks, Main.thePlayer));
		chunks.clear();

		Main.theWorld = null;
	}

	public void loadWorld(String world) {
		if(canCreateChunks && SaveSystem.load(world) != null) {
			this.canCreateChunks = false;



			SaveData data = SaveSystem.load(world);

			this.seed = data.seed;
			this.noise = null;
			this.noise = new OpenSimplexNoise(seed);

			if(Main.thePlayer == null) {
				Main.thePlayer = new Player(new Vector3f(data.x, data.y, data.z), new Vector3f(data.rotX, data.rotY, data.rotZ));
				entities.add(Main.thePlayer.getCamera().getSelectedBlock());
			}

			Main.thePlayer.resetMotion();

			MasterChunk.clear();
			this.masterChunks.clear();
			this.chunkEntities.clear();
			for(ChunkSaveData s : data.chunks) {
				masterChunks.add(new MasterChunk(new Vector3f((int)s.x,0,(int)s.z), s.blocks));
			}

			new Thread(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(null, "World has loaded!");
				}
			}).start();
			System.out.println(this.seed + " " + data.seed);

			System.out.println(this.seed + " " + data.seed);

			Vector3f v = Main.thePlayer.getPosition();

			v.y = getHeightFromPosition(Main.thePlayer.getRoundedPosition());
			if(v.y != -1) {
				v.y++;
				Main.thePlayer.setPosition(v);
			}

			//Main.thePlayer.setPosition(new Vector3f(data.x, data.y, data.z));
			//Main.thePlayer.getCamera().setRotation(data.rotX, data.rotY, data.rotZ);
			this.canCreateChunks = true;
		}
	}

	public int getHeightFromPosition(Vector3f position) {
		Vector3f chunkPos = new Vector3f();
		Maths.calculateChunkPosition(position, chunkPos);
		MasterChunk m = MasterChunk.getChunkFromPosition(chunkPos);

		if(m != null) {
			return m.getChunk().getHeightFromPosition(chunkPos, position);
		}
		return -1;
	}

	public void refreshChunk(MasterChunk master) {
		if(master.getMesh() != null) {
			master.destroyMesh();
			chunkEntities.remove(master.getEntity());
			master.setEntity(null);
			master.createMesh();
		}
	}

	public void refreshChunks() {
		Logger.log(LogLevel.INFO, "Clearing " + masterChunks.size() + " chunks!");
		this.chunkEntities.clear();

		for(MasterChunk master : masterChunks) {
			master.destroyMesh();
			master.setEntity(null);

			if(isInValidRange(master.getOrigin())){
				master.createMesh();
			}
		}
	}

	public boolean isInValidRange(Vector3f origin) {
		int distX = (int) FastMath.abs(Main.camPos.x - origin.x);
		int distZ = (int) FastMath.abs(Main.camPos.z - origin.z);

		if((distX <= WORLD_SIZE) && (distZ <= WORLD_SIZE)) {
			return true;
		}

		return false;
	}
}
