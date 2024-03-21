package net.oikmo.engine.chunk.blocks;

public class BlockBedrock extends Block {

	public BlockBedrock(Type type) {
		super(type);
	}

	public boolean isSolid() {
		return true;
	}
	
	public float getStrength() {
		return 1f;
	}
	
	public boolean blocksLight() {
		return false;
	}
	
}