package net.oikmo.engine.gui.component.slick;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import net.oikmo.engine.gui.Gui;
import net.oikmo.engine.inventory.Slot;
import net.oikmo.engine.sound.SoundMaster;

public class InventorySlot extends Gui implements GuiComponent {

	public static InventorySlot currentlyHoveringSlot;
	private static boolean lockedRightNow = false;
	
	private Slot slot;
	
	private GuiCommand command;
	
	private float x, y, width=16, height=16;

	private boolean lockButton = false;

	private boolean isHovering = false;

	public InventorySlot(Slot slot, float x, float y) {
		this.slot = slot;
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
				if(command != null) {
					if(!lockButton) {
						SoundMaster.playSFX("ui.button.click");
						command.invoke();
						lockButton = true;
					}
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
		
		drawImg(slot.getItem().getImage(), x, y, width, height);
	}

	public static void dropCurrent() {
		currentlyHoveringSlot = null;
		lockedRightNow = false;
	}
	
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Slot getSlot() {
		return slot;
	}

	@Override
	public void onCleanUp() {}

	
}
