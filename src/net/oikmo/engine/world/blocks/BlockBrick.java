package net.oikmo.engine.world.blocks;

/**
 * Bricks, nice in style, could make great homes
 * @author Oikmo
 */
public class BlockBrick extends Block {
	
	/**
	 * Brick constructor
	 * @param type Block tyoe
	 */
	public BlockBrick(Type type) {
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