package net.oikmo.engine.world.blocks;

public class BlockDiamondBlock extends Block {

	public BlockDiamondBlock(Type type) {
		super(type);
	}

	public boolean isSolid() {
		return true;
	}
	
	public float getStrength() {
		return 1f;
	}
	
	public boolean blocksLight() {
		return true;
	}
	
}