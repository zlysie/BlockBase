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
	private GuiText fps;
	private GuiText blockType;
	private GuiText position;
	private GuiText chunkblockpos;
	private boolean literallyUpdate = true;
	
	public void onInit() {
		fps = new GuiText("FPS: " + DisplayManager.getFPSCount(), 1.1f, MasterRenderer.font, new Vector2f(0,0), 1, false, false);
		fps.setColour(1, 1, 1);
		fps.setEdge(0.2f);
		position = new GuiText("", 1.1f, MasterRenderer.font, new Vector2f(0,0.03f), 1, false, false);
		position.setColour(1, 1, 1);
		position.setEdge(0.2f);
		blockType = new GuiText("", 1.1f, MasterRenderer.font, new Vector2f(0,0.06f), 1, false, false);
		blockType.setColour(1, 1, 1);
		blockType.setEdge(0.2f);
		chunkblockpos = new GuiText("", 1.1f, MasterRenderer.font, new Vector2f(0,0.09f), 1, false, false);
		chunkblockpos.setColour(1, 1, 1);
		chunkblockpos.setEdge(0.2f);
	}
	
	public void onUpdate() {
		if(literallyUpdate) {
			fps.setTextString("FPS: " + DisplayManager.getFPSCount());
			Vector3f v = Main.thePlayer.getRoundedPosition();
			Vector3f vv = Main.thePlayer.getCurrentChunkPosition();
			position.setTextString("X: "+ (int)v.x + " Y: "+ (int)v.y + " Z: "+ (int)v.z +" | " + "X: "+ (int)vv.x + " Y: "+ (int)vv.y + " Z: "+ (int)vv.z);
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
			position.setTextString("X: "+ (int)v.x + " Y: "+ (int)v.y + " Z: "+ (int)v.z +" ");
			blockType.setTextString("Selected block: " + Main.thePlayer.getCamera().getCurrentlySelectedBlock().getEnumType().name() +" ");
			
		}
	}
	
	public void updatechunkpos(int localX, int localZ) {
		chunkblockpos.setTextString("" + localX + " " + localZ);
	}
}
