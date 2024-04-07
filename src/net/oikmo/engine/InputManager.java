package net.oikmo.engine;

import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.main.Main;

import org.lwjgl.input.Keyboard;

public class InputManager {
	
	private final int toggleUIKey = Keyboard.KEY_F1;
	private final int screenShotKey = Keyboard.KEY_F2;
	private final int texturePackKey = Keyboard.KEY_F3;
	private final int refreshKey = Keyboard.KEY_F;
	private final int saveKey = Keyboard.KEY_R;
	private final int loadKey = Keyboard.KEY_T;
	
	private boolean lockInScreenshot = false;
	private boolean lockInChangeTexture = false;
	private boolean lockInUI = false;
	private boolean lockInRefresh = false;
	private boolean lockInWorldSave = false;
	private boolean lockInWorldLoad = false;
	
	public void handleInput() {
		if(Keyboard.isKeyDown(refreshKey)) {
			if(!lockInRefresh) {
				Main.theWorld.refreshChunks();
			}
			
			lockInRefresh = true;
		} else {
			lockInRefresh = false;
		}
		
		if(Keyboard.isKeyDown(saveKey)) {
			if(!lockInWorldSave) {
				Main.theWorld.saveWorld();
			}
			
			lockInWorldSave = true;
		} else {
			lockInWorldSave = false;
		}
		
		if(Keyboard.isKeyDown(loadKey)) {
			if(!lockInWorldLoad) {
				Main.theWorld.loadWorld();
			}
			
			lockInWorldLoad = true;
		} else {
			lockInWorldLoad = false;
		}
		
		if(Keyboard.isKeyDown(toggleUIKey)) {
			if(!lockInUI) {
				Main.currentScreen.onClose();
			}
			lockInUI = true;
		} else {
			lockInUI = false;
		}
		
		if(Keyboard.isKeyDown(screenShotKey)) {
			if(!lockInScreenshot) {
				DisplayManager.saveScreenshot();
			}
			lockInScreenshot = true;
		} else {
			lockInScreenshot = false;
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
	
}
