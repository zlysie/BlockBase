package net.oikmo.engine.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.Loader;
import net.oikmo.engine.entity.Camera;
import net.oikmo.engine.entity.Entity;
import net.oikmo.engine.models.RawModel;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.engine.save.ChunkSaveData;
import net.oikmo.engine.save.SaveData;
import net.oikmo.engine.save.SaveSystem;
import net.oikmo.engine.textures.ModelTexture;
import net.oikmo.engine.world.chunk.MasterChunk;
import net.oikmo.main.Main;
import net.oikmo.toolbox.FastMath;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Logger.LogLevel;
import net.oikmo.toolbox.Maths;
import net.oikmo.toolbox.PerlinNoiseGenerator;

public class World {

	public static final int WORLD_HEIGHT = 128;
	public static final int WORLD_SIZE = 4*8;
	private ModelTexture tex;
	private long seed;
	private PerlinNoiseGenerator noiseGen;
	
	private List<Entity> entities = Collections.synchronizedList(new ArrayList<Entity>());
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
		this.tex = ModelTexture.create("textures/defaultPack");
		this.noiseGen = new PerlinNoiseGenerator(seed);
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
										masterChunks.add(new MasterChunk(noiseGen, chunkPos));
										Logger.log(LogLevel.INFO, "Creating chunk at x:"+ chunkX +" z:" + chunkZ + "!");
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
	
	public void update(Camera camera) {
		if(canCreateChunks) {
			synchronized(masterChunks) {
				for(MasterChunk master : masterChunks) {
					if(master.getEntity() == null) {
						if(master.getMesh() != null) {
							if(master.getMesh().hasMeshInfo()) {
								RawModel raw = Loader.getInstance().loadToVAO(master.getMesh().positions, master.getMesh().uvs);
								TexturedModel texModel = new TexturedModel(raw, tex);
								Entity entity = new Entity(texModel, master.getOrigin(), new Vector3f(0,0,0),1);
								master.setEntity(entity);
								entities.add(entity);
								master.getMesh().removeMeshInfo();
							}
						} else {
							if(isInValidRange(master.getOrigin())) {
								master.createMesh();
							}
						}
					}
				}
			}
		}
		
		for(int i = 0; i < entities.size(); i++) {
			Vector3f origin = entities.get(i).getPosition();
			
			if(isInValidRange(origin)) {
				MasterRenderer.getInstance().addEntity(entities.get(i));
			}
		}
		
		MasterRenderer.getInstance().render(camera);
	}
	
	public void saveWorld() {
		List<ChunkSaveData> chunks = new ArrayList<>();
		
		for(MasterChunk master : masterChunks) {
			chunks.add(new ChunkSaveData(master.getOrigin(), master.getChunk().blocks));
		}
		
		SaveSystem.save("world1", new SaveData(seed, chunks, Main.thePlayer));
		chunks.clear();
	}
	
	public void loadWorld() {
		if(canCreateChunks) {
			System.out.println("sabing!");
			Main.thePlayer.resetMotion();
			this.canCreateChunks = false;
			SaveData data = SaveSystem.load("world1");
			MasterChunk.clear();
			this.masterChunks.clear();
			this.entities.clear();
			for(ChunkSaveData s : data.chunks) {
				masterChunks.add(new MasterChunk(new Vector3f((int)s.x,0,(int)s.z), s.blocks));
			}
			
			new Thread(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(null, "World has loaded!");
				}
			}).start();
			this.noiseGen = null;
			this.noiseGen = new PerlinNoiseGenerator(data.seed);
			
			Main.thePlayer.setPosition(new Vector3f(data.x, data.y, data.z));
			Main.thePlayer.getCamera().setRotation(data.rotX, data.rotY, data.rotZ);
			this.canCreateChunks = true;
		}
	}

	public void refreshChunk(MasterChunk master) {
		if(master.getMesh() != null) {
			master.destroyMesh();
			this.entities.remove(master.getEntity());
			master.setEntity(null);
			master.createMesh();
		}
	}
	
	public void refreshChunks() {
		System.out.println("clearing!");
		this.entities.clear();
		
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
