package net.oikmo.engine.world.blocks;

/**
 * Cobblestone... cool i guess
 * @author Oikmo
 *
 */
public class BlockCobble extends Block {
	
	/**
	 * Cobble contructor
	 * @param type Block type
	 */
	public BlockCobble(Type type) {
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