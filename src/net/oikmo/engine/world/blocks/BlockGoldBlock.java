package net.oikmo.engine.world.blocks;

public class BlockGoldBlock extends Block {

	public BlockGoldBlock(Type type) {
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