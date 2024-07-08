package net.oikmo.main;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import javax.swing.ImageIcon;

import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;

import com.mojang.minecraft.Timer;

import net.oikmo.engine.DisplayManager;
import net.oikmo.engine.InputManager;
import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.entity.Entity;
import net.oikmo.engine.entity.Player;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.models.CubeModel;
import net.oikmo.engine.models.PlayerModel;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.engine.sound.SoundMaster;
import net.oikmo.engine.world.World;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.engine.world.chunk.coordinate.ChunkCoordHelper;
import net.oikmo.main.gui.GuiComponentLoader;
import net.oikmo.main.gui.GuiConnecting;
import net.oikmo.main.gui.GuiDisconnected;
import net.oikmo.main.gui.GuiInGame;
import net.oikmo.main.gui.GuiMainMenu;
import net.oikmo.network.client.NetworkHandler;
import net.oikmo.network.client.OtherPlayer;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Logger.LogLevel;
import net.oikmo.toolbox.error.PanelCrashReport;
import net.oikmo.toolbox.error.UnexpectedThrowable;
import net.oikmo.toolbox.os.EnumOS;
import net.oikmo.toolbox.os.EnumOSMappingHelper;
import net.oikmo.toolbox.properties.LanguageHandler;
import net.oikmo.toolbox.properties.OptionsHandler;

/**
 * Main class. Starts the engine, handles logic.
 * @author Oikmo
 */
public class Main {

	public static final int resourceVersion = 5;
	public static final String gameName = "BlockBase";
	public static final String version = "a0.2.0 [2] [DEV]";
	public static final String gameVersion = gameName + " " + version;

	public static boolean displayRequest = false;
	public static int WIDTH = 854;
	public static int HEIGHT = 480;																

	public static Frame frame;
	public static Canvas gameCanvas;
	private static boolean realClose = false;

	private static PanelCrashReport report;
	private static boolean hasErrored = false;

	public static GuiInGame inGameGUI;
	public static GuiScreen currentScreen;

	public static String currentlyPlayingWorld;

	public static World theWorld;
	public static Player thePlayer;

	public static float elapsedTime = 0;

	public static Vector3f camPos = new Vector3f(0,0,0);

	public static boolean shouldTick = true;
	public static boolean runTick = false;

	public static String[] splashes;
	public static String splashText;

	public static NetworkHandler theNetwork;
	public static String playerName = null;
	public static ImageBuffer playerSkinBuffer;
	public static int playerSkin;
	public static boolean disableNetworking = true;

	public static boolean jmode = false;
	
	public static LanguageHandler lang = LanguageHandler.getInstance();
	public static InputManager im;
	
