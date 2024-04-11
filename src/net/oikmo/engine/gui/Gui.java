package net.oikmo.engine.gui;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.opengl.Texture;

import net.oikmo.main.Main;

public class Gui {

	protected static UnicodeFont font;
	
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
	
	protected static void init() {}
	
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
	
	protected void drawString(Color c, float x, float y, String text) {
		setupGL();
		font.drawString(x, y, text, c);
		dropGL();	
	}
	
	protected void drawShadowString(float x, float y, String text) {
		setupGL();
		font.drawString(x+2, y+2, text, Color.gray);
		font.drawString(x, y, text,  Color.white);
		dropGL();
	}
	
	protected void drawShadowString(Color c, float x, float y, String text) {
		setupGL();
		font.drawString(x+2, y+2, text, Color.gray);
		font.drawString(x, y, text, c);
		dropGL();
	}
	
	protected void drawImage(Texture texture, float x, float y, float width, float height) {
		setupGL();
		Image img = new Image(texture);
		img.setFilter(Image.FILTER_NEAREST);
		img.draw(x-width/2, y-height/2, width, height);
		dropGL();
	}
	
	protected void drawImageRaw(Texture texture, float x, float y, float width, float height) {
		setupGL();
		new Image(texture).draw(x, y, width, height);
		dropGL();
	}
}
