package net.oikmo.engine.gui;

import org.lwjgl.input.Keyboard;

import net.oikmo.engine.textures.GuiTexture;

public class GuiScreen extends Gui {

	protected String screenID;
	protected GuiTexture background;

	private boolean lockInput = false;
	private boolean dontUpdate = false;
	private boolean isUnableToExit = false;

	public GuiScreen(String screenID) {
		this.screenID = screenID;
		onInit();
	}

	public void update() {
		if(dontUpdate) { return; }
		
		onUpdate();
	}

	public void prepareCleanUp() {
		dontUpdate = true;
		onClose();
	}

	public void onInit() {}
	public void onUpdate() {}
	public void onClose() {}

	protected void handleKeyboardInput() {
		if(Keyboard.getEventKeyState()) {
			keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
		}
	}

	protected void keyTyped(char c, int i) {
		if(i == 1) {}
	}

	public String getScreenID() {
		return screenID;
	}

	public boolean isLockInput() {
		return lockInput;
	}
	public void setLockInput() {
		this.lockInput = true;
	}
	public void setLockInput(boolean lock) {
		this.lockInput = lock;
	}

	public boolean isUnableToExit() {
		return isUnableToExit;
	}
	public void setUAE(boolean isUnableToExit) {
		this.isUnableToExit = isUnableToExit;
	}
}