	/**
	 * Basically, it creates the resources folder if they don't exist,<br>
	 * then it creates the window and checks whether or not the last<br>
	 * recorded resource version is equal to the current version. If<br>
	 * not then attempt to download the required resources and update<br>
	 * the version. Then it enters the game loop in which that handles<br>
	 * logic and rendering.
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String password = null;
		for(String arg : args) {
			if(arg.startsWith("u=")) {
				try {
					playerName = arg.split("=")[1];
				} catch(ArrayIndexOutOfBoundsException e) {}
			} else if(arg.startsWith("p=")) { 
				try {
					password = arg.split("=")[1];
				} catch(ArrayIndexOutOfBoundsException e) {}
			} else if(arg.startsWith("w=")) {
				try {
					WIDTH = Integer.parseInt(arg.split("=")[1]);
				} catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {}
				
			} else if(arg.startsWith("h=")) {
				try {
					HEIGHT = Integer.parseInt(arg.split("=")[1]);
				} catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {}
			}
		}
		
		Thread.currentThread().setName("Main Thread");
		removeHSPIDERR();
		Timer timer = new Timer(60f);

		try {
			if(!new File(getWorkingDirectory()+"/resources/custom").exists()) {
				new File(getWorkingDirectory()+"/resources/custom").mkdirs();
			}

			if(!new File(getWorkingDirectory()+"/options.txt").exists()) {
				new File(getWorkingDirectory()+"/options.txt").createNewFile();
				OptionsHandler.getInstance().insertKey("graphics.distance", 2+"");
				OptionsHandler.getInstance().insertKey("graphics.fov", 70+"");
				OptionsHandler.getInstance().insertKey("graphics.vsync", Boolean.toString(true));
				OptionsHandler.getInstance().insertKey("audio.volume", GameSettings.globalVolume+"");
				OptionsHandler.getInstance().insertKey("input.sensitivity", GameSettings.sensitivity+"");
			}

			frame = new Frame(Main.gameName);
			frame.setFocusable(true);
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					realClose = true;
					if(!DisplayManager.activeDisplay) {
						Logger.saveLog();
						System.exit(0);
					}
				}
			});

			gameCanvas = new Canvas();
			URL iconURL = Main.class.getResource("/assets/iconx32.png");
			ImageIcon icon = new ImageIcon(iconURL);
			frame.setIconImage(icon.getImage());
			frame.add(gameCanvas, "Center");
			gameCanvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
			frame.pack();
			frame.setLocationRelativeTo((Component)null);
			frame.setVisible(true);
			
			Logger.log(LogLevel.INFO, "Psst! I see you in the console! You can add your own custom resources to the game via the .blockbase/resources/custom folder!");
			DisplayManager.createDisplay(frame, gameCanvas);
			Display.setVSyncEnabled(true);
			Main.shouldTick = false; 
			SoundMaster.init();
			
			currentScreen = new GuiComponentLoader(password);
			
			//Entity test = new Entity(new TexturedModel(PlayerModel.getRawModel(), ResourceLoader.loadTexture("textures/skin_template")), new Vector3f(0,-2, 0), new Vector3f(180,0,0), 1f);

			/*Main.shouldTick = true;
			Main.loadWorld("world1");*/
			
			Display.setVSyncEnabled(Boolean.parseBoolean(OptionsHandler.getInstance().translateKey("graphics.vsync")));

