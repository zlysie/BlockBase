package net.oikmo.engine.world.blocks;

/**
 * Walk round them... bills
 * @author Oikmo
 */
public class BlockDiamondBlock extends Block {

	/**
	 * Diamond block constructor
	 * @param type Block type
	 */
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