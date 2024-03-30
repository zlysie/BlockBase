package net.oikmo.engine.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.Loader;
import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.entity.Camera;
import net.oikmo.engine.entity.Entity;
import net.oikmo.engine.models.RawModel;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.engine.textures.ModelTexture;
import net.oikmo.engine.world.chunk.MasterChunk;
import net.oikmo.main.Main;
import net.oikmo.toolbox.FastMath;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Logger.LogLevel;
import net.oikmo.toolbox.Maths;
import prime.PerlinNoise;

public class World {

	public static final int WORLD_HEIGHT = 128;
	public static final int WORLD_SIZE = 6*8;
	
	private List<Entity> entities = Collections.synchronizedList(new ArrayList<Entity>());
	public List<MasterChunk> masterChunks = Collections.synchronizedList(new ArrayList<MasterChunk>());
	
	private PerlinNoise noiseGen;
	
	private ModelTexture tex;

	private boolean lockInRefresh = false;
	
	private Thread chunkCreator;
	
	public World(String seed) {
		tex = new ModelTexture(ResourceLoader.loadTexture("defaultPack"));
		this.noiseGen = new PerlinNoise((int)Maths.getSeedFromName(seed)*266, 1D,0.5D,2D, 7); 
		init();
	}
	
	public void init() {
		chunkCreator = new Thread(new Runnable() { 
			public void run() {
				while (!Main.displayRequest) {
					for (int x = (int) (Main.camPos.x - WORLD_SIZE) / 16; x < (Main.camPos.x + WORLD_SIZE) / 16; x++) {
						for (int z = (int) (Main.camPos.z - WORLD_SIZE) / 16; z < (Main.camPos.z + WORLD_SIZE) / 16; z++) {
							int chunkX = x * 16;
							int chunkZ = z * 16;
							Vector3f chunkPos = new Vector3f(chunkX, 0, chunkZ);
							synchronized(MasterChunk.usedPositions) {
								if(!MasterChunk.isPositionUsed(chunkPos)) {
									masterChunks.add(new MasterChunk(noiseGen, chunkPos));
									Logger.log(LogLevel.INFO, "Creating chunk!");
								}
							}
						}
					}
				}
			}
		});
		chunkCreator.setName("Chunk Creator");
		chunkCreator.start();
	}
	
	public void update(Camera camera) {
		if(Keyboard.isKeyDown(Keyboard.KEY_F)) {
			if(!lockInRefresh) {
				refreshChunks();
				lockInRefresh = true;
			}
		} else {
			lockInRefresh = false;
		}
		
		synchronized(masterChunks) {
			for(MasterChunk master : masterChunks) {
				if(master.getEntity() == null) {
					if(master.getMesh() != null) {
						if(master.getMesh().hasMeshInfo()) {
							RawModel model123 = Loader.getInstance().loadToVAO(master.getMesh().positions, master.getMesh().uvs);
							TexturedModel texModel123 = new TexturedModel(model123, tex);
							Entity entity = new Entity(texModel123, master.getOrigin(), new Vector3f(0,0,0),1);
							master.setEntity(entity);
							entities.add(entity);
							master.getMesh().removeMeshInfo();
						}
					}
				}
			}
		}
		

		for(int i = 0; i < entities.size(); i++) {
			Vector3f origin = entities.get(i).getPosition();

			int distX = (int) FastMath.abs(Main.camPos.x - origin.x);
			int distZ = (int) FastMath.abs(Main.camPos.z - origin.z);

			if((distX <= WORLD_SIZE) && (distZ <= WORLD_SIZE)) {
				MasterRenderer.getInstance().addEntity(entities.get(i));
			}
		}
		
		MasterRenderer.getInstance().render(camera);
	}
	
	public boolean isInValidDistance(Vector3f origin) {

		int distX = (int) FastMath.abs(Main.camPos.x - origin.x);
		int distZ = (int) FastMath.abs(Main.camPos.z - origin.z);

		if((distX <= WORLD_SIZE) && (distZ <= WORLD_SIZE)) {
			return true;
		}
		return false;
	}

	public void refreshChunk(MasterChunk master) {
		if(master.getMesh() != null) {
			master.destroyMesh();
			entities.remove(master.getEntity());
			master.setEntity(null);
			master.createMesh();
		}
	}
	
	/*public void refreshCertainChunk(Chunk chunk) {
		if(chunk.hasMesh && chunk.mesh != null) {
			chunk.hasMesh = false;
			entities.remove(chunk.mesh.entity);
			chunkMeshes.remove(chunk.mesh);
			chunk.mesh = null;
			chunkMeshes.add(new ChunkMesh(chunk));
		}
		
	}*/
	
	public void refreshChunks() {
		System.out.println("clearing!");
		entities.clear();
		
		for(MasterChunk master : masterChunks) {
			Vector3f origin = master.getOrigin();
			master.destroyMesh();
			master.setEntity(null);
			
			int distX = (int) FastMath.abs(Main.camPos.x - origin.x);
			int distZ = (int) FastMath.abs(Main.camPos.z - origin.z);
			
			if((distX <= WORLD_SIZE) && (distZ <= WORLD_SIZE)) {
				master.createMesh();
			}
		}
	}
	
	public void cleanUp() {
		
	}
}
