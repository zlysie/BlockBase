package net.oikmo.engine;

import java.awt.Canvas;
import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.gui.component.slick.GuiComponent;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.main.Main;
import net.oikmo.main.gui.GuiInventory;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Logger.LogLevel;

/**
 * Handles window layout and updates
 * 
 * @author <i>Oikmo</i>
 */
public class DisplayManager {

	private static int fps, literalfps;
	private static long lastFrameTime, lastFPS;
	private static float delta;
	public static boolean activeDisplay = false;

	/**
	 * Creates window by size (loads cursor and window icon) and sets OpenGL version.
	 * 
	 * @author <i>Oikmo</i>
	 * @param frame 
	 */
	public static void createDisplay(Frame frame, Canvas gameCanvas) {
		frame.removeAll();
		frame.add(gameCanvas, "Center");
		try {
			Display.setParent(gameCanvas);
			Display.create();
			Keyboard.create();
			GL11.glViewport(0, 0, Main.WIDTH, Main.HEIGHT);
			lastFrameTime = getCurrentTime();
			lastFPS = getCurrentTime();
			MasterRenderer.getInstance();
			activeDisplay = true;
		} catch (LWJGLException e) {
			Main.error("Display Error!", e);
		}
	}

	private static boolean setFullScreen;
	/**
	 * Updates the display to show a new frame and calculates the last frame time.
	 * <br>
	 * Handles fullscreen and taking screenshots on the press of a key.
	 */
	public static void updateDisplay(Canvas gameCanvas) {
		updateFPS();
		Display.update();

		if(Keyboard.isKeyDown(Keyboard.KEY_F11)) {
			if(!setFullScreen) {
				try {
					Display.setFullscreen(!Display.isFullscreen());

				} catch (LWJGLException e) {
					Main.error("Display Error!", e);
				}
				if(Display.isFullscreen()) {
					Main.WIDTH  = Display.getDisplayMode().getWidth();
					Main.HEIGHT = Display.getDisplayMode().getHeight();
					GL11.glViewport(0, 0, Main.WIDTH, Main.HEIGHT );
					MasterRenderer.getInstance().updateProjectionMatrix();
					for(GuiComponent comp : GuiComponent.components) {
						comp.updateComponent();
					}
					if(Main.currentScreen instanceof GuiInventory) {
						((GuiInventory)Main.currentScreen).onDisplayUpdate();
					}
				}
			}
			setFullScreen = true;
		} else {
			setFullScreen = false;
		}

		if(!Display.isFullscreen()) {
			if((gameCanvas.getWidth() != Main.WIDTH || gameCanvas.getHeight() != Main.HEIGHT)) {

				Main.WIDTH = gameCanvas.getWidth();
				Main.HEIGHT = gameCanvas.getHeight();
				if(Main.WIDTH <= 0) {
					Main.WIDTH = 1;
				}

				if(Main.HEIGHT <= 0) {
					Main.HEIGHT = 1;
				}

				GL11.glViewport(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
				MasterRenderer.getInstance().updateProjectionMatrix();
				for(GuiComponent comp : GuiComponent.components) {
					comp.updateComponent();
				}
				if(Main.currentScreen instanceof GuiInventory) {
					((GuiInventory)Main.currentScreen).onDisplayUpdate();
				}
			}
		}
		keyboardnext = Keyboard.next();
		
		//Display.sync(60);
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime)/1000f;
		lastFrameTime = currentFrameTime;		
	}
	private static boolean keyboardnext = false;
	public static boolean keyboardHasNext() {
		return keyboardnext;
	}

