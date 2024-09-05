package net.oikmo.main.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import net.oikmo.engine.nbt.NBTTagCompound;
import net.oikmo.engine.save.SaveSystem;
import net.oikmo.engine.save.ServerListData;
import net.oikmo.engine.sound.SoundMaster;
import net.oikmo.main.Main;
import net.oikmo.network.client.NetworkHandler;
import net.oikmo.network.client.Server;
import net.oikmo.toolbox.CompressedStreamTools;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Logger.LogLevel;
import net.oikmo.toolbox.noise.OpenSimplexNoise;

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
			servers = new ArrayList<>();
			File save = new File( Main.getWorkingDirectory(), "/servers.dat");
			NBTTagCompound baseTag = null;
			try {
				baseTag = CompressedStreamTools.readCompressed(new FileInputStream(save));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(baseTag != null) {
				for(int i = 0; i < 100; i++) {
					NBTTagCompound nbttagcompound1 = baseTag.getCompoundTag("server-"+i);
					if(nbttagcompound1 != null) {
						if(nbttagcompound1.getString("IP").isEmpty()) {
							break;
						} else {
							System.out.println(nbttagcompound1.getString("IP"));
							servers.add(new Server(nbttagcompound1.getString("Name"),nbttagcompound1.getString("IP")));
						}
						
					} else {
						break;
					}
				}
			}
		}
		
		add = new GuiButton(((Display.getWidth()/2)-200/2)-10, Display.getHeight()-30*2, 200, 30, Main.lang.translateKey("network.servers.add"));
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
		
		delete = new GuiButton(((Display.getWidth()/2)+200/2)+10, Display.getHeight()-30*2, 200, 30, Main.lang.translateKey("gui.delete"));
		delete.setGuiCommand(new GuiCommand() {
			public void invoke() {
				deleteMode = !deleteMode;
				if(deleteMode) {
					delete.setText(Main.lang.translateKey("gui.cancel"));
				} else {
					delete.setText(Main.lang.translateKey("gui.delete"));
				}
			}
			
			public void update() {
				x = ((Display.getWidth()/2)+200/2)+10;
				y = Display.getHeight()-30*2;
			}
		});
		
		refresh = new GuiButton(((Display.getWidth()/2)-200/2)-10, Display.getHeight()-25, 200, 30, Main.lang.translateKey("network.servers.refresh"));
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
		
		quit = new GuiButton(((Display.getWidth()/2)+200/2)+10, Display.getHeight()-25, 200, 30, Main.lang.translateKey("gui.quit"));
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
			drawShadowStringCentered(Color.red, (Display.getWidth()/2), fontSize, errorReason);
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
						errorReason = Main.lang.translateKey("network.error.server");
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
						GuiMainMenu.stopMusic();
						Main.shouldTick = true;
						Main.currentScreen = null;
					} else {
						errorMode = true;
						if(s.getIP().isEmpty()) {
							errorReason = Main.lang.translateKey("network.error.noinput");
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
	}
	
	public void onClose() {
		NBTTagCompound base = new NBTTagCompound();
		for(int i = 0; i < servers.size(); i++) {
			Server s = servers.get(i);
			NBTTagCompound serverCompound = new NBTTagCompound();
			serverCompound.setString("IP", s.getIP());
			serverCompound.setString("Name", s.getName());
			base.setCompoundTag("server-"+i, serverCompound);
			System.out.println(i);
		}
		File save = new File( Main.getWorkingDirectory(), "/servers.dat");
		try {
			CompressedStreamTools.writeGzippedCompoundToOutputStream(base, new FileOutputStream(save));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(GuiServer g : guis) {
			g.onCleanUp();
		}
		quit.onCleanUp();
		add.onCleanUp();
	}
}
