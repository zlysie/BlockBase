package net.oikmo.engine.inventory;

import net.oikmo.main.Main;
import net.oikmo.main.gui.GuiInventory;

public class Container {
	private Slot[][] slots;
	private int rows;
	private int columns;

	public Container(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;

		slots = new Slot[this.rows][this.columns];
	}

	public boolean addItem(Item item) {
		for (int y = 0; y < columns; y++) {
			for (int x = 0; x < rows; x++) {
				if (slots[x][y] != null && slots[x][y].getItem() == item) {
					if (!slots[x][y].isNotMax()) {
						slots[x][y].addItem();
						updateGuiInGame();
						return true;
					}
				}
			}
		}

		for (int y = 0; y < columns; y++) {
			for (int x = 0; x < rows; x++) {
				if (slots[x][y] == null) {
					slots[x][y] = new Slot(item);
					updateGuiInGame();
					return true;
				}
			}
		}

		//System.out.println("Inventory is full (boo womp). Cannot add item: " + item.itemName);
		return false;
	}
	public boolean canAddItem(Item item) {
		for (int x = 0; x < rows; x++) {
			for (int y = 0; y < columns; y++) {
				if (slots[x][y] == null) {
					return false;
				} else {
					if(slots[x][y].isNotMax() && slots[x][y].getItem() == item) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	private void updateGuiInGame() {	
		if(Main.currentScreen instanceof GuiInventory) {
			((GuiInventory)Main.currentScreen).updateInventory();
		}
	}
	
	public Slot[][] getSlots() {
		return slots;
	}

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}
}
