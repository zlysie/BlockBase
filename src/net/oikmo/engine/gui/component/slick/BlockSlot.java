package net.oikmo.engine.gui.component.slick;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import net.oikmo.engine.gui.Gui;
import net.oikmo.engine.inventory.Item;
import net.oikmo.engine.sound.SoundMaster;
import net.oikmo.main.Main;

/**
 * The block item that shows in the creative menu
 * @author Oikmo
 */
public class BlockSlot extends Gui implements GuiComponent {
	/** Actively hovering slot */
	public static BlockSlot currentlyHoveringSlot;
	/** True if any instance of the slot is being selected */
	private static boolean lockedRightNow = false;
	
	/** If the mouse is hovering over the slot */
	private boolean isHovering;
	
	/** What item does it hold */
	private Item item;
	
	/** Dimensions of the slot */
	private float x, y, width=32, height=32;
	
	/** To prevent action being repeated in multiple frames at once */
	private boolean lockButton = false;
	
	/**
	 * BlockSlot constructor
	 * @param item Item to store in slot
	 * @param x X coordinate of where slot should be
	 * @param y Y coordinate of where slot should be
	 */
	public BlockSlot(Item item, float x, float y) {
		this.item = item;
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void updateComponent() {}

	@Override
	public void tick() {
		float mouseX = Mouse.getX();
		float mouseY = Math.abs(Display.getHeight()-Mouse.getY());

		if(y + height/2 > mouseY && y-height/2 < mouseY && x + width/2 > mouseX && x-width/2 < mouseX) {
			isHovering = true;
			if(Mouse.isButtonDown(0)) {
				if(!lockButton) {
					SoundMaster.playSFX("ui.button.click");
					Main.inGameGUI.setSelectedItem(item);
					lockButton = true;
				}
			} else {
				lockButton = false;
			}
		} else {
			isHovering = false;
		}
		
		if(isHovering && !lockedRightNow && currentlyHoveringSlot != this) {
			currentlyHoveringSlot = this;
			lockedRightNow = true;
		}
		
		if(!isHovering && lockedRightNow && currentlyHoveringSlot == this) {
			currentlyHoveringSlot = null;
			lockedRightNow = false;
		}
		
		drawSquareFilled(Color.black, x-(width/2), y-(height/2), width, height);
		drawImage(item.getImage(), x, y, width, height);
	}
	
	/**
	 * Sets the position of the slot
	 * @param x X position to be set to
	 * @param y Y position to be set to
	 */
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Returns the stored item
	 * @return {@link Item}
	 */
	public Item getItem() {
		return item;
	}

	@Override
	public void onCleanUp() {}

	/** Drops the active static instance */
	public static void dropCurrent() {
		currentlyHoveringSlot = null;
		lockedRightNow = false;
	}
}
