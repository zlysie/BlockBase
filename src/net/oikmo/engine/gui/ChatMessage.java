package net.oikmo.engine.gui;

import net.oikmo.main.Main;
import net.oikmo.main.gui.GuiChat;

public class ChatMessage {
	
	private int timer = 60*6;
	private String message;
	private boolean special;
	
	public ChatMessage(String message, boolean special) {
		this.message = message;
		this.special = special;
		Main.network.currentlyShownMessages.add(this);
		if(Main.currentScreen instanceof GuiChat) {
			((GuiChat)Main.currentScreen).updateMessages();
		}
	}
	
	public void tick() {
		timer--;
		if(timer <= 0) {
			Main.network.currentlyShownMessages.remove(this);
		}
	}
	
	public String getMessage() {
		return message;
	}
	
	public boolean isSpecial() {
		return special;
	}
}
