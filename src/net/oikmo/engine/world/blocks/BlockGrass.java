package net.oikmo.engine.world.blocks;

/**
 * Dirt with a nice green blanket
 * @author Oikmo
 */
public class BlockGrass extends Block {

	/**
	 * Grass block constructor
	 * @param type Block type
	 */
	public BlockGrass(Type type) {
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
