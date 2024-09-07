package net.oikmo.engine.world.blocks;

/**
 * Leaves from a tree... now imagine that as a block.
 * @author Oikmo
 */
public class BlockOakLeaves extends Block {
	
	/**
	 * Oak leaves 
	 * @param type Block type
	 */
	public BlockOakLeaves(Type type) {
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
