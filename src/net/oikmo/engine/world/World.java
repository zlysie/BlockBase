package net.oikmo.engine.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.DisplayManager;
import net.oikmo.engine.Loader;
import net.oikmo.engine.entity.Camera;
import net.oikmo.engine.entity.Entity;
import net.oikmo.engine.models.RawModel;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.engine.textures.ModelTexture;
import net.oikmo.engine.world.chunk.Chunk;
import net.oikmo.engine.world.chunk.ChunkMesh;
import net.oikmo.main.Main;
import net.oikmo.toolbox.FastMath;

public class World {

	public static final int WORLD_HEIGHT = 128;
	public static final int WORLD_SIZE = 16;
	public static String seed = null;
	
	public  List<Chunk> chunks = Collections.synchronizedList(new ArrayList<Chunk>());
	private List<ChunkMesh> chunkMeshes = new ArrayList<ChunkMesh>();
	private List<Entity> entities = Collections.synchronizedList(new ArrayList<Entity>());
	public List<Vector3f> usedPos = new ArrayList<>();
	
	private ModelTexture tex;
	
	private int index = 0;
	private float timer = 0;

	private boolean lockInRefresh = false;
	
	
	
	public World(String seed) {
		tex = new ModelTexture(Loader.getInstance().loadTexture("defaultPack"));
		this.seed = seed;
		init();
	}
	
	public void init() {
		new Thread(new Runnable() { 
			public void run() {
				while (!Main.displayRequest) {
					for (int x = (int) (Main.camPos.x - WORLD_SIZE) / 16; x < (Main.camPos.x + WORLD_SIZE) / 16; x++) {
						for (int z = (int) (Main.camPos.z - WORLD_SIZE) / 16; z < (Main.camPos.z + WORLD_SIZE) / 16; z++) {
							int chunkX = x * 16;
							int chunkZ = z * 16;

							Vector3f chunkPos = new Vector3f(chunkX, 0, chunkZ);
							if (!usedPos.contains(chunkPos)) {
								Chunk chunk = new Chunk(chunkPos, seed);
								chunks.add(chunk);
								chunkMeshes.add(new ChunkMesh(chunk));
								usedPos.add(chunkPos);
							}
							
							for(Chunk chunk : chunks) {
								if(!chunk.hasMesh) {
									chunkMeshes.add(new ChunkMesh(chunk));
									chunk.hasMesh = true;
								}
							}
						}
					}
				}
			}
		}).start();
	}
	
	public void update(Camera camera) {

		timer += DisplayManager.getFrameTimeSeconds();
		//System.out.println(timer);
		if((timer % 2) == 0) {
			if(!lockInRefresh) {
				System.out.println(timer);
				refreshChunks();
				lockInRefresh = true;
			}

		} else {
			if(lockInRefresh) {
				lockInRefresh = false;
			}
		}
		
		synchronized(chunks) {
			for(Chunk chunk : chunks) {
				if(chunk.hasMesh && chunk.mesh != null) {
					if(chunk.mesh.positions != null && chunk.mesh.uvs != null && chunk.mesh.normals != null) {
						RawModel model123 = Loader.getInstance().loadToVAO(chunk.mesh.positions, chunk.mesh.uvs);
						TexturedModel texModel123 = new TexturedModel(model123, tex);
						Entity entity = new Entity(texModel123, chunk.origin, new Vector3f(0,0,0),1);
						chunk.mesh.entity = entity;
						entities.add(entity);

						chunk.mesh.positions = null;
						chunk.mesh.uvs = null;
						chunk.mesh.normals = null;
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

	
	public void refreshCertainChunk(Chunk chunk) {
		if(chunk.hasMesh && chunk.mesh != null) {
			chunk.hasMesh = false;
			entities.remove(chunk.mesh.entity);
			chunkMeshes.remove(chunk.mesh);
			chunk.mesh = null;
			chunkMeshes.add(new ChunkMesh(chunk));
		}
		
	}
	
	public void refreshChunks() {
		for(Chunk chunk : chunks) {
			chunk.hasMesh = false;
			chunk.mesh = null;
		}
		entities.clear();
		chunkMeshes.clear();
		for(int i = 0; i < chunks.size(); i++) {
			Vector3f origin = chunks.get(i).origin;

			int distX = (int) FastMath.abs(Main.camPos.x - origin.x);
			int distZ = (int) FastMath.abs(Main.camPos.z - origin.z);
			
			if((distX <= WORLD_SIZE) && (distZ <= WORLD_SIZE)) {
				chunkMeshes.add(new ChunkMesh(chunks.get(i)));
				chunks.get(i).hasMesh = true;
			}

		}
		index = 0;
	}
}
