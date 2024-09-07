package net.oikmo.engine.world.blocks;

/**
 * You can look through this one!
 * @author Oikmo
 */
public class BlockGlass extends Block {
	
	/**
	 * Glass block
	 * @param type Block type
	 */
	public BlockGlass(Type type) {
		super(type);
	}

	public boolean isSolid() {
		return true;
	}
	
	public float getStrength() {
		return 0.4f;
	}
	
	public boolean blocksLight() {
		return false;
	}
}