package net.oikmo.engine.world.blocks;

public class BlockOakLog extends Block {
	
	public BlockOakLog(Type type) {
		super(type);
	}

	public boolean isSolid() {
		return true;
	}
	
	public float getStrength() {
		return 0.5f;
	}
	
	public boolean blocksLight() {
		return false;
	}
}