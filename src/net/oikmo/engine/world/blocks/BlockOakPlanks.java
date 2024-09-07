package net.oikmo.engine.world.blocks;

/**
 * Planks from an oak tree
 * @author Oikmo
 */
public class BlockOakPlanks extends Block {
	
	/**
	 * Oak planks constructor
	 * @param type Block type
	 */
	public BlockOakPlanks(Type type) {
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