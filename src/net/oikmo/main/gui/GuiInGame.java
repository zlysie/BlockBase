package net.oikmo.main.gui;

import org.lwjgl.util.vector.Vector2f;

import net.oikmo.engine.DisplayManager;
import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.gui.component.slider.GuiText;
import net.oikmo.engine.renderers.MasterRenderer;

public class GuiInGame extends GuiScreen {
	public GuiInGame() {
		super("In Game");
	}
	
	private int texture;
	private GuiText fps;
	
	public void onInit() {
		texture = ResourceLoader.loadTexture("dirtTex");
		fps = new GuiText("fps: ", 1.1f, MasterRenderer.font, new Vector2f(-1,0), 1, false, false);
		fps.setColour(1, 1, 1);
		fps.setEdge(0.2f);
	}
	
	public void onUpdate() {
		fps.setTextString("fps: " + DisplayManager.getFPSCount());
		fps.setPosition(1.4f-fps.getWidth(),0);
	}
}
