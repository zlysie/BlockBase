package net.oikmo.engine.world.blocks;

/**
 * Mossy cobblestone?? Ewww
 * @author Oikmo
 */
public class BlockMossyCobble extends Block {
	
	/**
	 * Mossy cobblestone constructor
	 * @param type Block type
	 */
	public BlockMossyCobble(Type type) {
		super(type);
	}

	public boolean isSolid() {
		return true;
	}
	
	public float getStrength() {
		return 0.4f;
	}
	
	public boolean blocksLight() {
		return true;
	}
}