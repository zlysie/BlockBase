package net.oikmo.main.gui;

import org.lwjgl.opengl.Display;

import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.gui.Gui;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.gui.component.slick.GuiCommand;
import net.oikmo.engine.gui.component.slick.button.GuiButton;
import net.oikmo.engine.sound.SoundMaster;
import net.oikmo.main.Main;

public class GuiPauseMenu extends GuiScreen {
	
	public GuiPauseMenu() {
		super("Pause Menu");
	}
	
	private GuiButton quitButton;
	
	public void onInit() {
		if(Main.network != null) {
			Main.thePlayer.getCamera().setMouseLock(false);
		} else {
			Main.shouldTick();
		}
		
		
		quitButton = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2), 200, 30, "Quit world...");
		quitButton.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				Main.inGameGUI = null;
				if(Main.network == null) {
					Main.theWorld.saveWorldAndQuit(Main.currentlyPlayingWorld);
				} else {
					Main.network.disconnect();
					Main.network = null;
				}
				
				
				if(Main.network != null) {
					Main.thePlayer.getCamera().setMouseLock(false);
				} else {
					Main.shouldTick();
				}
				prepareCleanUp();
				SoundMaster.stopMusic();
				Main.currentScreen = new GuiMainMenu(Main.getRandomSplash());
			}
			
			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = Display.getHeight()/2;
			}
		});
	}
	
	public void onUpdate() {
		drawBackground(ResourceLoader.loadUITexture("ui/ui_background3"));
		quitButton.tick();
	}
	
	public void onClose() {
		if(Main.network != null) {
			Main.thePlayer.getCamera().setMouseLock(true);
		} else {
			Main.shouldTick();
		}
		
		Gui.cleanUp();
	}
}
