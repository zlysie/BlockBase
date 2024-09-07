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
import net.oikmo.engine.network.packet.PacketChatMessage;
import net.oikmo.main.Main;

public class GuiChat extends GuiScreen {
	
	public GuiChat() {
		super("Connecting");
	}
	
	private Color c = new Color(0, 0, 0, 0.5f);
	private GuiChatBar bar;
	private int offset = font.getWidth(" > ");
	private int limit = 20;
	
	private List<ChatMessage> messages = new ArrayList<>(Main.theNetwork.rawMessages);
	
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
					ChatMessage message = new ChatMessage(" <" + Main.theNetwork.player.userName +"> " + bar.getInputText().trim(), false);
					Main.theNetwork.rawMessages.add(message);
					PacketChatMessage packet =new PacketChatMessage();
					packet.message = message.getMessage(); 
					Main.theNetwork.client.sendTCP(packet);
					updateMessages();
					bar.setInputText("");
				}
			}
		});
		bar.getCommand().update();
		updateMessages();
	}
	
	int ticks = 0;
	boolean showMarker = true;
	public void onTick() {
		ticks++;
		if(ticks > 30) {
			showMarker = !showMarker;
			ticks = 0;
		}
	}
	
	public void updateMessages() {
		this.messages = new ArrayList<>(Main.theNetwork.rawMessages);
		//Collections.reverse(messages);
	}
	
	public void onUpdate() {
		int base = Display.getHeight()-fontSize;
		int size = messages.size()-1;
		for(int y = size; y > -1; y--) {
			int realY = ((size-y)*fontSize)+fontSize;
			this.drawSquareFilled(c, 0, base-realY, Display.getWidth(), fontSize);
			this.drawShadowString(Main.theNetwork.rawMessages.get(y).isSpecial() ? Color.yellow : Color.white, 0, base-realY, messages.get(y).getMessage());
		}
		this.drawSquareFilled(c, 0, base, Display.getWidth(), fontSize);
		this.drawShadowString(0, base, " > ");
		if(showMarker) {
			this.drawShadowString(font.getWidth(" > ") + font.getWidth(bar.getInputText()), base, "_");
		}
		
		bar.tick();
		if(messages.size() > limit) {
			Main.theNetwork.rawMessages.remove(0);
			messages.remove(0);
		}
	}
	
	public void onClose() {
		Keyboard.enableRepeatEvents(false);
		Gui.cleanUp();
		if(Main.thePlayer != null) {
			Main.thePlayer.getCamera().setMouseLock(true);
		}
		
		bar.onCleanUp();
	}
}
