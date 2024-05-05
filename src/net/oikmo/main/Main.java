package net.oikmo.main;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import com.mojang.minecraft.Timer;

import net.oikmo.engine.DisplayManager;
import net.oikmo.engine.InputManager;
import net.oikmo.engine.entity.Entity;
import net.oikmo.engine.entity.Player;
import net.oikmo.engine.gui.Gui;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.models.CubeModel;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.particle.ParticleEmitter;
import net.oikmo.engine.particle.ParticleEmitterBuilder;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.engine.save.SaveSystem;
import net.oikmo.engine.sound.SoundMaster;
import net.oikmo.engine.world.World;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.main.gui.GuiConnecting;
import net.oikmo.main.gui.GuiDisconnected;
import net.oikmo.main.gui.GuiInGame;
import net.oikmo.main.gui.GuiMainMenu;
import net.oikmo.network.client.NetworkHandler;
import net.oikmo.network.client.OtherPlayer;
import net.oikmo.toolbox.FastMath;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Logger.LogLevel;
import net.oikmo.toolbox.Maths;
import net.oikmo.toolbox.UnzipUtility;
import net.oikmo.toolbox.error.CanvasLogo;
import net.oikmo.toolbox.error.PanelCrashReport;
import net.oikmo.toolbox.error.UnexpectedThrowable;
import net.oikmo.toolbox.os.EnumOS;
import net.oikmo.toolbox.os.EnumOSMappingHelper;

public class Main extends Gui {
	
	private static final int resourceVersion = 3;
	public static final String gameName = "BlockBase";
	public static final String version = "a0.1.5";
	public static final String gameVersion = gameName + " " + version;
	
	public static boolean displayRequest = false;
	public static int WIDTH = 854;
	public static int HEIGHT = 480;																
	
	public static Frame frame;
	public static Canvas gameCanvas;
	
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

	private static String[] splashes;
	public static String splashText;
	
	public static NetworkHandler network;
	public static String playerName = null;
	
