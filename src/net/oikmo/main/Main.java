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
import java.util.Comparator;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.DisplayManager;
import net.oikmo.engine.InputManager;
import net.oikmo.engine.Loader;
import net.oikmo.engine.Timer;
import net.oikmo.engine.entity.ItemBlock;
import net.oikmo.engine.entity.Player;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.models.CubeModel;
import net.oikmo.engine.sound.SoundMaster;
import net.oikmo.engine.world.World;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.main.gui.GuiInGame;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Logger.LogLevel;
import net.oikmo.toolbox.UnzipUtility;
import net.oikmo.toolbox.error.CanvasLogo;
import net.oikmo.toolbox.error.PanelCrashReport;
import net.oikmo.toolbox.error.UnexpectedThrowable;
import net.oikmo.toolbox.os.EnumOS;
import net.oikmo.toolbox.os.EnumOSMappingHelper;

public class Main {
	
	private static final int resourceVersion = 00;
	public static final String gameName = "BlockBase";
	public static final String version = "a0.0.7";
	public static final String gameVersion = gameName + " " + version;
	
	public static boolean displayRequest = false;
	public static int WIDTH = 854;
	public static int HEIGHT = 480;																
	
	private static Frame frame;
	private static Canvas gameCanvas;
	
	private static PanelCrashReport report;
	private static boolean hasErrored = false;
	
	public static GuiScreen currentScreen;
	
	public static World theWorld;
	public static Player thePlayer;
	
	public static float elapsedTime = 0;
	
	public static Vector3f camPos = new Vector3f(0,0,0);

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
			
			frame = new Frame(Main.gameName);
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					close();
					System.exit(0);
				}
			});
			gameCanvas = new Canvas();
			URL iconURL = Main.class.getResource("/assets/iconx32.png");
			ImageIcon icon = new ImageIcon(iconURL);
			frame.setIconImage(icon.getImage());
			//frame.setBackground(new Color(0.4f, 0.7f, 1.0f, 1));
			frame.setLayout(new BorderLayout());
			frame.add(gameCanvas, "Center");
			gameCanvas.setPreferredSize(new Dimension(WIDTH, HEIGHT)); //480
			frame.pack();
			frame.setLocationRelativeTo((Component)null);
			frame.removeAll();
			frame.setBackground(new Color(55f/256f, 51f/256f, 99f/256f, 256f/256f));
			frame.add(new CanvasLogo("iconx128"), "Center");

			frame.setVisible(true);

			downloadResources();

			Thread.sleep(2000);

			Logger.log(LogLevel.INFO, "Psst! I see you in the console! You can add your own custom resources to the game via the .blockbase/resources/custom folder!");

			DisplayManager.createDisplay(frame, gameCanvas);
			CubeModel.setup();

			SoundMaster.init();

			InputManager im = new InputManager();

			theWorld = new World();
			currentScreen = new GuiInGame();

			thePlayer = new Player(new Vector3f(0,120,0), new Vector3f(0,0,0));
			
			//theWorld.entities.add(thePlayer);
			theWorld.entities.add(thePlayer.getCamera().getSelectedBlock());
			
			ItemBlock block = new ItemBlock(Block.bedrock, new Vector3f(0,100,0), false);
			
			theWorld.entities.add(block);
			
			while(!Display.isCloseRequested()) {
				Main.thePlayer.updateCamera();
				
				timer.advanceTime();

				for(int e = 0; e < timer.ticks; ++e) {
					tick();
				}
				
				im.handleInput();
				
				theWorld.update(thePlayer.getCamera());
				
				DisplayManager.updateDisplay(gameCanvas);				
			}
		} catch(Exception e) {
			Main.error("Runtime Error!", e);
		}
		Logger.saveLog();
		close();
		Loader.cleanUp();
		DisplayManager.closeDisplay();
	}

	private static void tick() {
		elapsedTime += 0.1f;
		theWorld.tick();
		camPos = new Vector3f(thePlayer.getCamera().getPosition());
	}

	private static void close() {
		SoundMaster.cleanUp();
		displayRequest = true;
	}

	/**
	 * Creates a frame with the error log embedded inside.
	 * 
	 * @param id (String)
	 * @param throwable (Throwable)
	 */
	public static void error(String id, Throwable throwable) {
		if(!hasErrored) {
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

			FileWriter myWriter = new FileWriter(getResources() + "/resourcesVersion.txt");

			myWriter.write(Integer.toString(resourceVersion));
			myWriter.close();

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
