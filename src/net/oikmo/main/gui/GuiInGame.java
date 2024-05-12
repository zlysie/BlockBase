package net.oikmo.main.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import net.oikmo.engine.DisplayManager;
import net.oikmo.engine.gui.Gui;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.inventory.Container;
import net.oikmo.engine.inventory.Item;
import net.oikmo.engine.inventory.Slot;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.main.Main;
import net.oikmo.network.shared.PacketUpdateWithheldBlock;

public class GuiInGame extends GuiScreen {
	
	private boolean literallyUpdate = false;
	private Image hotbar, hotbarSelector;
	private Image crosshair;
	private int selectedIndex = 0;
	//private UnicodeFont font = calculateFont(fontSize-4);
	
	public GuiInGame() {
		super("In Game");
	}
	
	public void onInit() {
		this.hotbar = Gui.guiAtlas.getSubImage(0, 0, 182, 22);
		this.hotbar.setFilter(Image.FILTER_NEAREST);
		this.hotbarSelector = Gui.guiAtlas.getSubImage(0, 22, 24, 24);
		this.hotbarSelector.setFilter(Image.FILTER_NEAREST);
		this.crosshair = Gui.guiAtlas.getSubImage(240, 0, 16, 16);
	}
	
	private Color c = new Color(0, 0, 0, 0.5f);
	
	public void onUpdate() {
		if(Main.thePlayer == null) { return; }
		Container cont = Main.thePlayer.getInventory();
		
		drawShadowString(0f, 0f, Main.gameVersion);
		if(literallyUpdate) {
			drawShadowString(0, fontSize, "FPS: " + DisplayManager.getFPSCount());
			Vector3f v = Main.thePlayer.getRoundedPosition();
			drawShadowString(0, Display.getHeight()-fontSize, "X: "+ (int)v.x + " Y: "+ (int)v.y + " Z: "+ (int)v.z +" ");
		}
		
		drawImage(crosshair, Display.getWidth()/2, Display.getHeight()/2, 20f, 20f);
		drawImage(hotbar, Display.getWidth()/2, Display.getHeight()-28, 364,44);
		
		
		for(int i = 0; i < cont.getRows(); i++) {
			Slot s = cont.getSlots()[i][0];
			if(s != null) {
				drawImage(s.getItem().getImage(), calculateXPosition(i), Display.getHeight()-28, 28,28);
				/*String amount = s.getCurrentAmount()+"";
				int height = font.getHeight(amount);
				drawShadowStringCentered(font, calculateXPosition(i)+12,(Display.getHeight()-28)+height-(height/4),amount);*/
			}
		}
		
		drawImage(hotbarSelector, calculateXPosition(selectedIndex), Display.getHeight()-28, 48,48);
		
		
		
		int previousIndex = selectedIndex;
		if(!(Main.currentScreen instanceof GuiChat)) {
			if(Main.theNetwork != null) {
				int base = Display.getHeight()-fontSize;
				int size = Main.theNetwork.currentlyShownMessages.size()-1;
				for(int y = size; y > -1; y--) {
					int realY = ((size-y)*fontSize)+fontSize;
					this.drawSquareFilled(c, 0, base-realY, Display.getWidth(), fontSize);
					this.drawShadowString(Main.theNetwork.currentlyShownMessages.get(y).isSpecial() ? Color.yellow : Color.white, 0, base-realY, Main.theNetwork.currentlyShownMessages.get(y).getMessage());
					
				}
			}
			
			if (DisplayManager.keyboardHasNext()) {
				if (Keyboard.getEventKeyState()) {
					try {
						int i = Integer.parseInt(Keyboard.getKeyName(Keyboard.getEventKey()))-1;
						selectedIndex = i != -1 ? i : 0;
					} catch(Exception e) {}
				}
			}
			
			int dWheel = Mouse.getDWheel();
		    if (dWheel < 0) {
		    	selectedIndex += 1;
				selectedIndex = selectedIndex > 8 ? 0 : selectedIndex;
		    } else if (dWheel > 0){
		    	selectedIndex -= 1;
				selectedIndex = selectedIndex <= -1 ? 8 : selectedIndex;
		    }
		    
		    if(selectedIndex != previousIndex) {
		    	updatePlayerHand();
		    }
		}
	}
	
	private void updatePlayerHand() {
		if(Main.theNetwork != null) {
    		PacketUpdateWithheldBlock packet = new PacketUpdateWithheldBlock();
    		if(Main.thePlayer.getInventory().getSlots()[selectedIndex][0] != null) {
    			packet.block = Item.itemToBlock(Main.thePlayer.getInventory().getSlots()[selectedIndex][0].getItem()).getByteType();
    		}
    		
    		Main.theNetwork.client.sendUDP(packet);
    	}
    	
	}

	public void setSelectedItem(Item item) {
		Main.thePlayer.getInventory().getSlots()[selectedIndex][0] = new Slot(item);
		updatePlayerHand();
	}
	
	public Block getSelectedItem() {
		if(Main.thePlayer.getInventory().getSlots()[selectedIndex][0] != null) {
			return Item.itemToBlock(Main.thePlayer.getInventory().getSlots()[selectedIndex][0].getItem());
		}
		return null;
	}
	
	private int calculateXPosition(int index) {
		
		int yep = 160-(index*40);
		return (Display.getWidth()/2)-yep;
	}

	public void toggleDebug() {
		literallyUpdate = !literallyUpdate;
	}
}
