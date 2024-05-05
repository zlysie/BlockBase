package net.oikmo.engine.world.blocks;

public class BlockTNT extends Block {

	public BlockTNT(Type type) {
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