package net.oikmo.main.gui;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

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
	
	public void onInit() {
		serverAddress = new GuiTextField(Display.getWidth()/2, (Display.getHeight()/2)-15, 200, 30);
		serverAddress.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				
			}
			
			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = Display.getHeight()/2;
			}
		});
		
		joinButton = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)+25, 200, 30, "Join world...");
		joinButton.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				
				boolean hasErrored = false;
				try {
					InetAddress.getByName(serverAddress.getInputText());
					
				} catch(UnknownHostException e) {
					hasErrored = true;
					Logger.log(LogLevel.WARN, "Couldn't connect to host!");
				}
				if(!hasErrored) {
					prepareCleanUp();
					Gui.cleanUp();
					
					Main.thePlayer = new Player(new Vector3f(0,120,0),new Vector3f(0,0,0));
					Main.network = new NetworkHandler(serverAddress.getInputText());
					Main.inGameGUI = new GuiInGame();
					Main.shouldTick = true;
					if(Main.thePlayer != null) {
						Main.thePlayer.getCamera().setMouseLock(true);
					}
					SoundMaster.stopMusic();
					SoundMaster.doMusic();
					Main.shouldTick = true;
					Main.currentScreen = null;
				}
				
			}
			
			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = Display.getHeight()/2;
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
				y = Display.getHeight()/2;
			}
		});
		
	}
	
	public void onUpdate() {
		if(Main.isPaused()) {
			drawTiledBackground(ResourceLoader.loadUITexture("dirtTex"), 48);
			drawShadowStringCentered((Display.getWidth()/2), (Display.getHeight()/2)-50, "Type in server address...");
			serverAddress.tick();
			joinButton.tick();
			quitButton.tick();
		}
	}
	
	public void onClose() {
		Gui.cleanUp();
	}
}
