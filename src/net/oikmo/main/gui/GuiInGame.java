package net.oikmo.main.gui;

import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.gui.GuiScreen;

public class GuiInGame extends GuiScreen {
	public GuiInGame() {
		super("In Game");
	}
	
	private int texture;
	
	public void onInit() {
		texture = ResourceLoader.loadTexture("dirtTex");
		
	}
	
	public void onUpdate() {
		draw(0,0,1f,1f, texture);
	}
}
