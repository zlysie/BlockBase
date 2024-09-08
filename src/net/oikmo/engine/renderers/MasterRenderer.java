package net.oikmo.engine.renderers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.entity.Camera;
import net.oikmo.engine.entity.Camera.TargetedAABB;
import net.oikmo.engine.entity.Entity;
import net.oikmo.engine.gui.Gui;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.renderers.chunk.ChunkEntity;
import net.oikmo.engine.renderers.chunk.ChunkRenderer;
import net.oikmo.engine.renderers.entity.EntityRenderer;
import net.oikmo.engine.renderers.skybox.SkyBoxRenderer;
import net.oikmo.engine.textures.ModelTexture;
import net.oikmo.main.Main;
import net.oikmo.main.gui.GuiMainMenu;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Logger.LogLevel;
import net.oikmo.toolbox.Maths;

public class MasterRenderer {
	
	private static MasterRenderer instance;
	public static MasterRenderer getInstance() {
		if(instance == null) {
			instance = new MasterRenderer();
		}
		return instance;
	}
	
	public float FOV = 60f;
	private final float NEAR_PLANE = 0.1f, FAR_PLANE = 10000f;
	
	private Matrix4f projectionMatrix;
	
	private SkyBoxRenderer skyboxRenderer;
	private EntityRenderer entityRenderer;
	private ChunkRenderer chunkRenderer;
	
	public static ModelTexture currentTexturePack;
	public static int defaultTexturePack;
	public static int customTexturePack;
	public static int particleTexture;
	public static int invisibleTexture;
	
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private Map<TexturedModel, List<ChunkEntity>> chunkEntities = new HashMap<TexturedModel, List<ChunkEntity>>();
	
	public MasterRenderer() {
		createProjectionMatrix();
		
		float offset = 0.2f;
		
		entityRenderer = new EntityRenderer(projectionMatrix, 0.4f+offset, 0.7f+offset, 1.0f+offset);
		chunkRenderer = new ChunkRenderer(projectionMatrix, 0.4f+offset, 0.7f+offset, 1.0f+offset);
		defaultTexturePack = ResourceLoader.loadTexture("textures/terrain");
		invisibleTexture = ResourceLoader.loadTexture("textures/invisible");
		
		File dir = new File(Main.getResources() + "/custom/textures/");
		if(!dir.exists()) {
			dir.mkdirs();
		} else {
			File customPack = new File(dir + "/terrain.png");
			if(customPack.exists()) {
				customTexturePack = ResourceLoader.loadCustomTexture("terrain");
			} else {
				customTexturePack = defaultTexturePack;
				Logger.log(LogLevel.WARN, "customPack.png was not found! Defaulting to defaultPack.png");
			}
		}
		
		File readme = new File(dir+"/README.TXT");
		if(!readme.exists()) {
			try {
				readme.createNewFile();
				try {
					BufferedImage example = ImageIO.read(MasterRenderer.class.getResourceAsStream("/assets/textures/terrain.png"));
					File examplePng = new File(dir+"/example_terrain.png");
					ImageIO.write(example, "png", examplePng);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				Logger.log(LogLevel.WARN, "Unable to create README at " + dir.getAbsolutePath());
			}
		}

		FileWriter fw = null;
		try {
			fw = new FileWriter(readme);
			fw.write("### --- Created at "+ Logger.getCurrentTime() +  " --- ###");
			fw.write("\r\nHello! This is the custom textures folder! In here you can put in custom textures that gets added to the game at start! (not during runtime.)");
			fw.write("\r\nPlease note that music files must be .png and have a specific name to be loaded (otherwise it is ignored)");
			fw.write("\r\nCustom textures = customPack.png (example is examplePack.png)");
			fw.write("\r\n- Oikmo :D");
			fw.close();
		} catch (IOException e) {
			Logger.log(LogLevel.WARN, "Unable to write into README at " + dir.getAbsolutePath());
		}
		particleTexture = ResourceLoader.loadTexture("textures/particles/blocks");
		currentTexturePack = new ModelTexture(defaultTexturePack);
		Gui.initFont();
	}
	
	public void setupSkybox() {
		skyboxRenderer = new SkyBoxRenderer((Main.jmode ? "jerma" : "panorama"), projectionMatrix);
	}
	
	public void setTexturePack(int texture) {
		currentTexturePack.setTextureID(texture);
	}
	
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClearColor(0.4f, 0.7f, 1.0f, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT );
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public void render(Camera camera) {
		prepare();
		
		if(Main.currentScreen instanceof GuiMainMenu) {
			skyboxRenderer.render(camera, projectionMatrix, 0.4f, 0.7f, 1.0f);
		}
		
		if(Main.thePlayer != null) {
			initGL();
			if(Main.thePlayer.getCamera().shouldRenderAABB() && Main.thePlayer != null) {
				MasterRenderer.getInstance().renderAABB(Main.thePlayer.getCamera().getAABB());
			}
		}
		
		entityRenderer.render(entities, camera);
		chunkRenderer.render(chunkEntities, camera);
		
		chunkEntities.clear();
		entities.clear();
	}
	
	FloatBuffer projectionBuffer = BufferUtils.createFloatBuffer(16);
	
	public void initGL() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadMatrix(projectionBuffer);
		glPerspective3(Main.thePlayer.getCamera().getPosition(), Main.thePlayer.getCamera().getRotation());
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
	}
	
