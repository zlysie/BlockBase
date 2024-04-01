package net.oikmo.engine.world.blocks;

public class BlockOakLeaf extends Block {
	
	public BlockOakLeaf(Type type) {
		super(type);
	}

	public boolean isSolid() {
		return true;
	}
	
	public float getStrength() {
		return 0.1f;
	}
	
	public boolean blocksLight() {
		return false;
	}
	
}
