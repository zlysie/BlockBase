package net.oikmo.engine.world.blocks;

import net.oikmo.engine.AABB;

public abstract class Block {
	public static final Block[] blocks = new Block[13];
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
	public static final byte blockSize = 1;
	public float minX;
	public float minY;
	public float minZ;
	public float maxX;
	public float maxY;
	public float maxZ;
	
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
		BRICK
	};
	
	public Type type;
	
	public Block(Type type) {
		blocks[type.ordinal()] = this;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
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
	
	protected final void setBlockBounds(float var1, float var2, float var3, float var4, float var5, float var6) {
		this.minX = var1;
		this.minY = var2;
		this.minZ = var3;
		this.maxX = var4;
		this.maxY = var5;
		this.maxZ = var6;
	}
	
	public final AABB getSelectedBoundingBoxFromPool(int var1, int var2, int var3) {
		return new AABB((float)var1 + this.minX, (float)var2 + this.minY, (float)var3 + this.minZ, (float)var1 + this.maxX, (float)var2 + this.maxY, (float)var3 + this.maxZ);
	}

	public AABB getCollisionBoundingBoxFromPool(int var1, int var2, int var3) {
		return new AABB((float)var1 + this.minX, (float)var2 + this.minY, (float)var3 + this.minZ, (float)var1 + this.maxX, (float)var2 + this.maxY, (float)var3 + this.maxZ);
	}
	
	public abstract float getStrength();
	
	public abstract boolean blocksLight();

	public abstract boolean isSolid();
	
}