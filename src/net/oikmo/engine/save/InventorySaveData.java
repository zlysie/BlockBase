package net.oikmo.engine.save;

import java.io.Serializable;

@SuppressWarnings("serial")
public class InventorySaveData implements Serializable {
	
	private Slot[][] slots;
	
	public InventorySaveData(Slot[][] slots) {
		this.slots = slots;
	}

	public static class Slot {
		private String itemID;
		private int maxStackSize;
		private int currentAmount;
		
		public Slot(String itemID, int maxStackSize, int currentAmount) {
			this.itemID = itemID;
			this.maxStackSize = maxStackSize;
			this.currentAmount = currentAmount;
		}
		
		public String getItemID() {
			return itemID;
		}
		
		public int getMaxStackSize() {
			return maxStackSize;
		}
		
		public int getCurrentAmount() {
			return currentAmount;
		}
	}
}
