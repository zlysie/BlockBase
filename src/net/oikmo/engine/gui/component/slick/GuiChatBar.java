package net.oikmo.engine.gui.component.slick;

import org.lwjgl.input.Keyboard;

import net.oikmo.engine.DisplayManager;
import net.oikmo.engine.gui.Gui;

public class GuiChatBar  extends Gui implements GuiComponent {
	private String inputText = "";
	private GuiCommand command;

	private float x, y;

	private boolean lockButton = false;

	private boolean isHovering = false;

	public GuiChatBar(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		components.add(this);
		if(!lockButton) {
			if(!lockedRightNow && current != this) {
				Gui.lockedRightNow = true;
				lockButton = true;
				current = this;
			}
		}
	}

	@Override
	public void tick() {
		if(Keyboard.getEventKey() == Keyboard.KEY_RETURN) {
			if(command != null && !lockButton) {
				command.invoke();
				lockButton = true;
			}
		} else {
			lockButton = false;
		}
		
		if(DisplayManager.keyboardHasNext()) {
			this.handleKeyboardInput();
		}

		drawShadowString(x, y, inputText);
	}

	protected void handleKeyboardInput() {

		if(Keyboard.getEventKeyState()) {
			if(Keyboard.getEventKey() == Keyboard.KEY_BACK) {
				if(getInputText().length() != 0) {
					setInputText(getInputText().substring(0, getInputText().length()-1));
				}
			}
			keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
		}
	}

	protected void keyTyped(char c, int i) {
		if(getInputText().length() < 60) {
			if(isValidCharacter(c)) {
				setInputText(getInputText() + c);
			}
		}
	}

	private boolean isValidCharacter(char c) {
		return " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_'abcdefghijklmnopqrstuvwxyz{|}~\u2302\307\374\351\342\344\340\345\347\352\353\350\357\356\354\304\305\311\346\306\364\366\362\373\371\377\326\334\370\243\330\327\u0192\341\355\363\372\361\321\252\272\277\256\254\275\274\241\253\273".indexOf(c) >= 0;
	}

	public void updateComponent() {
		command.update();
		this.x = command.getX();
		this.y = command.getY();
	}

	@Override
	public void onCleanUp() {
		if(lockedRightNow && current == this) {
			current = null;
			lockedRightNow = false;
		}
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

	public boolean hasContent() {
		return inputText.length() != 0;
	}

	public String getText() {
		return inputText;
	}

	public GuiCommand getCommand() {
		return command;
	}


}