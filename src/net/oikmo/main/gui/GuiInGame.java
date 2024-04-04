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
	private GuiText stupidThingToMakeAABBWork;
	
	public void onInit() {
		fps = new GuiText("FPS: " + DisplayManager.getFPSCount(), 1.1f, MasterRenderer.font, new Vector2f(0,0), 1, false, false);
		fps.setColour(1, 1, 1);
		fps.setEdge(0.2f);
		blockType = new GuiText("", 1.1f, MasterRenderer.font, new Vector2f(0,0.03f), 1, false, false);
		blockType.setColour(1, 1, 1);
		blockType.setEdge(0.2f);
		
		stupidThingToMakeAABBWork = new GuiText("", 1.1f, MasterRenderer.font, new Vector2f(0,1), 1, false, false);
	}
	
	public void onUpdate() {
		if(Main.thePlayer.getCurrentChunkPosition() != null) {
			stupidThingToMakeAABBWork.setTextString(Main.thePlayer.getCurrentChunk() + " " + Main.thePlayer.getCurrentChunkPosition().toString());
		}
		fps.setTextString("FPS: " + DisplayManager.getFPSCount());
		blockType.setTextString(Main.thePlayer.getCamera().getCurrentlySelectedBlock().getEnumType().name());
	}
}
