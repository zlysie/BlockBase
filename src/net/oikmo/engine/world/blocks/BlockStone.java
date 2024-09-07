package net.oikmo.engine.world.blocks;

/**
 * Stone block... The rock...
 * @author Oikmo
 */
public class BlockStone extends Block {
	
	/**
	 * Stone block constructor
	 * @param type Block type
	 */
	public BlockStone(Type type) {
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