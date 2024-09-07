package net.oikmo.engine.world.blocks;

/**
 * Indestructible (maybe)
 * @author Oikmo
 */
public class BlockBedrock extends Block {
	
	/**
	 * Bedrock block constructor 
	 * @param type Block type
	 */
	public BlockBedrock(Type type) {
		super(type);
	}

	public boolean isSolid() {
		return true;
	}
	
	public float getStrength() {
		return Integer.MAX_VALUE;
	}
	
	public boolean blocksLight() {
		return true;
	}
	
}