	public void glPerspective3(Vector3f position, Vector3f rotation) {
		GL11.glRotatef(rotation.x, 1, 0, 0);
		GL11.glRotatef(rotation.y, 0, 1, 0);
		GL11.glRotatef(rotation.z, 0, 0, 1);
		GL11.glTranslatef(-position.x, -position.y, -position.z);
	}
	
	public void renderAABB(TargetedAABB ent) {	
		float wb2 = 0.501f, hb2 =  0.501f;
		GL11.glColor3f(0, 0, 0);
		GL11.glTranslatef(ent.getPosition().x+0.5f, ent.getPosition().y+0.5f, ent.getPosition().z+0.5f);
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex3f(-wb2, -hb2, -wb2);
		GL11.glVertex3f(-wb2, -hb2, wb2);
		GL11.glVertex3f(wb2, -hb2, wb2);
		GL11.glVertex3f(wb2, -hb2, -wb2);
		GL11.glVertex3f(-wb2, -hb2, -wb2);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3f(-wb2, -hb2, -wb2);
		GL11.glVertex3f(-wb2, hb2, -wb2);
		GL11.glVertex3f(wb2, -hb2, -wb2);
		GL11.glVertex3f(wb2, hb2, -wb2);
		GL11.glVertex3f(-wb2, -hb2, wb2);
		GL11.glVertex3f(-wb2, hb2, wb2);
		GL11.glVertex3f(wb2, -hb2, wb2);
		GL11.glVertex3f(wb2, hb2, wb2);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex3f(-wb2, hb2, -wb2);
		GL11.glVertex3f(-wb2, hb2, wb2);
		GL11.glVertex3f(wb2, hb2, wb2);
		GL11.glVertex3f(wb2, hb2, -wb2);
		GL11.glVertex3f(-wb2, hb2, -wb2);
		GL11.glEnd();
		GL11.glTranslatef(-(ent.getPosition().x+0.5f), -(ent.getPosition().y+0.5f), -(ent.getPosition().z+0.5f));
	}
	
	public void addEntity(Entity entity) {
		TexturedModel model = entity.getModel();
		
		List<Entity> batch = entities.get(model);
		
		if(batch != null) {
			if(!batch.contains(entity)) {
				batch.add(entity);
			}
		} else {
			List<Entity> newBatch = new ArrayList<>();
			newBatch.add(entity);
			entities.put(model, newBatch);
		}
	}
	
	public void addChunkEntity(ChunkEntity entity) {
		TexturedModel model = entity.getModel();
		
		List<ChunkEntity> batch = chunkEntities.get(model);
		
		if(batch != null) {
			if(!batch.contains(entity)) {
				batch.add(entity);
			}
		} else {
			List<ChunkEntity> newBatch = new ArrayList<>();
			newBatch.add(entity);
			chunkEntities.put(model, newBatch);
		}
	}
	
	public void createProjectionMatrix() {
		projectionMatrix = new Matrix4f();
		
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) (1f / Math.tan(Math.toRadians(FOV / 2f)));
		float x_scale = y_scale / aspectRatio;
		float zp = FAR_PLANE + NEAR_PLANE;
		float zm = FAR_PLANE - NEAR_PLANE;
		
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -zp/zm; //literally do not remove the minus* sign
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -(2 * FAR_PLANE * NEAR_PLANE) / zm;
		projectionMatrix.m33 = 0;
		Maths.matrixToBuffer(projectionMatrix, projectionBuffer);
	}
	
	public void updateProjectionMatrix() {
		createProjectionMatrix();
		entityRenderer.updateProjectionMatrix(projectionMatrix);
		chunkRenderer.updateProjectionMatrix(projectionMatrix);
	}
	
	public Matrix4f getProjectionMatrix() {
		return this.projectionMatrix;
	}
}
