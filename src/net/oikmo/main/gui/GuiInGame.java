package net.oikmo.main.gui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.DisplayManager;
import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.gui.component.slick.button.GuiButton;
import net.oikmo.engine.gui.component.slick.button.GuiCommand;
import net.oikmo.engine.sound.SoundMaster;
import net.oikmo.main.Main;

public class GuiInGame extends GuiScreen {
	
	private boolean literallyUpdate = false;
	
	public GuiInGame() {
		super("In Game");
	}
	
	GuiButton button;
	
	public void onInit() {
		button = new GuiButton(Display.getWidth()/2, Display.getHeight()/2, 250, 60, "test!");
		button.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				Main.close();
			}
		});
	}
	
	public void onUpdate() {
		drawShadowString(0f, 0f, Main.gameVersion);
		if(literallyUpdate) {
			drawShadowString(0, fontSize, "FPS: " + DisplayManager.getFPSCount());
			Vector3f v = Main.thePlayer.getRoundedPosition();
			drawShadowString(0, Display.getHeight()-fontSize*2, "X: "+ (int)v.x + " Y: "+ (int)v.y + " Z: "+ (int)v.z +" ");
			drawShadowString(0, Display.getHeight()-fontSize, "Selected block: " + Main.thePlayer.getCamera().getCurrentlySelectedBlock().getEnumType().name());
		}
		button.tick();
		//button.setPosition(Mouse.getX(), Math.abs(Display.getHeight()-Mouse.getY()));
		drawImage(ResourceLoader.loadUITexture("ui/crosshair"),Display.getWidth()/2, Display.getHeight()/2,  20f, 20f);
	}

	public void onClose() {
		literallyUpdate = !literallyUpdate;
	}
}
