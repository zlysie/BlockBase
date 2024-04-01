package net.oikmo.engine.world.blocks;

public abstract class Block {
	public static final Block[] blocks = new Block[7];
	public static final Block grass = new BlockGrass(Type.GRASS);
	public static final Block dirt = new BlockDirt(Type.DIRT);
	public static final Block stone = new BlockStone(Type.STONE);
	public static final Block treebark = new BlockTreeBark(Type.TREEBARK);
	public static final Block treeleaf = new BlockTreeLeaf(Type.TREELEAF);
	public static final Block cobble = new BlockCobble(Type.COBBLE);
	public static final Block bedrock = new BlockBedrock(Type.BEDROCK);
	
	public static enum Type {
		GRASS, 
		DIRT, 
		STONE,
		BEDROCK,
		COBBLE,
		TREEBARK,
		TREELEAF
	};
	
	public static void init() {
		int i = 0;
		System.out.println("stone " + stone.getType());
		blocks[i] = grass;
		blocks[i++] = dirt;
		blocks[i++] = stone;
		blocks[i++] = treebark;
		blocks[i++] = treeleaf;
		blocks[i++] = cobble;
		blocks[i++] = bedrock;
	}
	
	public Type type;
	
	public Block(Type type) {
		blocks[type.ordinal()] = this;
		this.type = type;
	}
	
	public static Block getBlockFromOrdinal(byte type) {
		return type != -1 ? blocks[type] : null;
	}
	
	public void setType(Block.Type type) {
		this.type = type;
	}

	public byte getByteType() {
		return (byte)type.ordinal();
	}
	
	public Type getEnumType() {
		return type;
	}
	
	public int getType() {
		return type.ordinal();
	}
	
	public abstract float getStrength();
	
	public abstract boolean blocksLight();

	public abstract boolean isSolid();
	
}