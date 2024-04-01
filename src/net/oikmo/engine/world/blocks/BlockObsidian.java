package net.oikmo.engine.world.blocks;

public class BlockObsidian extends Block {
	
	public BlockObsidian(Type type) {
		super(type);
	}

	public boolean isSolid() {
		return true;
	}
	
	public float getStrength() {
		return 0.6f;
	}
	
	public boolean blocksLight() {
		return false;
	}
}