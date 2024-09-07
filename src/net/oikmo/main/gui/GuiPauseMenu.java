package net.oikmo.main.gui;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.gui.Gui;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.gui.component.slick.GuiCommand;
import net.oikmo.engine.gui.component.slick.button.GuiButton;
import net.oikmo.engine.network.OtherPlayer;
import net.oikmo.engine.sound.SoundMaster;
import net.oikmo.main.Main;

public class GuiPauseMenu extends GuiScreen {
	
	public GuiPauseMenu() {
		super("Pause Menu");
	}
	
	private GuiButton optionsButton;
	private GuiButton quitButton;
	private boolean dontLock = false;
	
	
	public void onInit() {
		if(Main.theNetwork != null) {
			if(Main.thePlayer != null) {
				Main.thePlayer.getCamera().setMouseLock(false);
			} else {
				Main.disconnect(false, Main.lang.translateKey("network.disconnect.u"));
			}
			
		} else {
			Main.thePlayer.getCamera().setMouseLock(false);
			Main.shouldTick = false;
		}
		
		optionsButton = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)-20, 200, 30, Main.lang.translateKey("options.title"));
		optionsButton.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				dontLock = true;
				prepareCleanUp();
				
				Main.currentScreen = new GuiOptions(false);
			}
			
			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)-20;
			}
		});
		
		quitButton = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)+20, 200, 30, Main.lang.translateKey("gui.quit"));
		quitButton.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				dontLock = true;
				prepareCleanUp();
				Main.inGameGUI = null;
				if(Main.theNetwork == null) {
					Main.theWorld.saveWorldAndQuit();
				} else {
					Main.theNetwork.disconnect();
					Main.theWorld.quitWorld();
					Main.theWorld = null;
					Main.theNetwork = null;
				}
				
				if(Main.theNetwork != null) {
					Main.thePlayer.getCamera().setMouseLock(false);
				} else {
					Main.shouldTick();
				}
				
				SoundMaster.stopMusic();
				Main.currentScreen = new GuiMainMenu(Main.getRandomSplash());
			}
			
			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)+20;
			}
		});
		
		
	}
	private int width = 200;
	public void onUpdate() {
		drawBackground(ResourceLoader.loadUITexture("ui/ui_background3"));
		
		drawShadowStringCentered(Display.getWidth()/2,fontSize*2,Main.lang.translateKey("pause.title"));
		
		if(Main.theNetwork != null) {
			int height = (fontSize*2)+5;
			boolean flag = false;
			
			for(OtherPlayer p : Main.theNetwork.players.values()) {
				if(p.userName != null) {
					if(p.userName.contentEquals(Main.theNetwork.player.userName)) {
						flag = true;
					}
				}
				height += fontSize + 5;
				if(flag && p.userName != null && Main.theNetwork.player.userName != null) {
					height -= fontSize + 5;
					
				}
			}
			
			int xPos = 100;
			int yPos = (Display.getHeight()/2)-height/2;
			
			this.drawSquareFilled(Color.gray, xPos, yPos, width, height);
			this.drawSquare(Color.lightGray, 2, xPos, yPos, width, height);
			drawShadowStringCentered(xPos+(width/2), yPos+10, Main.lang.translateKey("pause.players.title"));
			
			height = fontSize+10;
			if(!flag) {
				drawShadowStringCentered(xPos+(width/2), yPos+height, Main.theNetwork.player.userName + " ("+Main.lang.translateKey("pause.players.identifier")+")");
			}
			try {
				for(OtherPlayer p : Main.theNetwork.players.values()) {
					if(p.userName != null) {
						if(!p.userName.contentEquals(Main.theNetwork.player.userName)) {
							height += fontSize + 5;
							drawShadowStringCentered(xPos+(width/2), yPos+height, p.userName);
						} else {
							if(flag) {
								height += fontSize + 5;
								drawShadowStringCentered(xPos+(width/2), yPos+height, p.userName + " ("+Main.lang.translateKey("pause.players.identifier")+")");
							}
							
						}
						
					}
					
				}
			} catch(java.util.ConcurrentModificationException e) {}
			
		}
		
		this.optionsButton.tick();
		quitButton.tick();
	}
	
	public void onClose() {
		if(!dontLock) {
			if(Main.theNetwork != null) {
				if(Main.thePlayer != null) {
					Main.thePlayer.getCamera().setMouseLock(true);
				} else {
					Main.disconnect(false, Main.lang.translateKey("network.disconnect.u"));
				}
				
			} else {
				if(Main.thePlayer != null) {
					Main.thePlayer.getCamera().setMouseLock(true);
					Main.shouldTick = true;
				}
				
			}
		}
		this.optionsButton.onCleanUp();
		quitButton.onCleanUp();
		Gui.cleanUp();
	}
}
