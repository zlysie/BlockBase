package net.oikmo.engine.inventory;

public class Slot {
	
	private Item item;
	private int maxStackSize;
	private int currentAmount;
	
	public Slot(Item item) {
		this.item = item;
		this.maxStackSize = item.getMaxStackSize();
		this.currentAmount = 1;
	}

	public Item getItem() {
		return item;
	}

	public int getMaxStackSize() {
		return maxStackSize;
	}

	public int getCurrentAmount() {
		return currentAmount;
	}
	
	public int addItem() {
		currentAmount += 1;
		return currentAmount;
	}
	
	public boolean isNotMax() {
		return currentAmount + 1 > maxStackSize;
	}
}
