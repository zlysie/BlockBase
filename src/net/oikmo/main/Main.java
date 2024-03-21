package net.oikmo.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.DisplayManager;
import net.oikmo.engine.Loader;
import net.oikmo.engine.chunk.Chunk;
import net.oikmo.engine.chunk.ChunkMesh;
import net.oikmo.engine.entity.Camera;
import net.oikmo.engine.entity.Entity;
import net.oikmo.engine.models.RawModel;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.engine.textures.ModelTexture;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.os.EnumOS;
import net.oikmo.toolbox.os.EnumOSMappingHelper;

public class Main {

	public static int WIDTH = 854;
	public static int HEIGHT = 640;	

	public static final int WORLD_HEIGHT = 128;

	private static List<Entity> entities = Collections.synchronizedList(new ArrayList<Entity>());
	public static List<Chunk> chunks = Collections.synchronizedList(new ArrayList<Chunk>());
	private static List<ChunkMesh> chunkMeshes = new ArrayList<ChunkMesh>();
	public static List<Vector3f> usedPos = new ArrayList<>();
	private static Vector3f camPos = new Vector3f(0,0,0);

	private static final int WORLD_SIZE = 15;
	private static int index = 0;
	public static void main(String[] args) {
		DisplayManager.createDisplay(WIDTH, HEIGHT);
		MasterRenderer r = MasterRenderer.getInstance();

		Loader loader = Loader.getInstance();

		ModelTexture tex = new ModelTexture(loader.loadTexture("defaultPack"));
		
		Camera camera = new Camera(new Vector3f(0,10,0), new Vector3f(0,0,0));

		//chunk render radius
		new Thread(new Runnable() { 
			public void run() {
				while (!Display.isCloseRequested()) {
		            for (int x = (int) (camPos.x - WORLD_SIZE) / 16; x < (camPos.x + WORLD_SIZE) / 16; x++) {
		                for (int z = (int) (camPos.z - WORLD_SIZE) / 16; z < (camPos.z + WORLD_SIZE) / 16; z++) {
		                    int chunkX = x * 16;
		                    int chunkZ = z * 16;

		                    Vector3f chunkPos = new Vector3f(chunkX, 0, chunkZ);
		                    if (!usedPos.contains(chunkPos)) {
		                        Chunk chunk = new Chunk(chunkPos);
		                        chunks.add(chunk);
		                        ChunkMesh mesh = new ChunkMesh(chunk);
		                        chunkMeshes.add(mesh);
		                        usedPos.add(chunkPos);
		                    }
		                }
		            }
				}
			}
		}).start();
		
		while(!Display.isCloseRequested()) {
			camera.update();

			camPos = new Vector3f(camera.getPosition());

			if(index < chunkMeshes.size()) {
				
				
				RawModel model123 = loader.loadToVAO(chunkMeshes.get(index).positions, chunkMeshes.get(index).uvs);
				TexturedModel texModel123 = new TexturedModel(model123, tex);
				Entity entity = new Entity(texModel123, chunkMeshes.get(index).chunk.origin, new Vector3f(0,0,0),1);
				
				entities.add(entity);
				
				chunkMeshes.get(index).positions = null;
				chunkMeshes.get(index).uvs = null;
				chunkMeshes.get(index).normals = null;
			
				index++;
			}
			
			for(int i = 0; i < entities.size(); i++) {
				Vector3f origin = entities.get(i).getPosition();
				
				int distX = (int) (camPos.x - origin.x);
				int distZ = (int) (camPos.z - origin.z);
				
				if(distX < 0) {
					distX = -distX;
				}
				
				if(distZ < 0) {
					distZ = -distZ;
				}
				
				
				
				if((distX <= WORLD_SIZE) && (distZ <= WORLD_SIZE)) {
					r.addEntity(entities.get(i));
				}
				
				
			}
			
			if(Keyboard.isKeyDown(Keyboard.KEY_F)) {
				refreshChunks();
			}

			r.render(camera);

			DisplayManager.updateDisplay();
		}
		DisplayManager.closeDisplay();
	}

	public static void refreshChunks() {
		entities.clear();
		chunkMeshes.clear();
		for(int i = 0; i < chunks.size(); i++) {
			chunkMeshes.add(new ChunkMesh(chunks.get(i)));
		}
		index = 0;
	}
	
	/**
	 * Retrieves data directory of .pepdog/ using {@code Main.getAppDir(String)}
	 * @return Directory (File)
	 */
	public static File getDir() {
		return getAppDir("blockbase");
	}

	/**
	 * Uses {@code Main.getOS} to locate an APPDATA directory in the system.
	 * Then it creates a new directory based on the given name e.g <b>.name/</b>
	 * 
	 * @param name (String)
	 * @return Directory (File)
	 */
	public static File getAppDir(String name) {
		String userDir = System.getProperty("user.home", ".");
		File folder;
		switch(EnumOSMappingHelper.os[EnumOS.getOS().ordinal()]) {
		case 1:
		case 2:
			folder = new File(userDir, '.' + name + '/');
			break;
		case 3:
			String appdataLocation = System.getenv("APPDATA");
			if(appdataLocation != null) {
				folder = new File(appdataLocation, "." + name + '/');
			} else {
				folder = new File(userDir, '.' + name + '/');
			}
			break;
		case 4:
			folder = new File(userDir, "Library/Application Support/" + name);
			break;
		default:
			folder = new File(userDir, name + '/');
		}

		if(!folder.exists() && !folder.mkdirs()) {
			throw new RuntimeException("The working directory could not be created: " + folder);
		} else {
			return folder;
		}
	}

	public void cleanUp() {
		Loader.cleanUp();
		DisplayManager.closeDisplay();
		Logger.saveLog();
		System.exit(0);
	}
}
