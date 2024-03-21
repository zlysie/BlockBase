package net.oikmo.engine.world.blocks;

public class BlockGrass extends Block {

	public BlockGrass(Type type) {
		super(type);
	}
	
	public boolean isSolid() {
		return true;
	}
	
	public float getStrength() {
		return 0.3f;
	}
	
	public boolean blocksLight() {
		return false;
	}

}
