package net.oikmo.engine.world.blocks;

/**
 * Isn't this meant to be sharp?
 * @author Oikmo
 */
public class BlockObsidian extends Block {
	
	/**
	 * Obsidian block constructor
	 * @param type Block type
	 */
	public BlockObsidian(Type type) {
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