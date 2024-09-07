package net.oikmo.engine.gui;

import net.oikmo.main.Main;
import net.oikmo.main.gui.GuiChat;

/**
 * Those messages you get that disappear after a bit in chat
 * @author Oikmo
 */
public class ChatMessage {
	/** How long the message should last... (in ticks) */
	private int timer = 60*6;
	/** What the message is */
	private String message;
	/** Yellow text! */
	private boolean special;
	
	/**
	 * Adds itself to {@link net.oikmo.engine.network.client.NetworkHandler#currentlyShownMessages}
	 * @param message The message itself
	 * @param special Yellow text or nah
	 */
	public ChatMessage(String message, boolean special) {
		this.message = message;
		this.special = special;
		Main.theNetwork.currentlyShownMessages.add(this);
		if(Main.currentScreen instanceof GuiChat) {
			((GuiChat)Main.currentScreen).updateMessages();
		}
	}
	
	/**
	 * Every tick it counts down (decrements {@link #timer}) and removes itself from 
	 */
	public void tick() {
		timer--;
		if(timer <= 0) {
			Main.theNetwork.currentlyShownMessages.remove(this);
		}
	}
	
	/**
	 * Returns the message string
	 * @return {@link String}
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Returns true if it is yellow text
	 * @return {@link Boolean}
	 */
	public boolean isSpecial() {
		return special;
	}
}
