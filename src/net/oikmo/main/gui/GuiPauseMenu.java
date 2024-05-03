package net.oikmo.main.gui;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.gui.Gui;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.gui.component.slick.GuiCommand;
import net.oikmo.engine.gui.component.slick.button.GuiButton;
import net.oikmo.engine.sound.SoundMaster;
import net.oikmo.main.Main;
import net.oikmo.network.client.OtherPlayer;

public class GuiPauseMenu extends GuiScreen {
	
	public GuiPauseMenu() {
		super("Pause Menu");
	}
	
	private GuiButton quitButton;
	
	public void onInit() {
		if(Main.network != null) {
			if(Main.thePlayer != null) {
				Main.thePlayer.getCamera().setMouseLock(false);
			} else {
				Main.disconnect(false, "Unknown");
			}
			
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
	private int width = 200;
	public void onUpdate() {
		drawBackground(ResourceLoader.loadUITexture("ui/ui_background3"));
		
		if(Main.network != null) {
			int height =  (fontSize *2)+5;
			boolean flag = false;
			
			for(OtherPlayer p : Main.network.players.values()) {
				if(p.userName != null) {
					if(p.userName.contentEquals(Main.network.player.userName)) {
						flag = true;
					}
				}
				height += fontSize + 5;
				if(flag && p.userName != null && Main.network.player.userName != null) {
					if(p.userName.contentEquals(Main.network.player.userName)) {
						height -= fontSize + 5;
					}
					
				}
			}
			
			int xPos = 100;
			int yPos = (Display.getHeight()/2)-height/2;
			
			this.drawSquareFilled(Color.gray, xPos, yPos, width, height);
			this.drawSquare(Color.lightGray, 2, xPos, yPos, width, height);
			drawShadowStringCentered(xPos+(width/2), yPos+10, "Players");	
			height = fontSize+10;
			if(flag) {
				drawShadowStringCentered(xPos+(width/2), yPos+height, Main.network.player.userName + " (You)");
			}
			for(OtherPlayer p : Main.network.players.values()) {
				if(p.userName != null) {
					
					if(!p.userName.contentEquals(Main.network.player.userName)) {
						height += fontSize + 5;
						drawShadowStringCentered(xPos+(width/2), yPos+height, p.userName);
					} else {
						if(!flag) {
							height += fontSize + 5;
							drawShadowStringCentered(xPos+(width/2), yPos+height, p.userName + " (You)");
						}
						
					}
					
				}
				
			}
		}
		
		
		quitButton.tick();
	}
	
	public void onClose() {
		if(Main.network != null) {
			if(Main.thePlayer != null) {
				Main.thePlayer.getCamera().setMouseLock(true);
			}
		} else {
			Main.shouldTick();
		}
		quitButton.onCleanUp();
		Gui.cleanUp();
	}
}