	public static void main(String[] args) {
		Thread.currentThread().setName("Main Thread");
		removeHSPIDERR();
		Timer timer = new Timer(60f);
		try {
			if(!new File(getDir()+"/resources/custom").exists()) {
				new File(getDir()+"/resources/custom").mkdirs();
			}
			if(!new File(getResources() + "/music").exists()) {
				new File(getResources() + "/music").mkdirs();
			}
			if(!new File(getResources() + "/sfx").exists()) {
				new File(getResources() + "/sfx").mkdirs();
			}
			
			frame = new Frame(Main.gameName);
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					Logger.saveLog();
					System.exit(0);
				}
			});
			
			gameCanvas = new Canvas();
			URL iconURL = Main.class.getResource("/assets/iconx32.png");
			ImageIcon icon = new ImageIcon(iconURL);
			frame.setIconImage(icon.getImage());
			frame.setLayout(new BorderLayout());
			frame.add(gameCanvas, "Center");
			gameCanvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
			frame.pack();
			frame.setLocationRelativeTo((Component)null);
			frame.removeAll();
			
			
			if(Calendar.getInstance().get(Calendar.MONTH) == 3 && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 1) {
				frame.setBackground(new Color(0f, 0.6f, 0.8274509803921568f, 1f));
				frame.add(new CanvasLogo("icon_aprilFools"), "Center");
			} else {
				frame.setBackground(new Color(55f/256f, 51f/256f, 99f/256f, 256f/256f));
				frame.add(new CanvasLogo("iconx128"), "Center");
			}
			
			
			frame.setVisible(true);
			downloadResources();
			Thread.sleep(2000);

			Logger.log(LogLevel.INFO, "Psst! I see you in the console! You can add your own custom resources to the game via the .blockbase/resources/custom folder!");
			DisplayManager.createDisplay(frame, gameCanvas);
			frame.setBackground(new Color(0.4f, 0.7f, 1.0f, 1));
			CubeModel.setup();
			
			SoundMaster.init();

			InputManager im = new InputManager();
			
			shouldTick = false; 
			
			splashes = Maths.fileToArray("splashes.txt");
			splashText = splashes[new Random().nextInt(splashes.length)];
			
			currentScreen = new GuiMainMenu(splashText);
			
			((GuiMainMenu)currentScreen).doRandomMusic();
			
			TexturedModel obsidian = new TexturedModel(CubeModel.getRawModel(Block.obsidianPlayer), MasterRenderer.currentTexturePack);
			
			ParticleEmitter particleEmitter = new ParticleEmitterBuilder()
		            .setInitialVelocity(new Vector3f(0, 0, 0))
		            .setGravity(new Vector3f(0, -0.0001f, 0))
		            .setSpawningRate(50)
		            .setParticleLifeTime(500)
		            .createParticleEmitter();
			
			boolean lockParticle = false;
			while(!Display.isCloseRequested()) {
				timer.updateTimer();				
				
				if(thePlayer != null) {
					Main.thePlayer.updateCamera();
					particleEmitter.update();
					if(Keyboard.isKeyDown(Keyboard.KEY_G)) {
						particleEmitter.setVelocityModifier(particleEmitter.getVelocityModifier() * 1.01f);
						particleEmitter.setGravity((Vector3f) particleEmitter.getGravity().scale(1.01f));
						particleEmitter.setSpawningRate(particleEmitter.getSpawningRate() * 1.01f);
						lockParticle = true;
					} else {
						lockParticle = false;
					}
				}
				
				if(network != null) {
					for(Map.Entry<Integer, OtherPlayer> e : Main.network.players.entrySet()) {
						OtherPlayer p = e.getValue();
						Vector3f position = new Vector3f(p.x,p.y,p.z);
						//System.out.println(position);
						Vector3f rotation = new Vector3f(p.rotZ,-p.rotY,p.rotX);
						//System.out.println(rotation);
						Entity entity = new Entity(obsidian, position, rotation, 1f);
						if(p.block != -1) {
							Vector3f pos = new Vector3f(position);
							pos.y += 0.85f;
							Entity block = new Entity(new TexturedModel(CubeModel.getRawModel(Block.getBlockFromOrdinal(p.block)), MasterRenderer.currentTexturePack), pos, new Vector3f(p.rotZ,-p.rotY,0), 0.4f);
							MasterRenderer.getInstance().addEntity(block);
						}
						
						MasterRenderer.getInstance().addEntity(entity);
					}
				}
				
				if(theWorld != null && thePlayer != null) {
					if(!(Main.currentScreen instanceof GuiConnecting) && !(Main.currentScreen instanceof GuiDisconnected) && !Main.thePlayer.tick) {
						Main.currentScreen = new GuiConnecting();
					}
					if(thePlayer != null) {
						if(thePlayer.getCamera() != null) {
							SoundMaster.setListener(thePlayer.getCamera(), timer.renderPartialTicks);
						}
					}
					theWorld.update(thePlayer.getCamera());
				}
				
				particleEmitter.draw();
				
				if(inGameGUI != null) {				
					inGameGUI.update();
				}
				
				if(Main.currentScreen != null) {
					Main.currentScreen.update();
				}
				
				for(int e = 0; e < timer.elapsedTicks; ++e) {
					elapsedTime += 0.1f;
					
					if(Main.currentScreen != null) {
						Main.currentScreen.onTick();
					}
					
					if(network != null && thePlayer != null) {
						network.tick();
					}
					
					if(shouldTick) {
						tick();
						
					}
				}
				
				im.handleInput();
				
				DisplayManager.updateDisplay(gameCanvas);				
			}
		} catch(Exception e) {
			Main.error("Runtime Error!", e);
		}
		close();
	}
	
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
		if(Main.inGameGUI != null) {
			Main.inGameGUI.prepareCleanUp();
			Main.inGameGUI = null;
		}
		
		Main.network = null;
		if(Main.currentScreen != null) {
			Main.currentScreen.prepareCleanUp();
		}
		Main.currentScreen = new GuiDisconnected(kick, message);
		
	}
	
	public static boolean isInValidRange(Vector3f origin) {
		return isInValidRange(1, origin);
	}
	public static boolean isInValidRange(int size, Vector3f origin) {
		int distX = (int) FastMath.abs((Main.camPos.x * size) - origin.x);
		int distZ = (int) FastMath.abs((Main.camPos.z * size) - origin.z);

		if((distX <= World.WORLD_SIZE) && (distZ <= World.WORLD_SIZE)) {
			return true;
		}

		return false;
	}
	
	public static String getRandomSplash() {
		return splashes[new Random().nextInt(splashes.length)];
	}
	
	private static boolean hasSaved = false;
	
	public static void loadWorld(String worldName) {
		currentlyPlayingWorld = worldName;
		SoundMaster.stopMusic();
		SoundMaster.doMusic();
		
		inGameGUI = new GuiInGame();
		
		if(SaveSystem.load(worldName) != null) {
			theWorld = World.loadWorld(worldName);
		} else {
			theWorld = new World();
			thePlayer = new Player(new Vector3f(0,120,0), new Vector3f(0,0,0));
			theWorld.startChunkCreator();
		}
	}
	
	private static boolean tick;
	public static void shouldTick() {
		if(Main.network == null) {
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

	private static void tick() {
		if(Main.network == null) {
			
			if(theWorld != null) {
				theWorld.tick();
			}
			if(thePlayer != null) {
				
				
				camPos = new Vector3f(thePlayer.getCamera().getPosition());
				if(!hasSaved && thePlayer.isOnGround()) {
					theWorld.saveWorld(currentlyPlayingWorld);
					hasSaved = true;
				}
			}
			
		} else {
			if(thePlayer != null && network != null) {
				camPos = new Vector3f(thePlayer.getCamera().getPosition());
				thePlayer.tick();
				network.update();
			}
			
			
		}	
	}

	public static void close() {
		Logger.saveLog();
		displayRequest = true;
		DisplayManager.closeDisplay();
		SoundMaster.cleanUp();
		//System.exit(0);
	}

	/**
	 * Creates a frame with the error log embedded inside.
	 * 
	 * @param id (String)
	 * @param throwable (Throwable)
	 */
	public static void error(String id, Throwable throwable) {
		if(!hasErrored) {
			if(network != null) {
				network.disconnect();
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

	private static void downloadResources() throws IOException {
		File dir = getResources();
		File tmp = new File(getDir() + "/tmp");
		File txt = new File(getResources() + "/resourcesVersion.txt");


		tmp.mkdir();
		if(!txt.exists()) {

			FileWriter resourceTxtWriter = new FileWriter(getResources() + "/resourcesVersion.txt");

			resourceTxtWriter.write(Integer.toString(resourceVersion));
			resourceTxtWriter.close();

			int options = JOptionPane.showConfirmDialog(frame, "Going to download resources based on resourceVersion: " + resourceVersion +  ".\nJust saying this will take a while depending on your connection or disk speeds.", "Resource Downloader Reminder", JOptionPane.PLAIN_MESSAGE);

			if(options == -1) {
				System.exit(0);
			}
			download("https://oikmo.github.io/resources/blockbase/resources"+resourceVersion+".zip", tmp + "/resources.zip");
			UnzipUtility unzipper = new UnzipUtility();

			unzipper.unzip(tmp + "/resources.zip", getResources()+"");
		}

		try {
			File versionTXT = new File(getResources() + "/resourcesVersion.txt");
			if (versionTXT.createNewFile()) {
				System.out.println("File created: " + versionTXT.getName());
				FileWriter myWriter = new FileWriter(getResources() + "/resourcesVersion.txt");

				myWriter.write(Integer.toString(resourceVersion));
				myWriter.close();

			} else {
				System.out.println("File already exists.");

				BufferedReader brr = new BufferedReader(new FileReader(getResources() + "/resourcesVersion.txt"));     
				String temp = brr.readLine();
				if (temp == null) {
					FileWriter myWriter = new FileWriter(getResources() + "/resourcesVersion.txt");
					myWriter.write(Integer.toString(resourceVersion));
					myWriter.close();
				} else {
					if(!temp.contentEquals(Integer.toString(resourceVersion))) {
						int tempInt = Integer.parseInt(temp.trim());
						if(tempInt < resourceVersion || tempInt > resourceVersion) {
							if(dir.exists()) {
								Files.walk(Paths.get(new File(getResources()+"/").getPath())).sorted(Comparator.reverseOrder()) .forEach(path -> {
									try {
										if(!path.toString().contains("custom")) {
											Files.delete(path);  //delete each file or directory
										}
									} catch (IOException e) {
										e.printStackTrace();
									}
								});
							}

							int options = JOptionPane.showConfirmDialog(frame, "Going to download resources based on resourceVersion: " + resourceVersion +  ".\nJust saying this will take a while depending on your connection or disk speeds.", "Resource Downloader Reminder", JOptionPane.PLAIN_MESSAGE);

							if(options == -1) {
								System.exit(0);
							}
							
							
							if(!new File(getResources() + "/music").exists()) {
								new File(getResources() + "/music").mkdirs();
							}
							if(!new File(getResources() + "/sfx").exists()) {
								new File(getResources() + "/sfx").mkdirs();
							}
							if(new File(getResources()+"/music").list().length == 0) {
								download("https://oikmo.github.io/resources/blockbase/resources"+resourceVersion+".zip", tmp + "/resources.zip");
								UnzipUtility unzipper = new UnzipUtility();
								unzipper.unzip(tmp+"/resources.zip", getResources()+"/");
							}

							download("https://oikmo.github.io/resources/blockbase/resources"+resourceVersion+".zip", tmp + "/resources.zip");
							UnzipUtility unzipper = new UnzipUtility();
							unzipper.unzip(tmp+"/resources.zip", getResources()+"/");

							FileWriter myWriter = new FileWriter(getResources() + "/resourcesVersion.txt");
							myWriter.write(Integer.toString(resourceVersion));
							myWriter.close();
						}
					}

				}

				if(new File(getResources()+"/music/").list().length == 0) {
					download("https://oikmo.github.io/resources/blockbase/resources"+resourceVersion+".zip", tmp + "/resources.zip");
					UnzipUtility unzipper = new UnzipUtility();
					unzipper.unzip(tmp+"/resources.zip", getResources()+"/");
				}

				brr.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("[ERROR] Version could NOT be verified!");
		}

		File zipFile = tmp;
		if(zipFile.exists()) {
			new File(zipFile + "/resources.zip").delete();
			zipFile.delete();
		}
	}

	public static File getResources() {
		return new File(getDir()+"/resources");
	}

	/**
	 * Retrieves data directory of .blockbase/ using {@code Main.getAppDir(String)}
	 * @return Directory (File)
	 */
	public static File getDir() {
		return getAppDir("blockbase");
	}

	/**
	 * Uses {@code Main.getOS} to locate an APPDATA directory in the system.
	 * Then it creates a new directory based on the given name e.g <b>.name/</b>
	 * 
	 * @param name (String)
	 * @return Directory (File)
	 */
	public static File getAppDir(String name) {
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

	private static void download(String urlStr, String file) throws IOException {
		URL url = new URL(urlStr);
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		FileOutputStream fos = new FileOutputStream(file);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
		rbc.close();
	}
}
