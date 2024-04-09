package net.oikmo.main.gui;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.DisplayManager;
import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.main.Main;

public class GuiInGame extends GuiScreen {
	
	private boolean literallyUpdate = false;
	
	public GuiInGame() {
		super("In Game");
	}
	
	public void onUpdate() {
		drawShadowString(0f, 0f, Main.gameVersion);
		if(literallyUpdate) {
			drawShadowString(0, 14f, "FPS: " + DisplayManager.getFPSCount());
			Vector3f v = Main.thePlayer.getRoundedPosition();
			drawShadowString(0, Display.getHeight()-28f, "X: "+ (int)v.x + " Y: "+ (int)v.y + " Z: "+ (int)v.z +" ");
			drawShadowString(0, Display.getHeight()-14f, "Selected block: " + Main.thePlayer.getCamera().getCurrentlySelectedBlock().getEnumType().name());
		}
		
		drawImage(ResourceLoader.loadUITexture("ui/crosshair"),Display.getWidth()/2, Display.getHeight()/2,  20f, 20f);
	}

	public void onClose() {
		literallyUpdate = !literallyUpdate;
	}
}
