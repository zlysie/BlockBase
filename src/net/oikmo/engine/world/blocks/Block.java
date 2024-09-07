package net.oikmo.engine.world.blocks;

/**
 * Those tiny voxels you can interact with
 * @author Oikmo
 */
public abstract class Block {
	/** Blocks list (for looping and rendering) */
	public static final Block[] blocks = new Block[18];
	/** Grass block */
	public static final Block grass = new BlockGrass(Type.GRASS);
	/** Dirt block */
	public static final Block dirt = new BlockDirt(Type.DIRT);
	/** Stone block */
	public static final Block stone = new BlockStone(Type.STONE);
	/** Bedrock block */
	public static final Block bedrock = new BlockBedrock(Type.BEDROCK);
	/** Cobblestone block */
	public static final Block cobble = new BlockCobble(Type.COBBLE);
	/** Mossy cobblestone block */
	public static final Block mossycobble = new BlockMossyCobble(Type.MOSSYCOBBLE);
	/** Obsidian block */
	public static final Block obsidian = new BlockObsidian(Type.OBSIDIAN);
	/** Oak log block */
	public static final Block oaklog = new BlockOakLog(Type.WOOD);
	/** Oak leaves block */
	public static final Block oakleaf = new BlockOakLeaf(Type.LEAVES);
	/** Oak plank block */
	public static final Block oakplanks = new BlockOakPlanks(Type.PLANKS);
	/** Glass block */
	public static final Block glass = new BlockGlass(Type.GLASS);
	/** Smooth stone block */
	public static final Block smoothstone = new BlockSmoothStone(Type.SMOOTHSTONE);
	/** Brick block */
	public static final Block brick = new BlockBrick(Type.BRICK);
	/** Iron block */
	public static final Block ironBlock = new BlockIronBlock(Type.IRONBLOCK);
	/** Gold block */
	public static final Block goldBlock = new BlockGoldBlock(Type.GOLDBLOCK);
	/** Diamond block */
	public static final Block diamondBlock = new BlockDiamondBlock(Type.DIAMONDBLOCK);
	/** TNT block */
	public static final Block tnt = new BlockTNT(Type.TNT);
	
	/**
	 * The type of block...
	 * @author Oikmo
	 */
	public static enum Type {
		/** Air */
		AIR,
		/** Grass  */
		GRASS, 
		/** Dirt  */
		DIRT, 
		/**  Stone */
		STONE,
		/** Bedrock */
		BEDROCK,
		/**  Cobblestone */
		COBBLE,
		/** Mossy Cobblestone */
		MOSSYCOBBLE,
		/** Obsidian */
		OBSIDIAN,
		/** Leaves */
		LEAVES,
		/** Wood */
		WOOD,
		/** Planks */
		PLANKS,
		/** Glass */
		GLASS,
		/** Smooth Stone */
		SMOOTHSTONE,
		/** Brick */
		BRICK,
		/** Tnt */
		TNT,
		/** Iron Block */
		IRONBLOCK,
		/** Gold Block */
		GOLDBLOCK,
		/** Diamond Block  */
		DIAMONDBLOCK, 
	};
	
	/** What type is the block? */
	public Type type;
	
	/**
	 * Block constructor
	 * @param type What type should the block be?
	 */
	public Block(Type type) {
		blocks[type.ordinal()] = this;
		this.type = type;
	}
	
	/**
	 * Returns a {@link Block} based on the index of block given
	 * @param type Index of block
	 * @return {@link Block}
	 */
	public static Block getBlockFromOrdinal(byte type) {
		return type != -1 ? blocks[type] : null;
	}
	
	/** 
	 * Sets the {@link Type} of the Block
	 * @param type Type to be set to
	 */
	public void setType(Block.Type type) {
		this.type = type;
	}
	
	/**
	 * Returns the index of the type (ordinal)
	 * @return {@link Byte}
	 */
	public byte getByteType() {
		return (byte)type.ordinal();
	}
	
	/**
	 * Returns the enum of the type
	 * @return {@link Type}
	 */
	public Type getEnumType() {
		return type;
	}
	
	/**
	 * Returns the integer index of the type
	 * @return {@link Integer}
	 */
	public int getType() {
		return type.ordinal();
	}
	
	/**
	 * How long does it take for it break?<br>How much can it resist explosions?
	 * @return {@link Float}
	 */
	public abstract float getStrength();
	
	/**
	 * Should light pass through or not. 
	 * @return {@link Boolean}
	 */
	public abstract boolean blocksLight();
	
	/**
	 * Can it be collided with or not?
	 * @return {@link Boolean}
	 */
	public abstract boolean isSolid();
	
}