package net.oikmo.engine.gui.component.slick;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import net.oikmo.engine.gui.Gui;
import net.oikmo.engine.gui.component.slick.inventory.InventorySlot;

public class Cursor extends Gui implements GuiComponent {
	
	private float x, y;
	
	@Override
	public void tick() {
		x = Mouse.getX() + 10;
		
		
		if(BlockSlot.currentlyHoveringSlot != null) {
			String name = BlockSlot.currentlyHoveringSlot.getItem().getName();
			int width = font.getWidth(name) + 12;
			
			int nameHeight = font.getHeight(name);
			int height = nameHeight + 4;
			
			y = Math.abs(Display.getHeight()-Mouse.getY()) -2-height;
			
			this.drawSquareFilled(x, y, width, height+4);
			this.drawSquare(Color.white, 5f, x, y, width, height+4);
			this.drawString(x+4, y+2, name);
		}
		
		if(InventorySlot.currentlyHoveringSlot != null) {
			String name = InventorySlot.currentlyHoveringSlot.getSlot().getItem().getName()+ " (" +  InventorySlot.currentlyHoveringSlot.getSlot().getCurrentAmount() + ")";
			List<String> strings = splitEqually(InventorySlot.currentlyHoveringSlot.getSlot().getItem().getDescription(), 16);
			
			int width = font.getWidth(name) + 12;
			
			int nameHeight = font.getHeight(name);
			int height = nameHeight + 4;
			
			for(String string : strings) {
				int textWidth = font.getWidth(string) + 12;
				if(width < textWidth) {
					width = textWidth;
				}
				
				height += font.getHeight(string) + 4;
			}
			
			y = Math.abs(Display.getHeight()-Mouse.getY()) -2-height;
			
			this.drawSquareFilled(x, y, width, height+4);
			this.drawSquare(Color.white, 5f, x, y, width, height+4);
			this.drawString(x+4, y+2, name);
			
			int h = nameHeight + 4;
			for(String string : strings) {
				
				this.drawString(Color.lightGray, x+4, y+h, string);
				h += (fontSize) + 2;
			}
		}
	}
	
	@Override
	public void updateComponent() {}
	
	@Override
	public void onCleanUp() {
		
	}
	
	public List<String> splitEqually(String text, int size) {
	    // Give the list the right capacity to start with. You could use an array
	    // instead if you wanted.
	    List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

	    for (int start = 0; start < text.length(); start += size) {
	        ret.add(text.substring(start, Math.min(text.length(), start + size)));
	    }
	    return ret;
	}
}
