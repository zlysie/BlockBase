package net.oikmo.engine.gui.component.slick.button;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.gui.Gui;
import net.oikmo.engine.gui.component.slick.GuiCommand;
import net.oikmo.engine.gui.component.slick.GuiComponent;
import net.oikmo.engine.sound.SoundMaster;

public class GuiButton extends Gui implements GuiComponent {

	private static Texture normalTexture;
	private static Texture hoveredTexture;
	
	private Texture texture = normalTexture;
	
	private String text;
	private GuiCommand command;

	private float x, y, width, height;
	private boolean lockButton = false;
	
	private boolean isHovering = false;

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
		components.add(this);
	}

	public GuiButton(float x, float y, float width, float height, String text) {
		onInit();
		this.text = text;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		components.add(this);
	}
	
	@Override
	public void tick() {
		float mouseX = Mouse.getX();
		float mouseY = Math.abs(Display.getHeight()-Mouse.getY());

		if(y + height/2 > mouseY && y-height/2 < mouseY && x + width/2 > mouseX && x-width/2 < mouseX) {
			//texture = hoveredTexture;

			isHovering = true;
			if(Mouse.isButtonDown(0)) {
				if(command != null) {
					if(!lockButton) {
						if(!lockedRightNow && current != this) {
							Gui.lockedRightNow = true;
							SoundMaster.playSFX("ui.button.click");
							command.invoke();
							lockButton = true;
							current = this;
						}
						
					}
				}
			} else {
				lockButton = false;
			}
		} else {
			isHovering = false;
			texture = normalTexture;
			if(lockedRightNow && current == this) {
				lockedRightNow = false;
				current = null;
				
			}
		}
		
		Image img = new Image(texture);
		if(isHovering) {
			img.setImageColor(0.85f, 0.85f, 2f);
		}
		drawImg(img, x, y, width, height);
		
		Color c = isHovering ? new Color(0.9f,0.9f,0.1f,1f) : Color.white;
		drawShadowStringCentered(c, x, y, text);
	}

	public void updateComponent() {
		command.update();
		this.x = command.getX();
		this.y = command.getY();
	}

	@Override
	public void onCleanUp() {
		components.remove(this);
	}

	public boolean isHovering() {
		return isHovering;
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