package net.oikmo.engine.world.blocks;

public class BlockBedrock extends Block {

	public BlockBedrock(Type type) {
		super(type);
	}

	public boolean isSolid() {
		return true;
	}
	
	public float getStrength() {
		return Integer.MAX_VALUE;
	}
	
	public boolean blocksLight() {
		return true;
	}
	
}