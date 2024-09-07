package net.oikmo.engine.world.blocks;

/**
 * Iron block, holds iron
 * @author Oikmo
 */
public class BlockIronBlock extends Block {

	/**
	 * Iron block constructor
	 * @param type Block type
	 */
	public BlockIronBlock(Type type) {
		super(type);
	}

	public boolean isSolid() {
		return true;
	}
	
	public float getStrength() {
		return 0.6f;
	}
	
	public boolean blocksLight() {
		return true;
	}
	
}