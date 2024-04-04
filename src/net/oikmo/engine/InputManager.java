package net.oikmo.engine;

import net.oikmo.main.Main;

import org.lwjgl.input.Keyboard;

public class InputManager {
	
	private final int refreshKey = Keyboard.KEY_F;
	private final int saveKey = Keyboard.KEY_R;
	private final int loadKey = Keyboard.KEY_T;
	
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
	}
	
}
