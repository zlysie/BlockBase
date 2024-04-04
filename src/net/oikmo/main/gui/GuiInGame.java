package net.oikmo.main.gui;

import org.lwjgl.util.vector.Vector2f;

import net.oikmo.engine.DisplayManager;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.gui.component.slider.GuiText;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.main.Main;

public class GuiInGame extends GuiScreen {
	public GuiInGame() {
		super("In Game");
	}
	private GuiText fps;
	private GuiText blockType;
	
	public void onInit() {
		fps = new GuiText("FPS: " + DisplayManager.getFPSCount(), 1.1f, MasterRenderer.font, new Vector2f(0,0), 1, false, false);
		fps.setColour(1, 1, 1);
		fps.setEdge(0.2f);
		blockType = new GuiText("", 1.1f, MasterRenderer.font, new Vector2f(0,0.03f), 1, false, false);
		blockType.setColour(1, 1, 1);
		blockType.setEdge(0.2f);
	}
	
	public void onUpdate() {
		fps.setTextString("FPS: " + DisplayManager.getFPSCount());
		blockType.setTextString(Main.thePlayer.getCamera().getCurrentlySelectedBlock().getEnumType().name());
	}
}
