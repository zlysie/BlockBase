package net.oikmo.main.gui;

import java.net.InetAddress;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.entity.Player;
import net.oikmo.engine.gui.Gui;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.gui.component.slick.GuiCommand;
import net.oikmo.engine.gui.component.slick.button.GuiButton;
import net.oikmo.engine.gui.component.slick.textfield.GuiTextField;
import net.oikmo.engine.sound.SoundMaster;
import net.oikmo.main.Main;
import net.oikmo.network.client.NetworkHandler;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Logger.LogLevel;

public class GuiMultiplayer extends GuiScreen {
	
	public GuiMultiplayer() {
		super("Multiplayer");
	}
	
	private GuiTextField serverAddress;
	private GuiButton joinButton;
	private GuiButton quitButton;
	private String errorMsg;
	private boolean errorPopup;
	
	public void onInit() {
		serverAddress = new GuiTextField("IP of server here...",Display.getWidth()/2, (Display.getHeight()/2)-15, 200, 30);
		serverAddress.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {}
			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)-15;
			}
		});
		
		joinButton = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)+25, 200, 30, "Join world...");
		joinButton.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				NetworkHandler testNetwork = null;
				boolean hasErrored = false;
				try {
					if(serverAddress.hasContent() ) {
						InetAddress.getByName(serverAddress.getInputText());
						testNetwork = new NetworkHandler(serverAddress.getInputText());
					} else {
						hasErrored = true;
					}
					
					
				} catch(Exception e) {
					hasErrored = true;
					Logger.log(LogLevel.WARN, "Couldn't connect to host!");
					e.printStackTrace();
					errorMsg = "Couldn't connect to server!";
				}
				
				if(testNetwork != null) {
					testNetwork.disconnect();
				}
				
				if(!hasErrored) {
					errorPopup = false;
					prepareCleanUp();
					Gui.cleanUp();
					
					Main.thePlayer = new Player(new Vector3f(0,120,0),new Vector3f(0,0,0));
					try {
						Main.network = new NetworkHandler(serverAddress.getInputText());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Main.inGameGUI = new GuiInGame();
					Main.shouldTick = true;
					if(Main.thePlayer != null) {
						Main.thePlayer.getCamera().setMouseLock(true);
					}
					SoundMaster.stopMusic();
					SoundMaster.doMusic();
					Main.shouldTick = true;
					Main.currentScreen = null;
				} else {
					errorPopup = true;
					if(!serverAddress.hasContent() ) {
						errorMsg = "No input given!";
					}
				}
			}
			
			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)+25;
			}
		});
		
		quitButton = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)+65, 200, 30, "Back to main menu...");
		quitButton.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				prepareCleanUp();
				Gui.cleanUp();
				Main.currentScreen = new GuiMainMenu(Main.getRandomSplash());
			}
			
			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)+65;
			}
		});
		
	}
	
	private int ticksToWait = 0;
	private int maxCoolDown = 60;
	private boolean lockTick = false;
	public void onTick() {
		if(ticksToWait < maxCoolDown) {
			ticksToWait++;
		}
		
		if(!lockTick && ticksToWait >= maxCoolDown) {
			lockTick = true;
		}
	}
	
	public void onUpdate() {
		if(Main.isPaused()) {
			drawTiledBackground(ResourceLoader.loadUITexture("dirtTex"), 48);
			drawShadowStringCentered((Display.getWidth()/2), (Display.getHeight()/2)-50, "Type in server address...");
			if(errorPopup) {
				
				drawShadowStringCentered(Color.red, (Display.getWidth()/2), 0+font.getHeight(errorMsg), errorMsg);
			}
			serverAddress.tick();
			joinButton.tick(lockTick);
			quitButton.tick(lockTick);
		}
	}
	
	public void onClose() {
		Gui.cleanUp();
	}
}
