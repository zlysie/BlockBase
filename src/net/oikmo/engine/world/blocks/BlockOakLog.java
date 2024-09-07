package net.oikmo.engine.world.blocks;

/**
 * Oak logs of a oak tree
 * @author Oikmo
 */
public class BlockOakLog extends Block {
	
	/**
	 * Oak log contructor
	 * @param type Block type
	 */
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
		return true;
	}
}