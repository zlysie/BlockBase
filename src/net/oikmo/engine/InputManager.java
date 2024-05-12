package net.oikmo.engine;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.main.Main;
import net.oikmo.main.gui.GuiChat;
import net.oikmo.main.gui.GuiInventory;
import net.oikmo.main.gui.GuiPauseMenu;

public class InputManager {

	public static final int texturePackKey = Keyboard.KEY_F1;
	public static final int screenShotKey = Keyboard.KEY_F2;
	public static final int debugKey = Keyboard.KEY_F3;
	
	public static final int pauseEscapeKey = Keyboard.KEY_ESCAPE;
	
	public static final int refreshKey = Keyboard.KEY_F;
	public static final int inventoryKey = Keyboard.KEY_E;
	public static final int teleportKey = Keyboard.KEY_P;

	public static final int chatKey = Keyboard.KEY_T;
	//public static final int itemKey = Keyboard.KEY_Y;
	
	public static boolean lockInPause = false;
	
	public static boolean lockInChangeTexture = false;
	public static boolean lockInScreenshot = false;
	public static boolean lockInDebug = false;
	
	public static boolean lockInRefresh = false;
	//public static boolean lockInItem = false;

	public void handleInput() {
		if(!Main.isPaused()) {
			
			/*if(Keyboard.isKeyDown(itemKey)) {
				if(!lockInItem) {
					ItemBlock block = new ItemBlock(Main.thePlayer.getCamera().getCurrentlySelectedBlock(), new Vector3f(Main.thePlayer.getCamera().getPosition()));
					block.setRotation(0.0f, Main.thePlayer.getCamera().getYaw()-90, 0.0f);
					block.moveRelative(1, 0, 0.1f);
					//block.setPosition(block.getRoundedPosition());
					Main.theWorld.addEntity(block);
				}
				lockInItem = true;
			} else {
				lockInItem = false;
			}*/
			
			if(!(Main.currentScreen instanceof GuiChat)) {
				if(Keyboard.isKeyDown(teleportKey)) {
					Main.thePlayer.resetPos();
				}
				
				if(Keyboard.isKeyDown(refreshKey)) {
					if(!lockInRefresh) {
						Main.theWorld.refreshChunks();
					}

					lockInRefresh = true;
				} else {
					lockInRefresh = false;
				}

				if(Keyboard.isKeyDown(texturePackKey)) {
					if(!lockInChangeTexture) {
						int texture = MasterRenderer.currentTexturePack.getTextureID();
						if(texture == MasterRenderer.defaultTexturePack) {
							MasterRenderer.getInstance().setTexturePack(MasterRenderer.customTexturePack);
						} else {
							MasterRenderer.getInstance().setTexturePack(MasterRenderer.defaultTexturePack);
						}
					}
					lockInChangeTexture = true;
				} else {
					lockInChangeTexture = false;
				}
			}
			

			

			if(Keyboard.isKeyDown(debugKey)) {
				if(!lockInDebug) {
					Main.inGameGUI.toggleDebug();
				}
				lockInDebug = true;
			} else {
				lockInDebug = false;
			}

			
			if(Keyboard.isKeyDown(inventoryKey)) {
				if(Main.currentScreen == null) {
					Main.currentScreen = new GuiInventory();
				}
			}
			
			if(Main.theNetwork != null && Keyboard.isKeyDown(chatKey) && Main.currentScreen == null) {
				Main.currentScreen = new GuiChat();
			}
		}
		
		if(Main.inGameGUI != null) {
			if(Keyboard.isKeyDown(screenShotKey)) {
				if(!lockInScreenshot) {
					DisplayManager.saveScreenshot();
				}
				lockInScreenshot = true;
			} else {
				lockInScreenshot = false;
			}
		}
		
		if((Main.inGameGUI != null) && (Main.currentScreen instanceof GuiPauseMenu || Main.currentScreen instanceof GuiInventory || Main.currentScreen == null || Main.currentScreen instanceof GuiChat)) {
			if(!lockInPause) {
				if(Keyboard.isKeyDown(pauseEscapeKey)) {
					
					if(Main.currentScreen != null) {
						Main.currentScreen.prepareCleanUp();
						Main.currentScreen = null;
					} else {
						Main.currentScreen = new GuiPauseMenu();
					}
					lockInPause = true;
				}
			} else {
				if(!Keyboard.isKeyDown(pauseEscapeKey)) {
					lockInPause = false;
				}
			}
		}
		
	}
	
	public static boolean isMoving() {
		return Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_SPACE);
	}
	
	public static int lastMouseDX = Mouse.getDX();
	public static int lastMouseDY = Mouse.getDY();
	public static boolean hasMouseMoved() {
		int mouseDX = Mouse.getDX();
		int mouseDY = Mouse.getDX();
		if( mouseDX != lastMouseDX || mouseDY != lastMouseDY) {
			lastMouseDX = Mouse.getDX();
			lastMouseDY = Mouse.getDY();
			return true;
		}
		return false;
	}

}
