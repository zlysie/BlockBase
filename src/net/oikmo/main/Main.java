package net.oikmo.main;

import java.io.File;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.DisplayManager;
import net.oikmo.engine.Loader;
import net.oikmo.engine.entity.Camera;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.engine.world.World;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.os.EnumOS;
import net.oikmo.toolbox.os.EnumOSMappingHelper;

public class Main {

	public static int WIDTH = 854;
	public static int HEIGHT = 640;																
	
	public static World theWorld;
	
	public static Vector3f camPos = new Vector3f(0,0,0);
	
	public static boolean displayRequest = false;
	
	public static void main(String[] args) {
		DisplayManager.createDisplay(WIDTH, HEIGHT);
		MasterRenderer.getInstance();

		theWorld = new World("ballsack!!!");
		
		Camera camera = new Camera(new Vector3f(0,10,0), new Vector3f(0,0,0));
		
		

		while(!Display.isCloseRequested()) {
			camera.update();
			camPos = new Vector3f(camera.getPosition());
			
			theWorld.update(camera);
			
			DisplayManager.updateDisplay();
		}
		displayRequest = true;
		DisplayManager.closeDisplay();
	}
	
	

	/**
	 * Retrieves data directory of .pepdog/ using {@code Main.getAppDir(String)}
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
