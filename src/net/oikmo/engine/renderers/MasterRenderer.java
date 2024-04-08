package net.oikmo.engine.renderers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.entity.Camera;
import net.oikmo.engine.entity.Entity;
import net.oikmo.engine.gui.font.meshcreator.FontType;
import net.oikmo.engine.gui.font.renderer.TextMaster;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.renderers.entity.EntityRenderer;
import net.oikmo.engine.renderers.gui.GuiRenderer;
import net.oikmo.engine.textures.GuiTexture;
import net.oikmo.engine.textures.ModelTexture;
import net.oikmo.main.Main;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Logger.LogLevel;

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
	private GuiRenderer guiRenderer;
	
	public static FontType font;
	public static ModelTexture currentTexturePack;
	public static int defaultTexturePack;
	public static int customTexturePack;
	public int ui_nuhuh;
	public int ui_button;
	public int ui_smallbutton;
	public int ui_hover;
	public int ui_smallhover;
	
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private List<GuiTexture> guis = new ArrayList<>();
	
	public MasterRenderer() {
		createProjectionMatrix();
		
		float offset = 0.2f;
		
		entityRenderer = new EntityRenderer(projectionMatrix, 0.4f+offset, 0.7f+offset, 1.0f+offset);
		guiRenderer = new GuiRenderer(projectionMatrix);
		font = new FontType("minecraft");
		defaultTexturePack = ResourceLoader.loadTexture("textures/defaultPack");
		
		File dir = new File(Main.getResources() + "/custom/textures/");
		if(!dir.exists()) {
			dir.mkdirs();
			
			
			
			
			
		} else {
			File customPack = new File(dir + "/customPack.png");
			if(customPack.exists()) {
				customTexturePack = ResourceLoader.loadCustomTexture("customPack");
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
					BufferedImage example = ImageIO.read(MasterRenderer.class.getResourceAsStream("/assets/textures/defaultPack.png"));
					File examplePng = new File(dir+"/examplePack.png");
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
		
		currentTexturePack = new ModelTexture(defaultTexturePack);
		ui_nuhuh = ResourceLoader.loadTexture("textures/ui/ui_nuhuh");
		ui_button = ResourceLoader.loadTexture("textures/ui/normal/ui_button");
		ui_hover = ResourceLoader.loadTexture("textures/ui/normal/ui_button_hover");
		ui_smallbutton = ResourceLoader.loadTexture("textures/ui/small/ui_button");
		ui_smallhover = ResourceLoader.loadTexture("textures/ui/small/ui_button_hover");
		TextMaster.init();
	}
	
	public void setTexturePack(int texture) {
		currentTexturePack.setTextureID(texture);
	}
	
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		//GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glClearColor(0.4f, 0.7f, 1.0f, 1);
		//GL11.glCullFace(GL11.GL_BACK);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT );
	}
	
	public void render(Camera camera) {
		prepare();
		entityRenderer.render(entities, camera);
		guiRenderer.render(guis);
		TextMaster.render();
		entities.clear();
		guis.clear();
	}
	
	public List<GuiTexture> getGUIList() {
		return guis;
	}
	
	public void addToGUIs(GuiTexture texture) {
		if(!guis.contains(texture)) {
			guis.add(texture);
		}
	}
	
	public void removeFromGUIs(GuiTexture texture) {
		if(guis.contains(texture)) {
			guis.remove(texture);
		}
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
	}
	
	public void updateProjectionMatrix() {
		createProjectionMatrix();
		entityRenderer.updateProjectionMatrix(projectionMatrix);
	}
	
	public Matrix4f getProjectionMatrix() {
		return this.projectionMatrix;
	}
}
