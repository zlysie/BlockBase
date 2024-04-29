package net.oikmo.main.gui;

import org.lwjgl.opengl.Display;

import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.gui.Gui;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.gui.component.slick.GuiCommand;
import net.oikmo.engine.gui.component.slick.button.GuiButton;
import net.oikmo.main.Main;

public class GuiConnecting extends GuiScreen {
	
	public GuiConnecting() {
		super("Connecting");
	}
	
	private GuiButton quitButton;
	
	public void onInit() {
		if(Main.network != null) {
			Main.thePlayer.getCamera().setMouseLock(false);
		} else {
			Main.shouldTick();
		}
		
		quitButton = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)+40, 200, 30, "Quit");
		quitButton.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				prepareCleanUp();
				Main.network.disconnect();
				Main.disconnect(false, "");
				prepareCleanUp();
				Main.currentScreen = new GuiMainMenu(Main.getRandomSplash());
			}
			
			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)+40;
			}
		});
	}
	
	public void onUpdate() {
		drawTiledBackground(ResourceLoader.loadUITexture("dirtTex"), 48);
		drawShadowStringCentered(Display.getWidth()/2, (Display.getHeight()/2), "Connecting...");
		quitButton.tick();
		
		if(Main.thePlayer != null) {
			if(Main.thePlayer.tick) {
				prepareCleanUp();
				Main.thePlayer.getCamera().setMouseLock(true);
				Main.currentScreen = null;
			}
		}
		
	}
	
	public void onClose() {
		Gui.cleanUp();
	}
}
