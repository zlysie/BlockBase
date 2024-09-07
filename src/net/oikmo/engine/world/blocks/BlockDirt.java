package net.oikmo.engine.world.blocks;

/**
 * A block of dirt... wow...
 * @author Oikmo
 */
public class BlockDirt extends Block {
	
	/**
	 * Dirt block constructor
	 * @param type Block type
	 */
	public BlockDirt(Type type) {
		super(type);
	}
	
	public boolean isSolid() {
		return true;
	}
	
	public float getStrength() {
		return 0.2f;
	}
	
	public boolean blocksLight() {
		return true;
	}

}