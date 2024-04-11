package net.oikmo.main.gui;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.DisplayManager;
import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.gui.component.slick.GuiCommand;
import net.oikmo.engine.gui.component.slick.button.GuiButton;
import net.oikmo.engine.gui.component.slick.slider.GuiSlider;
import net.oikmo.main.Main;

public class GuiInGame extends GuiScreen {
	
	private boolean literallyUpdate = false;
	
	public GuiInGame() {
		super("In Game");
	}
	
	GuiButton quitButton;
	GuiSlider testSlider;
	
	public void onInit() {
		quitButton = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)+30, 200, 30, "Quit game");
		quitButton.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				Main.close();
			}
			
			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = Display.getHeight()/2 + 30;
			}
		});
		
		testSlider = new GuiSlider(Display.getWidth()/2, (Display.getHeight()/2)-30, 200, 30, "Test slider");
		testSlider.setGuiCommand(new GuiCommand() {
			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = Display.getHeight()/2 - 30;
			}
		});
	}
	
	public void onUpdate() {
		drawImage(ResourceLoader.loadUITexture("ui/crosshair"),Display.getWidth()/2, Display.getHeight()/2,  20f, 20f);
		drawShadowString(0f, 0f, Main.gameVersion);
		if(literallyUpdate) {
			drawShadowString(0, fontSize, "FPS: " + DisplayManager.getFPSCount());
			Vector3f v = Main.thePlayer.getRoundedPosition();
			drawShadowString(0, Display.getHeight()-fontSize*2, "X: "+ (int)v.x + " Y: "+ (int)v.y + " Z: "+ (int)v.z +" ");
			drawShadowString(0, Display.getHeight()-fontSize, "Selected block: " + Main.thePlayer.getCamera().getCurrentlySelectedBlock().getEnumType().name());
		}
		
		if(Main.isPaused()) {
			quitButton.tick();
			testSlider.tick();
		}
	}

	public void onClose() {
		if(!Main.isPaused()) {
			literallyUpdate = !literallyUpdate;
		}
		
	}
}