	/**
	 * Captures a frame of the screen (getImage()) and saves it to the screenshots folder.
	 * 
	 * @see getImage()
	 */
	public static void saveScreenshot() {

		File saveDirectory =  new File(Main.getDir().getPath()+"/screenshots/");

		if (!saveDirectory.exists()) {
			try {
				saveDirectory.mkdir();
			} catch (SecurityException e) {
				return;
			}
		}
		
		String name = Logger.getCurrentTimeFile();

		File file = new File(saveDirectory + "/" + name + ".png"); // The file to save the pixels too.
		String format = "png";
		try {
			// Save the BufferedImage object to a file using the ImageIO.write() method
			ImageIO.write(getImage(null,null), format, file);
			
			// Print a message indicating that the screenshot has been saved
			Logger.log(LogLevel.INFO, "Saved screenshot!");
			
		} catch (Exception e) {
			Main.error("Failed to save screenshot! : " + name+".png", e);
		}
	}

	/**
	 * Destroys the display (not the program)
	 */
	public static void closeDisplay() {
		activeDisplay = false;
		Display.destroy();
	}	

	/**
	 * Updates FPS counter.
	 */
	static void updateFPS() {
		if (getCurrentTime() - lastFPS > 1000)	{
			literalfps = fps;
			fps = 0; //reset the FPS counter
			lastFPS += 1000; //add one second
			//System.gc();
		}
		fps++;
	}

	/**
	 * Gets frames per second
	 * @return {@link Integer}
	 */
	public static int getFPSCount() {
		return (int)literalfps;
	}

	private static long getCurrentTime() {
		return Sys.getTime()*1000/Sys.getTimerResolution();
	}

	public static float getDelta() {
		return delta + 1;
	}


	/**
	 * Creates a buffered image from the OpenGL pixel buffer.
	 *
	 * @param destination The destination BufferedImage to store in, if null a new one will be created.
	 * @param buffer The buffer to store OpenGL data into, if null a new one will be created.
	 *
	 * @return A new buffered image containing the displays data.
	 */
	public static BufferedImage getImage(BufferedImage destination, ByteBuffer buffer) {
		// Creates a new destination if it does not exist, or fixes a old one,
		if (destination == null || buffer == null || destination.getWidth() != Display.getWidth() || destination.getHeight() != Display.getHeight()) {
			destination = new BufferedImage(Display.getWidth(), Display.getHeight(), BufferedImage.TYPE_INT_RGB);
			buffer = BufferUtils.createByteBuffer(Display.getWidth() * Display.getHeight() * 4);
		}

		// Creates a new buffer and stores the displays data into it.
		GL11.glReadPixels(0, 0, Display.getWidth(), Display.getHeight(), GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

		// Transfers the data from the buffer into the image. This requires bit shifts to get the components data.
		for (int x = destination.getWidth() - 1; x >= 0; x--) {
			for (int y = destination.getHeight() - 1; y >= 0; y--) {
				int i = (x + Display.getWidth() * y) * 4;
				destination.setRGB(x, destination.getHeight() - 1 - y, (((buffer.get(i) & 0xFF) & 0x0ff) << 16) | (((buffer.get(i + 1) & 0xFF) & 0x0ff) << 8) | ((buffer.get(i + 2) & 0xFF) & 0x0ff));
			}
		}

		return destination;
	}

	/**
	 * Returns delta time
	 * @return deltaTim
	 */
	public static float getFrameTimeSeconds() {
		return delta;
	}
	/**
	 * Converts mouse coordinates from (pixels) screen to window (float).
	 * <br>
	 * -1f to 1f
	 * 
	 * @return
	 */
	public static Vector2f getNormalisedMouseCoords() {
		float normalisedX = -1.0f + 2.0f * (float) Mouse.getX() / (float) Display.getWidth();
		float normalisedY = 1.0f - 2.0f * (float) Mouse.getY() / (float) Display.getHeight();
		return new Vector2f(normalisedX, normalisedY);
	}

	/**
	 * Returns Window size as OpenGL coordinates.
	 * @param position - ({@link Vector2f})
	 * @param scale - ({@link Vector2f})
	 * @return {@link Vector3f}
	 */
	public static Vector2f getNormalizedDeviceCoords(Vector2f position, Vector2f scale) {
		float x = (((2f * position.x + scale.x) / Display.getWidth()) - 1f);
		float y = ((((2f * position.y +  scale.y) / Display.getHeight()) - 1f) * -1f);
		return new Vector2f(x, y);
	}
}

