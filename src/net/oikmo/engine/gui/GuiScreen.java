package net.oikmo.engine.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import net.oikmo.engine.gui.component.button.GuiButton;
import net.oikmo.engine.gui.component.slider.GuiSlider;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.engine.textures.GuiTexture;
import net.oikmo.main.Main;

public class GuiScreen extends Gui {

	
	
	protected String screenID;
	protected GuiTexture background;
	protected GuiButton selectedButton;

	protected List<GuiTexture> uiList;
	protected List<GuiButton> buttonList;
	protected List<GuiSlider> sliderList;

	private boolean lockInput = false;
	private boolean dontUpdate = false;
	private boolean isUnableToExit = false;

	public GuiScreen(String screenID) {
		init(screenID);
	}
	public GuiScreen(int textureID, String screenID) {
		init(screenID, textureID, 20);
	}
	public GuiScreen(int textureID, String screenID, int tilingSize) {
		init(screenID, textureID, tilingSize);
	}

	private void init(String screenID, int textureID, int tilingSize) {
		this.background = new GuiTexture(textureID, new Vector2f(0,0), new Vector2f(1,1));
		this.background.setScale(new Vector2f(1,1));
		this.background.setTilingSize(tilingSize);
		init(screenID);
	}
	private void init(String screenID) { 

		this.screenID = screenID;
		this.buttonList = new ArrayList<GuiButton>();
		this.sliderList = new ArrayList<GuiSlider>();
		this.uiList = new ArrayList<>();
		onInit();
	}
	
	public void update() {
		if(!dontUpdate) {
			for(GuiTexture ui : uiList) {
				MasterRenderer.getInstance().addToGUIs(ui);
			}
			for(GuiButton comp : buttonList) {
				comp.update();
				MasterRenderer.getInstance().addToGUIs(comp.getTexture());
			}
			for(GuiSlider comp : sliderList) {
				comp.update();
				MasterRenderer.getInstance().addToGUIs(comp.getTexture());
				MasterRenderer.getInstance().addToGUIs(comp.getBackTexture());
			}

			if(background != null) {
				MasterRenderer.getInstance().addToGUIs(background);
			}
			handleInput();
			uiList.clear();
			buttonList.clear();
			sliderList.clear();
		}

		onUpdate();
	}
	
	public void prepareCleanUp() {
		dontUpdate = true;
		onClose();
	}	
	
	
	
 	
	
	public void draw(float x, float y, float scaleX, float scaleY, int texture) {
		GuiTexture ui = new GuiTexture(texture, new Vector2f(x, y), new Vector2f(scaleX, scaleY));
		ui.setScaleRelativeToScreen(ui.getScale());
		ui.setPositionRelativeToScreen(x, y);
		
		if(!uiList.contains(ui)) {
			uiList.add(ui);
		}
	}
	public void drawButton(int controlID, String label, float x, float y, float scaleX, float scaleY) {
		GuiButton ui = new GuiButton(controlID, new Vector2f(x, y), new Vector2f(scaleX, scaleY), label);
		if(!buttonList.contains(ui)) {
			buttonList.add(ui);
		}
	}
	public void drawSlider(int controlID, float defaultValue, String label, float x, float y, float scaleX, float scaleY) {
		GuiSlider ui = new GuiSlider(controlID, defaultValue, new Vector2f(x, y), new Vector2f(scaleX, scaleY), label);
		if(!sliderList.contains(ui)) {
			sliderList.add(ui);
		}
	}

	protected void actionPerformed(GuiButton guibutton) {}
	protected void actionPerformed(GuiSlider guislider) {}

	public void onInit() {}
	public void onUpdate() {}
	public void onClose() {}

	protected void handleInput() {
		try {
			for(;Mouse.next(); handleMouseInput()) {}
			for(;Keyboard.next(); handleKeyboardInput()) {}
		} catch(IllegalStateException e) {
			Main.error("Input Component Failed!", e);
		}
	}
	protected void handleMouseInput() {
		if(Mouse.getEventButtonState()) {
			mouseClicked(Mouse.getEventButton());
		} else {
			mouseMovedOrUp(Mouse.getEventButton());
		}
	}
	protected void handleKeyboardInput() {
		if(Keyboard.getEventKeyState()) {
			if(Keyboard.getEventKey() == Keyboard.KEY_F11) {
				//mc.toggleFullscreen();
				return;
			}
			keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
		}
	}

	protected void keyTyped(char c, int i) {
		if(i == 1) {}
	}

	protected void mouseMovedOrUp(int k) {
		if(selectedButton != null && k == 0) {
			if(!selectedButton.isHovering() && !selectedButton.isHidden()) {
				//play sound
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {}
				selectedButton = null;
			}
		}
	}
	protected void mouseClicked(int k) {
		if(k == 0) {
			for(int i = 0; i < buttonList.size(); i++) {
				GuiButton guibutton = buttonList.get(i);
				if(guibutton.isHovering()) {
					selectedButton = guibutton;
					//play sound
					actionPerformed(guibutton);
				}
			}

			for(int i = 0; i < sliderList.size(); i++) {
				GuiSlider guibutton = sliderList.get(i);
				if(guibutton.isHovering()) {
					//play sound
				}
			}
		}
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
