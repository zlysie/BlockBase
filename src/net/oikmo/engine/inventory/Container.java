package net.oikmo.engine.inventory;

import net.oikmo.engine.save.InventorySaveData;
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
	
	public net.oikmo.engine.save.InventorySaveData saveContainer() {
		net.oikmo.engine.save.InventorySaveData.Slot[][] slots = new net.oikmo.engine.save.InventorySaveData.Slot[rows][columns];
		
		for (int y = 0; y < columns; y++) {
			for (int x = 0; x < rows; x++) {
				Slot s = this.slots[x][y];
				if(s != null) {
					slots[x][y] = new net.oikmo.engine.save.InventorySaveData.Slot(s.getItem().getID(), s.getCurrentAmount());
				}
			}
		}
		return new InventorySaveData(slots, rows, columns);
	}
	
	public static Container loadSavedContainer(InventorySaveData data) {
		Container cont = new Container(data.rows, data.columns);
		
		for (int y = 0; y < data.columns; y++) {
			for (int x = 0; x < data.rows; x++) {
				net.oikmo.engine.save.InventorySaveData.Slot slot = data.getSlots()[x][y];
				if(slot != null) {
					cont.slots[x][y] = new Slot(Item.getItemFromID(slot.getItemID()), slot.getCurrentAmount());
				}
				
			}
		}
		
		return cont;
	}
}
