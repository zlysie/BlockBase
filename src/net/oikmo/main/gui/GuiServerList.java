package net.oikmo.main.gui;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.entity.Player;
import net.oikmo.engine.gui.Gui;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.gui.component.slick.GuiCommand;
import net.oikmo.engine.gui.component.slick.GuiServer;
import net.oikmo.engine.gui.component.slick.button.GuiButton;
import net.oikmo.engine.save.SaveSystem;
import net.oikmo.engine.save.ServerListData;
import net.oikmo.engine.sound.SoundMaster;
import net.oikmo.main.Main;
import net.oikmo.network.client.NetworkHandler;
import net.oikmo.network.client.Server;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Logger.LogLevel;

public class GuiServerList extends GuiScreen {
	
	private boolean errorMode = false;
	private String errorReason = "";
	
	public static List<Server> servers = null;
	private List<GuiServer> guis = new ArrayList<>();
	private int width = 400;
	private int height = 60;
	private int offset = 10;
	
	private boolean lockDelete = false;
	
	private GuiButton add;
	private GuiButton delete;
	private GuiButton refresh;
	private GuiButton quit;
	
	private boolean deleteMode = false;
	
	public GuiServerList() {
		super("Server List");
	}
	
	public void onInit() {
		guis = new ArrayList<>();
		
		if(servers ==  null) {
			ServerListData data = SaveSystem.loadServers();
			if(data != null) {
				servers = data.servers;
			} else {
				servers = new ArrayList<>();
			}
		}
		
		add = new GuiButton(((Display.getWidth()/2)-200/2)-10, Display.getHeight()-30*2, 200, 30, "Add");
		add.setGuiCommand(new GuiCommand() {
			public void invoke() {
				prepareCleanUp();
				Gui.cleanUp();
				Main.currentScreen = new GuiAddServer();
			}
			
			public void update() {
				x = ((Display.getWidth()/2)-200/2)-10;
				y = Display.getHeight()-30*2;
			}
		});
		
		delete = new GuiButton(((Display.getWidth()/2)+200/2)+10, Display.getHeight()-30*2, 200, 30, "Delete...");
		delete.setGuiCommand(new GuiCommand() {
			public void invoke() {
				deleteMode = !deleteMode;
				if(deleteMode) {
					delete.setText("Cancel");
				} else {
					delete.setText("Delete...");
				}
			}
			
			public void update() {
				x = ((Display.getWidth()/2)+200/2)+10;
				y = Display.getHeight()-30*2;
			}
		});
		
		refresh = new GuiButton(((Display.getWidth()/2)-200/2)-10, Display.getHeight()-25, 200, 30, "Refresh");
		refresh.setGuiCommand(new GuiCommand() {
			public void invoke() {
				for(GuiServer s : guis) {
					s.refresh();
				}
			}
			
			public void update() {
				x = ((Display.getWidth()/2)-200/2)-10;
				y = Display.getHeight()-25;
			}
		});
		
		quit = new GuiButton(((Display.getWidth()/2)+200/2)+10, Display.getHeight()-25, 200, 30, "Back to main menu");
		quit.setGuiCommand(new GuiCommand() {
			public void invoke() {
				prepareCleanUp();
				Gui.cleanUp();
				Main.currentScreen = new GuiMainMenu(Main.splashText);
			}
			
			public void update() {
				x = ((Display.getWidth()/2)+200/2)+10;
				y = Display.getHeight()-25;
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
		if(guis.size() != servers.size()) {		
			reconstructList();
		}
		drawTiledBackground(ResourceLoader.loadUITexture("dirtTex"), 48);
		
		if(Keyboard.isKeyDown(Keyboard.KEY_DELETE)) {
			if(!lockDelete) {
				if(servers.size() != 0) {
					servers.remove(servers.size()-1);
				}
			}
			lockDelete = true;
		} else {
			lockDelete = false;
		}
		
		for(GuiServer s : guis) {
			s.tick(deleteMode, lockTick);
		}
		
		if(errorMode) {
			drawShadowStringCentered(Color.red, (Display.getWidth()/2), 0+font.getHeight(errorReason), errorReason);
		}
		
		add.tick();
		delete.tick();
		refresh.tick();
		quit.tick();
	}
	
	private void reconstructList() {
		guis.clear();
		int heightLength = (height*servers.size()) + (offset*servers.size()-1);
		for(int i = 0; i < servers.size(); i++) {
			Server s = servers.get(i);
			int yPos = i * (height+offset);
			GuiServer g = new GuiServer((Display.getWidth()/2), (Display.getHeight()/2)-(heightLength/2)+yPos, width, height, s);
			g.setGuiCommand(new GuiCommand() {
				
				@Override
				public void invoke() {
					boolean hasErrored = false;
					try {
						if(!s.getIP().isEmpty()) {
							InetAddress.getByName(s.getIP().trim());
							NetworkHandler.testNetwork(s.getIP().trim());
						} else {
							hasErrored = true;
						}
						
						
					} catch(Exception e) {
						hasErrored = true;
						Logger.log(LogLevel.WARN, "Couldn't connect to host!");
						e.printStackTrace();
						errorReason = "Couldn't connect to server!";
					}
					
					if(!hasErrored) {
						errorMode = false;
						Gui.cleanUp();
						
						Main.thePlayer = new Player(new Vector3f(0,120,0),new Vector3f(0,0,0));
						try {
							Main.theNetwork = new NetworkHandler(s.getIP().trim());
						} catch (Exception e) {
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
						errorMode = true;
						if(s.getIP().isEmpty()) {
							errorReason = "No input given!";
						}
					}
				}
				public void update() {
					x = (Display.getWidth()/2);
					y = (Display.getHeight()/2)-(heightLength/2)+yPos;
				}
			});
			guis.add(g);
		}
		SaveSystem.saveServers(new ServerListData(servers));
	}
	
	public void onClose() {
		for(GuiServer g : guis) {
			g.onCleanUp();
		}
		quit.onCleanUp();
		add.onCleanUp();
	}
}
