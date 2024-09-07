package net.oikmo.engine.world.blocks;

/**
 * Stone block but smooth...
 * @author Oikmo
 */
public class BlockSmoothStone extends Block {
	
	/**
	 * Smooth stone constructor
	 * @param type Block type
	 */
	public BlockSmoothStone(Type type) {
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