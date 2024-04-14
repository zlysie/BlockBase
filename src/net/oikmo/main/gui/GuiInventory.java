package net.oikmo.main.gui;

import org.lwjgl.opengl.Display;

import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.gui.component.slick.InventoryContainer;
import net.oikmo.engine.gui.component.slick.InventorySlot;
import net.oikmo.engine.inventory.Container;
import net.oikmo.main.Main;

public class GuiInventory extends GuiScreen {
	
	private Container cont = Main.thePlayer.getInventory();
	
	private InventoryContainer playerInv;
	
	public GuiInventory() {
		super("In Game");
	}
	public void onInit() {
		Main.thePlayer.getCamera().setMouseLock(false);
		
		cont = Main.thePlayer.getInventory();
		playerInv = new InventoryContainer(cont.getRows(), cont.getColumns());
	}
	
	private int slotSize = 32;
	public void onUpdate() {
		
		for(int y = 0; y < playerInv.getColumns(); y++) {
			for(int x = 0; x < playerInv.getRows(); x++) {
				if(playerInv.getSlots()[x][y] != null) {
					playerInv.getSlots()[x][y].tick();
				}
			}
		}
		updateInventory();
	}
	
	public void updateInventory() {
		
		for(int y = 0; y < cont.getColumns(); y++) {
			for(int x = 0; x < cont.getRows(); x++) {
				
				int actualX = ((Display.getWidth()/2)+(x*slotSize)) - ((cont.getRows()*slotSize)/2);
				int actualY = ((Display.getHeight()/2)+(y*slotSize)) - ((cont.getColumns()*slotSize)/2);
				if(cont.getSlots()[x][y] != null && playerInv.getSlots()[x][y] == null) {
					playerInv.getSlots()[x][y] = new InventorySlot(cont.getSlots()[x][y], actualX, actualY);
				} else if(cont.getSlots()[x][y] != null && playerInv.getSlots()[x][y] != null) {
					playerInv.getSlots()[x][y].setPosition(actualX, actualY);
				}
			}
		}
	}

	public void onClose() {
		Main.thePlayer.getCamera().setMouseLock(true);
		
		InventorySlot.dropCurrent();
	}
}
