package net.oikmo.engine.gui.component.slick.button;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.opengl.Texture;

import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.gui.Gui;
import net.oikmo.engine.gui.component.slick.GuiComponent;

public class GuiButton extends Gui implements GuiComponent {

	private Texture texture;
	private static Texture normalTexture;
	private static Texture hoveredTexture;
	private String text;
	private GuiCommand command;

	private float x, y, width, height;
	private boolean lockButton = false;
	
	private void onInit() {
		if(normalTexture == null) {
			normalTexture = ResourceLoader.loadUITexture("ui/normal/ui_button");
		}
		if(hoveredTexture == null) {
			hoveredTexture = ResourceLoader.loadUITexture("ui/normal/ui_button_hover");
		}
	}
	
	public GuiButton(float x, float y, float width, float height, String text, GuiCommand command) {
		onInit();
		this.text = text;
		this.command = command;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public GuiButton(float x, float y, float width, float height, String text) {
		onInit();
		this.text = text;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void tick() {
		float mouseX = Mouse.getX();
		float mouseY = Math.abs(Display.getHeight()-Mouse.getY());

		if(y + height/2 > mouseY && y-height/2 < mouseY && x + width/2 > mouseX && x-width/2 < mouseX) {
			texture = hoveredTexture;
			if(Mouse.isButtonDown(0)) {
				if(command != null) {
					if(!lockButton) {
						command.invoke();
						lockButton = true;
					}
				}
			} else {
				lockButton = false;
			}
		} else {
			texture = normalTexture;
		}

		drawImage(texture, x, y, width, height);
		int textWidth = (font.getWidth(text)*2)-(font.getWidth(text)/2);
		int textHeight = font.getHeight(text);
		float width2 =  (width - textWidth)/2;
		float height2 = ((height - textHeight)+fontSize)/2;
		float textX = x + ((width2*2)+(width2/2))-width;
		float textY = y - height2/2;
		drawString(textX, textY,  text);
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void setGuiCommand(GuiCommand command) {
		this.command = command;
	}

	public Texture getTexture() {
		return texture;
	}

	public String getText() {
		return text;
	}
}