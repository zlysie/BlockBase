package net.oikmo.main.gui;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.DisplayManager;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.gui.component.slider.GuiText;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.main.Main;

public class GuiInGame extends GuiScreen {
	public GuiInGame() {
		super("In Game");
	}
	private GuiText gameVersion;
	private GuiText fps;
	private GuiText blockType;
	private GuiText position;
	private boolean literallyUpdate = false;
	
	public void onInit() {
		gameVersion = new GuiText(Main.gameVersion, 1.1f, MasterRenderer.font, new Vector2f(0,0), 1, false, false);
		gameVersion.setOutlineColour(0, 0.6f, 0.1f);
		gameVersion.setOffset(0.1f,0.1f);
		gameVersion.setColour(1, 1, 1);
		gameVersion.setEdge(0.2f);
		
		fps = new GuiText("", 1.1f, MasterRenderer.font, new Vector2f(0,0.03f), 1, false, false);
		fps.setOutlineColour(0, 0.6f, 0.1f);
		fps.setOffset(0.1f,0.1f);
		fps.setColour(1, 1, 1);
		fps.setEdge(0.2f);
		position = new GuiText("", 1.1f, MasterRenderer.font, new Vector2f(0,1-0.06f), 1, false, false);
		position.setColour(1, 1, 1);
		position.setEdge(0.2f);
		blockType = new GuiText("", 1.1f, MasterRenderer.font, new Vector2f(0,1-0.03f), 1, false, false);
		blockType.setColour(1, 1, 1);
		blockType.setEdge(0.2f);
		
	}
	
	public void onUpdate() {
		if(literallyUpdate) {
			fps.setTextString("FPS: " + DisplayManager.getFPSCount());
			Vector3f v = Main.thePlayer.getRoundedPosition();
			position.setTextString("X: "+ (int)v.x + " Y: "+ (int)v.y + " Z: "+ (int)v.z +" ");
			blockType.setTextString("Selected block: " + Main.thePlayer.getCamera().getCurrentlySelectedBlock().getEnumType().name());
		}
	}
	
	public void onClose() {
		literallyUpdate = !literallyUpdate;
		fps.remove();
		position.remove();
		blockType.remove();
		if(literallyUpdate) {
			fps.setTextString("FPS: " + DisplayManager.getFPSCount() +" ");
			Vector3f v = Main.thePlayer.getRoundedPosition();
			position.setTextString("X: "+ (int)v.x + " Y: "+ (int)v.y + " Z: "+ (int)v.z +"e");
			blockType.setTextString("Selected block: " + Main.thePlayer.getCamera().getCurrentlySelectedBlock().getEnumType().name() +" ");
		}
	}
}