			while(!Display.isCloseRequested() && !realClose) {
				timer.updateTimer();				

				//System.out.println(theWorld);
				
				if(thePlayer != null) {
					Main.thePlayer.updateCamera();
					if(Main.thePlayer.getCamera().isPerspective()) {
						MasterRenderer.getInstance().addEntity(Main.thePlayer);
					}
				} else {
					//MasterRenderer.getInstance().addEntity(test);
				}
				
				if(Main.currentScreen instanceof GuiComponentLoader) {
					if(((GuiComponentLoader)Main.currentScreen).isDone) {
						if(playerSkinBuffer != null) {
							Image skin = new Image(playerSkinBuffer);
							skin.setFilter(Image.FILTER_NEAREST);
							Main.playerSkin = skin.getTexture().getTextureID();
						
						} else {
							Main.playerSkin = ResourceLoader.loadTexture("textures/default_skin");
						}
					}
				}
				
				runTick = false;
				for(int e = 0; e < timer.elapsedTicks; ++e) {
					elapsedTime += 0.1f;

					
					if(inGameGUI != null && theWorld != null) {
						SoundMaster.playRandomMusicIfReady();
					}
					
					if(Main.currentScreen != null) {
						Main.currentScreen.onTick();
					}

					if(theNetwork != null && thePlayer != null) {
						theNetwork.tick();
					}

					if(shouldTick) {
						tick();
					}
				}

				if(theNetwork != null) {
					for(OtherPlayer p : Main.theNetwork.players.values()) {
						Vector3f position = new Vector3f(p.x,p.y,p.z);
						Vector3f rotation = new Vector3f(p.rotZ,-p.rotY+90,p.rotX);
						
						if(p.userName != null ) {
							if(p.model != null) {
								if(p.buffer != null && p.model.getTexture().getTextureID() == ResourceLoader.loadTexture("textures/default_skin")) {
									Image skinTex = new Image(p.buffer);
									skinTex.setFilter(Image.FILTER_NEAREST);
									p.model = new TexturedModel(PlayerModel.getRawModel(), skinTex.getTexture().getTextureID());
								}
								Entity entity = new Entity(p.model, position, rotation, 1f);
								if(p.block != -1) {
									Vector3f pos = new Vector3f(position);
									pos.y += 0.85f;
									Entity block = new Entity(new TexturedModel(CubeModel.getRawModel(Block.getBlockFromOrdinal(p.block)), MasterRenderer.currentTexturePack), pos, new Vector3f(p.rotZ,-p.rotY,0), 0.4f);
									MasterRenderer.getInstance().addEntity(block);
								}

								MasterRenderer.getInstance().addEntity(entity);
							} else {
								if(p.buffer != null) {
									Image skinTex = new Image(p.buffer);
									skinTex.setFilter(Image.FILTER_NEAREST);
									p.model = new TexturedModel(PlayerModel.getRawModel(), skinTex.getTexture().getTextureID());
								} else {
									p.model = new TexturedModel(PlayerModel.getRawModel(), ResourceLoader.loadTexture("textures/default_skin"));	
								}
							}
						}
						
						
					}
				}

				if(theWorld != null && thePlayer != null) {
					if(!(Main.currentScreen instanceof GuiConnecting) && !(Main.currentScreen instanceof GuiDisconnected) && !Main.thePlayer.tick && Main.theNetwork != null) {
						Main.currentScreen = new GuiConnecting();
					}
					if(thePlayer != null) {
						if(thePlayer.getCamera() != null) {
							SoundMaster.setListener(thePlayer.getCamera(), timer.renderPartialTicks);
						}
					}
					theWorld.update(thePlayer.getCamera());
				}
				
				if(inGameGUI != null) {				
					inGameGUI.update();
				}

				if(Main.currentScreen != null) {
					Main.currentScreen.update();
				}

				if(im != null) {
					im.handleInput();
				}
				
				DisplayManager.updateDisplay(gameCanvas);				
			}
		} catch(Exception e) {
			Main.error("Runtime Error!", e);
		}

		close();
	}

	/**
	 * Disconnects the player from a server.
	 * @param kick
	 * @param message
	 */
	public static void disconnect(boolean kick, String message) {
		Main.shouldTick = false;
		if(Main.thePlayer != null) {
			Main.thePlayer.getCamera().setMouseLock(false);
		}

		Main.thePlayer = null;
		if(Main.theWorld != null) {
			Main.theWorld.quitWorld();
		}
		Main.theWorld = null;
		
		ChunkCoordHelper.cleanUp();
		if(Main.inGameGUI != null) {
			Main.inGameGUI.prepareCleanUp();
			Main.inGameGUI = null;
		}
		if(Main.theNetwork != null) {
			Main.theNetwork.disconnect();
			if(Main.theNetwork.players != null) {
				Main.theNetwork.players.clear();
			}
		}

		Main.theNetwork = null;
		if(Main.currentScreen != null) {
			Main.currentScreen.prepareCleanUp();
		}
		Main.currentScreen = new GuiDisconnected(kick, message);

	}
	
	public static String getRandomSplash() {
		return splashes[new Random().nextInt(splashes.length)];
	}

	private static boolean hasSaved = false;

	public static void loadWorld(String worldName) {
		loadWorld(worldName, null, false);
	}
	
	public static void loadWorld(String worldName, String seed, boolean superflat) {
		currentlyPlayingWorld = worldName;
		
		GuiMainMenu.stopMusic();
		SoundMaster.stopMusic();

		inGameGUI = new GuiInGame();
		
		if(seed != null) {
			theWorld = new World(seed);
		} else {
			theWorld = new World();
		}
		
		theWorld.superFlat = superflat;
		theWorld.initLevelLoader(worldName);
		theWorld.startChunkCreator();
	}

	private static boolean tick;
	public static void shouldTick() {
		if(Main.theNetwork == null) {
			Main.shouldTick = !shouldTick;
			tick = Main.shouldTick;
		} else {
			tick = !tick;
		}

		if(Main.thePlayer != null) {

			Main.thePlayer.getCamera().setMouseLock(tick);
		}	
	}

	public static boolean isPaused() {
		return shouldTick == false;
	}

	

	/**
	 * Every 1/60th this method is ran. This handles movement.
	 */
	private static void tick() {
		runTick = true;

		if(Main.theNetwork == null) {

			if(theWorld != null) {
				theWorld.tick();
			}
			if(thePlayer != null) {
				camPos = new Vector3f(thePlayer.getPosition());
				if(!hasSaved && thePlayer.isOnGround()) {
					theWorld.saveWorld();
					hasSaved = true;
				}
			}

		} else {

			if(thePlayer != null) {
				camPos = new Vector3f(thePlayer.getPosition());
				if(theWorld != null) {
					theWorld.tick();
				}
				theNetwork.update();	
			}
		}	
	}

	/**
	 * Closes the game.
	 */
	public static void close() {
		if(Main.theNetwork != null) {
			Main.disconnect(false, "");
		} else {
			if(Main.theWorld != null) {
				Main.theWorld.saveWorldAndQuit();
			}
		}
		
		OptionsHandler.getInstance().save();
		Logger.saveLog();
		displayRequest = true;
		SoundMaster.cleanUp();
		ResourceLoader.cleanUp();
		DisplayManager.closeDisplay();

		System.exit(0);
	}

	/**
	 * Creates a frame with the error log embedded inside.
	 * 
	 * @param id (String)
	 * @param throwable (Throwable)
	 */
	public static void error(String id, Throwable throwable) {
		if(!hasErrored) {
			if(theNetwork != null) {
				theNetwork.disconnect();
			}
			
			displayRequest = true;
			Logger.saveLog();
			DisplayManager.closeDisplay();
			frame.removeAll();
			frame.addWindowListener(new WindowAdapter(){  
				public void windowClosing(WindowEvent e) {  
					frame.dispose(); 
					System.exit(0);
				}  
			});
			frame.setTitle(Main.gameName + " Error!");
			frame.setSize(WIDTH, HEIGHT);
			frame.setVisible(true);
			UnexpectedThrowable unexpectedThrowable = new UnexpectedThrowable(id, throwable);
			if(report == null) {
				report = new PanelCrashReport(unexpectedThrowable);
			} else {
				report.set(unexpectedThrowable);
			}
			frame.add(report, "Center");
			frame.validate();

			hasErrored = true;
		}	
	}

	private static void removeHSPIDERR() {
		File path = new File(".");
		String[] files = path.list();
		for(int i = 0; i < files.length; i++) {
			if(files[i].contains("hs_err_pid")) {
				File file = new File(path.getAbsoluteFile() + "\\" + files[i]);
				file.delete();
			}
		}
	}
	
	/**
	 * Returns the resources directory
	 * 
	 * %appdata%/.blockbase/resources
	 * @return
	 */
	public static File getResources() {
		return new File(getWorkingDirectory()+"/resources");
	}

	/**
	 * Retrieves data directory of .blockbase/ using {@code Main.getWorkingDirectory(String)}
	 * @return Directory (File)
	 */
	public static File getWorkingDirectory() {
		return getWorkingDirectory("blockbase");
	}

	/**
	 * Uses {@link EnumOSMappingHelper} to locate an APPDATA directory in the system.
	 * Then it creates a new directory based on the given name e.g <b>.name/</b>
	 * 
	 * @param name (String)
	 * @return Directory (File)
	 */
	public static File getWorkingDirectory(String name) {
		String userDir = System.getProperty("user.home", ".");
		File folder;
		switch(EnumOSMappingHelper.os[EnumOS.getOS().ordinal()]) {
		case 1:
		case 2:
			folder = new File(userDir, '.' + name + '/');
			break;
		case 3:
			String appdataLocation = System.getenv("APPDATA");
			if(appdataLocation != null) {
				folder = new File(appdataLocation, "." + name + '/');
			} else {
				folder = new File(userDir, '.' + name + '/');
			}
			break;
		case 4:
			folder = new File(userDir, "Library/Application Support/" + name);
			break;
		default:
			folder = new File(userDir, name + '/');
		}

		if(!folder.exists() && !folder.mkdirs()) {
			throw new RuntimeException("The working directory could not be created: " + folder);
		} else {
			return folder;
		}
	}
}
