package net.oikmo.main.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.gui.component.slick.BlockSlot;
import net.oikmo.engine.gui.component.slick.inventory.InventoryContainer;
import net.oikmo.engine.gui.component.slick.inventory.InventorySlot;
import net.oikmo.engine.inventory.Container;
import net.oikmo.engine.inventory.Item;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.main.Main;

public class GuiInventory extends GuiScreen {
	
	private Container cont = Main.thePlayer.getInventory();
	
	private InventoryContainer playerInv;
	
	private List<BlockSlot> slots =  new ArrayList<BlockSlot>();
	
	private int width = 300, height = 300;
	
	private int xPos = (Display.getWidth()/2)-width/2;
	private int yPos = (Display.getHeight()/2)-height/2;
	
	public GuiInventory() {
		super("In Game");
	}
	public void onInit() {
		Main.thePlayer.getCamera().setMouseLock(false);		
		
		List<BlockSlot> slots =  new ArrayList<BlockSlot>();
		this.slots = slots;
	}
	
	private boolean refreshAtStart = false;
	
	private int slotSize = 32;
	public void onUpdate() {
		if(!refreshAtStart) {
			onDisplayUpdate();
			refreshAtStart = true;
		}
		xPos = (Display.getWidth()/2)-width/2;
		yPos = (Display.getHeight()/2)-height/2;
		drawSquareFilled(Color.lightGray, xPos, yPos, width, height);
		drawSquare(Color.gray, 4, xPos, yPos, width, height);
		drawShadowStringCentered(xPos+(width/2), yPos+10, Main.lang.translateKey("inventory.title"));		
		
		for(int i = 0; i < slots.size(); i++) {
			slots.get(i).tick();
		}
	}
	
	public void onDisplayUpdate() {
		slots.clear();
		
		int xOffset = 0;
		int yOffset = 0;

		xPos += 20;
		yPos += 20;
		
		int test = Block.blocks.length-1;
		int yIndex = 0;
		while(test-7 > 0) {
			test -= 7;
			yIndex++;
		}
		
		int xOff = 0;
		
		for(int y = 0; y < yIndex+1; y++) {
			if(y == 0) {
				yOffset += 16;
			} else {
				yOffset += 8;
			}
			for(int x = 0; x <= 6; x++) {
				Item item = Item.blockToItem(Block.blocks[xOff]);
				
				if(x == 0) {
					slots.add(new BlockSlot(item, xPos+(x*32)+8, yPos+(y*32)+8+yOffset));
					xOffset += 16;
				} else {
					slots.add(new BlockSlot(item, xPos+(x*32)+xOffset, yPos+(y*32)+8+yOffset));
					xOffset += 8;
				}
				if(xOff < Block.blocks.length-2) {
					xOff++;
				} else {
					break;
				}
			}
			xOffset = 0;
		}

	}
	
	public void updateInventory() {
		for(int y = 0; y < playerInv.getColumns(); y++) {
			for(int x = 0; x < playerInv.getRows(); x++) {
				if(playerInv.getSlots()[x][y] != null) {
					playerInv.getSlots()[x][y].tick();
				}
			}
		}
		
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
		for(BlockSlot slot : slots) {
			slot.onCleanUp();
		}
		InventorySlot.dropCurrent();
	}
}
