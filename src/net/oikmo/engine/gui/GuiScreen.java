package net.oikmo.engine.gui;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.opengl.Texture;

import net.oikmo.engine.gui.component.button.GuiButton;
import net.oikmo.engine.gui.component.slider.GuiSlider;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.engine.textures.GuiTexture;
import net.oikmo.main.Main;

public class GuiScreen {

	private static UnicodeFont font;
	
	protected String screenID;
	protected GuiTexture background;
	protected GuiButton selectedButton;

	protected List<GuiTexture> uiList;
	protected List<GuiButton> buttonList;
	protected List<GuiSlider> sliderList;

	private boolean lockInput = false;
	private boolean dontUpdate = false;
	private boolean isUnableToExit = false;
	private static Font awtFont = null;
	protected static int fontSize = 18;

	@SuppressWarnings("unchecked")
	public static void initFont() {
		
		try {
			awtFont = Font.createFont(Font.TRUETYPE_FONT, Main.class.getResourceAsStream("/assets/fonts/minecraft.ttf"));
		} catch (FontFormatException | IOException e) {}
		
		ColorEffect effect = new ColorEffect();
		font = new UnicodeFont(awtFont.deriveFont(Font.PLAIN, fontSize));
		font.getEffects().add(effect);
		font.addAsciiGlyphs();
		try {
			font.loadGlyphs();
		} catch (SlickException e1) {
			e1.printStackTrace();
		}
	}

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

	public void init() {
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
	
	
	private void setupGL() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0,Display.getWidth(), Display.getHeight(), 0, -1, 1);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	private void dropGL() {
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
 	
	protected void drawString(float x, float y, String text) {
		setupGL();
		font.drawString(x, y, text);
		dropGL();	
	}
	
	protected void drawShadowString(float x, float y, String text) {
		setupGL();
		font.drawString(x+2, y+2, text, Color.gray);
		font.drawString(x, y, text,  Color.white);
		dropGL();
	}
	
	protected void drawImage(Texture texture, float x, float y, float width, float height) {
		setupGL();
		new Image(texture).draw(x-width, y-height, width, height);
		dropGL();
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
