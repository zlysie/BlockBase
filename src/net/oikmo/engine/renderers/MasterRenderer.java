package net.oikmo.engine.renderers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import net.oikmo.engine.entity.Camera;
import net.oikmo.engine.entity.Entity;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.renderers.entity.EntityRenderer;

public class MasterRenderer {
	
	private static MasterRenderer instance;
	public static MasterRenderer getInstance() {
		if(instance == null) {
			instance = new MasterRenderer();
		}
		return instance;
	}
	
	private float FOV = 60f;
	private final float NEAR_PLANE = 0.1f, FAR_PLANE = 10000f;
	
	private Matrix4f projectionMatrix;
	
	private EntityRenderer entityRenderer;
	
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	
	public MasterRenderer() {
		createProjectionMatrix();
		entityRenderer = new EntityRenderer(projectionMatrix);	
	}
	
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClearColor(0.4f, 0.7f, 1.0f, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}
	
	public void render(Camera camera) {
		prepare();
		entityRenderer.render(entities, camera);
		
		
		entities.clear();
	}
	
	public void addEntity(Entity entity) {
		TexturedModel model = entity.getModel();
		
		List<Entity> batch = entities.get(model);
		
		if(batch != null) {
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<>();
			newBatch.add(entity);
			entities.put(model, newBatch);
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
		projectionMatrix.m22 = -zp/zm; //literally do not remove the minux sign
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -(2 * FAR_PLANE * NEAR_PLANE) / zm;
		projectionMatrix.m33 = 0;
	}
}
