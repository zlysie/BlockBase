package net.oikmo.engine.world.blocks;

public abstract class Block {
	
	public static final Block[] blocks = new Block[256];
	public static final Block grass = new BlockGrass(Type.GRASS);
	public static final Block dirt = new BlockDirt(Type.DIRT);
	public static final Block stone = new BlockStone(Type.STONE);
	public static final Block treebark = new BlockTreeBark(Type.TREEBARK);
	public static final Block treeleaf = new BlockTreeLeaf(Type.TREELEAF);
	public static final Block cobble = new BlockCobble(Type.COBBLE);
	public static final Block bedrock = new BlockBedrock(Type.BEDROCK);
	
	public static void main(String[] args) {	
		Block.init();
	}
	public static void init() {
		int i = 0;
		blocks[i] = grass;
		blocks[i++] = dirt;
		blocks[i++] = stone;
		blocks[i++] = treebark;
		blocks[i++] = treeleaf;
		blocks[i++] = cobble;
		blocks[i++] = bedrock;
	}
	
	public static enum Type {
		GRASS, 
		DIRT, 
		STONE,
		TREEBARK,
		TREELEAF,
		COBBLE,
		BEDROCK
	};
	
	public Type type;
	
	public Block(Type type) {
		blocks[type.ordinal()] = this;
		this.type = type;
	}
	
	public void setType(Block.Type type) {
		this.type = type;
	}

	public int getType() {
		return type.ordinal();
	}
	
	public abstract float getStrength();
	
	public abstract boolean blocksLight();

	public abstract boolean isSolid();
	
}