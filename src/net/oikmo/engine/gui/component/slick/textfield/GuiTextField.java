package net.oikmo.engine.gui.component.slick.textfield;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.gui.Gui;
import net.oikmo.engine.gui.component.slick.GuiCommand;
import net.oikmo.engine.gui.component.slick.GuiComponent;

public class GuiTextField  extends Gui implements GuiComponent {

	private Texture backgroundTexture;

	private String inputText = "";
	private GuiCommand command;

	private float x, y, width, height;

	private boolean lockButton = false;

	private boolean isHovering = false;
	private boolean grabbed = false;

	private void onInit() {
		if(backgroundTexture == null) {
			backgroundTexture = ResourceLoader.loadUITexture("ui/ui_button_background");
		}
	}

	public GuiTextField(float x, float y, float width, float height,GuiCommand command) {
		onInit();
		this.command = command;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		components.add(this);
	}

	public GuiTextField(float x, float y, float width, float height) {
		onInit();
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
			isHovering = true;
			if(Mouse.isButtonDown(0)) {
				if(command != null) {
					if(!lockButton) {
						if(!lockedRightNow && current != this) {
							grabbed = true; 
							Gui.lockedRightNow = true;
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
			if(Mouse.isButtonDown(0)) {
				grabbed = false;
				if(lockedRightNow && current == this) {
					current = null;
					lockedRightNow = false;
				}
			}
		}

		if(grabbed) {
			handleKeyboardInput();
		}

		drawImage(backgroundTexture, x, y, width, height);
		
		if(inputText.length() == 0) {
			drawShadowString(Color.darkGray, (x+3)-width/2, y-font.getHeight("Type here...")/2, "Type here...");
		} else {
			drawShadowString((x+3)-width/2, y-font.getHeight(inputText)/2, inputText);
		}
	}

	protected void handleKeyboardInput() {
		if(Keyboard.next()) {
			if(Keyboard.isKeyDown(Keyboard.KEY_BACK)) {
				if(getInputText().length() != 0) {
					setInputText(getInputText().substring(0, getInputText().length()-1));
				}
			}
			keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());

		}
	}

	protected void keyTyped(char c, int i) {
		if(isValidCharacter(c)) {
			if(x+width > x+font.getWidth(getInputText() + c)) {
				setInputText(getInputText() + c);
			}
		}
	}
	
	private boolean isValidCharacter(char c) {
		return Character.isAlphabetic(c) || Character.isDigit(c) || c == ' ' || c == '-' || c == '.' || c == '_';
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

	public String getInputText() {
		return inputText;
	}

	public void setInputText(String inputText) {
		this.inputText = inputText;
	}

	public boolean isGrabbed() {
		return grabbed;
	}
	
	public boolean hasContent() {
		return inputText.length() != 0;
	}

	public String getText() {
		return inputText;
	}


}