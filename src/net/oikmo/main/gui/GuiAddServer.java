package net.oikmo.main.gui;

import org.lwjgl.opengl.Display;

import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.gui.Gui;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.gui.component.slick.GuiCommand;
import net.oikmo.engine.gui.component.slick.button.GuiButton;
import net.oikmo.engine.gui.component.slick.textfield.GuiTextField;
import net.oikmo.engine.network.Server;
import net.oikmo.main.Main;

public class GuiAddServer extends GuiScreen {
	
	public GuiAddServer() {
		super("Multiplayer");
	}
	
	private GuiTextField serverAddress;
	private GuiTextField serverName;
	private GuiButton saveButton;
	private GuiButton quitButton;
	
	public void onInit() {
		
		serverName = new GuiTextField(Main.lang.translateKey("network.servers.add.input.name"),Display.getWidth()/2, (Display.getHeight()/2)-55, 200, 30);
		serverName.setGuiCommand(new GuiCommand() {
			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)-15;
			}
		});
		serverName.setInputText("BlockBase Server");
		
		serverAddress = new GuiTextField(Main.lang.translateKey("network.servers.add.input.ip"),Display.getWidth()/2, (Display.getHeight()/2)-15, 200, 30);
		serverAddress.setGuiCommand(new GuiCommand() {
			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)-15;
			}
		});
		
		saveButton = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)+25, 200, 30, Main.lang.translateKey("network.servers.add.save"));
		saveButton.setGuiCommand(new GuiCommand() {
			public void invoke() {
				if(!serverName.getInputText().trim().isEmpty() && !serverAddress.getInputText().trim().isEmpty()) {
					GuiServerList.servers.add(new Server(serverName.getInputText().trim(), serverAddress.getInputText().trim()));
					prepareCleanUp();
					Gui.cleanUp();
					Main.currentScreen = new GuiServerList();
				}
			}
			
			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)+25;
			}
		});
		
		quitButton = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)+65, 200, 30, Main.lang.translateKey("network.servers.add.quit"));
		quitButton.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				prepareCleanUp();
				Gui.cleanUp();
				Main.currentScreen = new GuiServerList();
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
			drawShadowStringCentered((Display.getWidth()/2), (Display.getHeight()/2)-85, Main.lang.translateKey("network.servers.add.title"));
			serverAddress.tick();
			serverName.tick();
			saveButton.tick(lockTick);
			quitButton.tick(lockTick);
		}
	}
	
	public void onClose() {
		Gui.cleanUp();
		quitButton.onCleanUp();
		saveButton.onCleanUp();
		serverAddress.onCleanUp();
		serverName.onCleanUp();
	}
}
