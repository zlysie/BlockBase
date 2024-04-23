package net.oikmo.engine.save;

import java.io.Serializable;

@SuppressWarnings("serial")
public class InventorySaveData implements Serializable {
	
	private Slot[][] slots;
	public int rows;
	public int columns;
	
	public InventorySaveData(Slot[][] slots, int rows, int columns) {
		this.slots = slots;
		this.rows = rows;
		this.columns = columns;
	}
	
	public Slot[][] getSlots() {
		return slots;
	}

	public static class Slot implements Serializable {
		private String itemID;
		private int currentAmount;
		
		public Slot(String itemID, int currentAmount) {
			this.itemID = itemID;
			this.currentAmount = currentAmount;
		}
		
		public String getItemID() {
			return itemID;
		}
		
		public int getCurrentAmount() {
			return currentAmount;
		}
	}
}
