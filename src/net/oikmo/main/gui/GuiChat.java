package net.oikmo.main.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import net.oikmo.engine.gui.ChatMessage;
import net.oikmo.engine.gui.Gui;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.gui.component.slick.GuiChatBar;
import net.oikmo.engine.gui.component.slick.GuiCommand;
import net.oikmo.main.Main;
import net.oikmo.network.shared.PacketChatMessage;

public class GuiChat extends GuiScreen {
	
	public GuiChat() {
		super("Connecting");
	}
	
	private Color c = new Color(0, 0, 0, 0.5f);
	private GuiChatBar bar;
	private int offset = font.getWidth(" > ");
	private int limit = 20;
	
	private List<ChatMessage> messages = new ArrayList<>(Main.network.rawMessages);
	
	public void onInit() {
		Keyboard.enableRepeatEvents(true);
		Main.thePlayer.getCamera().setMouseLock(false);
		bar = new GuiChatBar(font.getWidth(" > "), Display.getHeight()-fontSize, Display.getWidth(), fontSize);
		
		bar.setGuiCommand(new GuiCommand() {
			public void update() {
				x = offset;
				y = Display.getHeight()-fontSize;
				width = Display.getWidth();
				height = fontSize;
			}
			
			public void invoke() {
				if(!bar.getInputText().trim().isEmpty()) {
					ChatMessage message = new ChatMessage(" <" + Main.network.player.userName +"> " + bar.getInputText().trim(), false);
					Main.network.rawMessages.add(message);
					PacketChatMessage packet =new PacketChatMessage();
					packet.message = message.getMessage(); 
					Main.network.client.sendTCP(packet);
					updateMessages();
					bar.setInputText("");
				}
			}
		});
		bar.getCommand().update();
		updateMessages();
	}
	
	public void updateMessages() {
		this.messages = new ArrayList<>(Main.network.rawMessages);
		//Collections.reverse(messages);
	}
	
	public void onUpdate() {
		int base = Display.getHeight()-fontSize;
		int size = messages.size()-1;
		for(int y = size; y > -1; y--) {
			int realY = ((size-y)*fontSize)+fontSize;
			this.drawSquareFilled(c, 0, base-realY, Display.getWidth(), fontSize);
			this.drawShadowString(Main.network.rawMessages.get(y).isSpecial() ? Color.yellow : Color.white, 0, base-realY, messages.get(y).getMessage());
		}
		this.drawSquareFilled(c, 0, base, Display.getWidth(), fontSize);
		this.drawShadowString(0, base, " > ");
		bar.tick();
		if(messages.size() > limit) {
			Main.network.rawMessages.remove(0);
			messages.remove(0);
		}
	}
	
	public void onClose() {
		Keyboard.enableRepeatEvents(false);
		Gui.cleanUp();
		Main.thePlayer.getCamera().setMouseLock(true);
		bar.onCleanUp();
	}
}
