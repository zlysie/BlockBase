package net.oikmo.main;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.DisplayManager;
import net.oikmo.engine.Loader;
import net.oikmo.engine.audio.AudioMaster;
import net.oikmo.engine.entity.Camera;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.models.CubeModel;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.engine.world.World;
import net.oikmo.main.gui.GuiInGame;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.error.PanelCrashReport;
import net.oikmo.toolbox.error.UnexpectedThrowable;
import net.oikmo.toolbox.os.EnumOS;
import net.oikmo.toolbox.os.EnumOSMappingHelper;

public class Main {
	
	public static String gameName = "BlockBase";
	public static String version = "[a0.0.1]";
	public static String gameVersion = gameName + " " + version;
	
	public static boolean displayRequest = false;
	public static int WIDTH = 854;
	public static int HEIGHT = 480;																
	
	public static World theWorld;
	
	public static Vector3f camPos = new Vector3f(0,0,0);
	
	static Frame frame;
	static Canvas gameCanvas;
	
	private static GuiScreen currentScreen;
	
	public static void main(String[] args) {
		Thread.currentThread().setName("Main Thread");
		removeHSPIDERR();
		try {
			
			frame = new Frame(Main.gameVersion);
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					displayRequest = true;
					Logger.saveLog();
					System.exit(0);
					DisplayManager.closeDisplay();
					
				}
			});
			gameCanvas = new Canvas();
			frame.setBackground(new Color(0.4f, 0.7f, 1.0f, 1));
			gameCanvas.setBackground(new Color(0.4f, 0.7f, 1.0f, 1));

			frame.setLayout(new BorderLayout());
			frame.add(gameCanvas, "Center");
			gameCanvas.setPreferredSize(new Dimension(WIDTH, HEIGHT)); //480
			frame.pack();
			frame.setLocationRelativeTo((Component)null);
			frame.setVisible(true);
			DisplayManager.createDisplay(gameCanvas,WIDTH, HEIGHT);
			CubeModel.createVertices();
			AudioMaster.init();
			MasterRenderer.getInstance();

			theWorld = new World("ballsack!!!");
			
			currentScreen = new GuiInGame();
			
			Camera camera = new Camera(new Vector3f(0,10,0), new Vector3f(0,0,0));
			while(!Display.isCloseRequested()) {
				camera.update();
				camPos = new Vector3f(camera.getPosition());
				
				currentScreen.update();
				theWorld.update(camera);
				
				DisplayManager.updateDisplay(gameCanvas);
				
				if(Keyboard.next()) {
					if(Keyboard.isKeyDown(Keyboard.KEY_F2)) {
						DisplayManager.saveScreenshot();
					}
				}
				
			}
		} catch(Exception e) {
			Main.error("Runtime Error!", e);
		}
		
		
		
	}
	
	static PanelCrashReport report;
	static boolean balls = false;
	/**
	 * Creates a frame with the error log embedded inside.
	 * 
	 * @param id (String)
	 * @param throwable (Throwable)
	 */
	public static void error(String id, Throwable throwable) {
		if(!balls) {
			frame = new Frame();
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
			
			balls = true;
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

	public void cleanUp() {
		Loader.cleanUp();
		DisplayManager.closeDisplay();
		Logger.saveLog();
		System.exit(0);
	}
}
