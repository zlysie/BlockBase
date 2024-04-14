package net.oikmo.engine.gui.component.slick;

public class InventoryContainer {
	
	private InventorySlot[][] slots;
	
	private int rows, columns;
	
	public InventoryContainer(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		slots = new InventorySlot[rows][columns];
	}
	
	public int getRows() {
		return rows;
	}
	
	public int getColumns() {
		return columns;
	}
	
	public InventorySlot[][] getSlots() {
		return slots;
	}
}
