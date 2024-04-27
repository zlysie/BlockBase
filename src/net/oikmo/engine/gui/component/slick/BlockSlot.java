package net.oikmo.engine.gui.component.slick;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import net.oikmo.engine.gui.Gui;
import net.oikmo.engine.inventory.Item;
import net.oikmo.engine.sound.SoundMaster;
import net.oikmo.main.Main;

public class BlockSlot extends Gui implements GuiComponent {
	private Item item;
	
	private float x, y, width=32, height=32;

	private boolean lockButton = false;
	
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
			
			if(Mouse.isButtonDown(0)) {
				if(!lockButton) {
					SoundMaster.playSFX("ui.button.click");
					Main.inGameGUI.setSelectedItem(item);
					lockButton = true;
				}
			} else {
				lockButton = false;
			}
		}
		
		drawSquareFilled(Color.black, x-(width/2), y-(height/2), width, height);
		drawImage(item.getImage(), x, y, width, height);
	}
	
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Item getItem() {
		return item;
	}

	@Override
	public void onCleanUp() {}

	
}
