package net.oikmo.engine.world.blocks;

/**
 * Walk round them... bills
 * @author Oikmo
 */
public class BlockGoldBlock extends Block {

	/**
	 * Gold block constructor
	 * @param type Block type
	 */
	public BlockGoldBlock(Type type) {
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