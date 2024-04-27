package net.oikmo.engine;

import org.lwjgl.input.Keyboard;

import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.main.Main;
import net.oikmo.main.gui.GuiInventory;
import net.oikmo.main.gui.GuiMainMenu;
import net.oikmo.main.gui.GuiPauseMenu;
import net.oikmo.main.gui.GuiSelectWorld;

public class InputManager {

	private final int texturePackKey = Keyboard.KEY_F1;
	private final int screenShotKey = Keyboard.KEY_F2;
	private final int debugKey = Keyboard.KEY_F3;
	
	private final int pauseEscapeKey = Keyboard.KEY_ESCAPE;
	
	private final int refreshKey = Keyboard.KEY_F;
	private final int inventoryKey = Keyboard.KEY_E;
	private final int teleportKey = Keyboard.KEY_P;
	//private final int itemKey = Keyboard.KEY_Y;
	
	private boolean lockInPause = false;
	
	private boolean lockInChangeTexture = false;
	private boolean lockInScreenshot = false;
	private boolean lockInDebug = false;
	
	private boolean lockInRefresh = false;
	//private boolean lockInItem = false;

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

			if(Keyboard.isKeyDown(debugKey)) {
				if(!lockInDebug) {
					Main.inGameGUI.toggleDebug();
				}
				lockInDebug = true;
			} else {
				lockInDebug = false;
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

			
			
			if(Keyboard.isKeyDown(inventoryKey)) {
				if(Main.currentScreen == null) {
					Main.currentScreen = new GuiInventory();
				}
			}
		}
		

		
		
		if(!(Main.currentScreen instanceof GuiMainMenu) && !(Main.currentScreen instanceof GuiSelectWorld)) {
			if(Keyboard.isKeyDown(screenShotKey)) {
				if(!lockInScreenshot) {
					DisplayManager.saveScreenshot();
				}
				lockInScreenshot = true;
			} else {
				lockInScreenshot = false;
			}
			
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

}
