package net.oikmo.engine.world.blocks;

/**
 * This one EXPLODES!!!!
 * @author Oikmo
 */
public class BlockTNT extends Block {

	/**
	 * TNT block constructor
	 * @param type Block type
	 */
	public BlockTNT(Type type) {
		super(type);
	}

	public boolean isSolid() {
		return true;
	}
	
	public float getStrength() {
		return 1f;
	}
	
	public boolean blocksLight() {
		return true;
	}
	
}