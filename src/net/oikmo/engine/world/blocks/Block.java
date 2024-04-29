package net.oikmo.engine.world.blocks;

public abstract class Block {
	public static final Block[] blocks = new Block[18];
	public static final Block grass = new BlockGrass(Type.GRASS);
	public static final Block dirt = new BlockDirt(Type.DIRT);
	public static final Block stone = new BlockStone(Type.STONE);
	public static final Block bedrock = new BlockBedrock(Type.BEDROCK);
	public static final Block cobble = new BlockCobble(Type.COBBLE);
	public static final Block mossycobble = new BlockMossyCobble(Type.MOSSYCOBBLE);
	public static final Block obsidian = new BlockObsidian(Type.OBSIDIAN);
	public static final Block oaklog = new BlockOakLog(Type.OAKLOG);
	public static final Block oakleaf = new BlockOakLeaf(Type.OAKLEAF);
	public static final Block oakplanks = new BlockOakPlanks(Type.OAKPLANKS);
	public static final Block glass = new BlockGlass(Type.GLASS);
	public static final Block smoothstone = new BlockSmoothStone(Type.SMOOTHSTONE);
	public static final Block brick = new BlockBrick(Type.BRICK);
	public static final Block ironBlock = new BlockIronBlock(Type.IRONBLOCK);
	public static final Block goldBlock = new BlockGoldBlock(Type.GOLDBLOCK);
	public static final Block diamondBlock = new BlockDiamondBlock(Type.DIAMONDBLOCK);
	public static final Block tnt = new BlockTNT(Type.TNT);
	public static final Block obsidianPlayer = new BlockObsidian(Type.OBSIDIANPLAYER);
	
	public static enum Type {
		GRASS, 
		DIRT, 
		STONE,
		BEDROCK,
		COBBLE,
		MOSSYCOBBLE,
		OBSIDIAN,
		OAKLEAF,
		OAKLOG,
		OAKPLANKS,
		GLASS,
		SMOOTHSTONE,
		BRICK,
		TNT,
		IRONBLOCK,
		GOLDBLOCK,
		DIAMONDBLOCK, 
		OBSIDIANPLAYER,
	};
	
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