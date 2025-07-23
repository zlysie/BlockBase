package net.oikmo.engine.world.chunk;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.world.World;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.engine.world.chunk.coordinate.ChunkCoordinates;
import net.oikmo.main.Main;
import net.oikmo.toolbox.Maths;
import net.oikmo.toolbox.noise.OpenSimplexNoise;

/**
 * Generates chunk and holds blocks along with height maps
 * 
 * @author Oikmo
 */
public class Chunk {
	/** Static size for all chunks */
	public static final byte CHUNK_SIZE = 16;
	
	/** Position of chunk in the world */
	ChunkCoordinates origin;
	/** Blocks array (holds block indexes) */
	private byte[] blocks;
	/** Holds the highest block heights */
	private int[][] heights;
	/** Light values */
	private int[][][] lightDepths;
	
	/**
	 * Creates an entirely new chunk in which it calls {@link #generateChunk(OpenSimplexNoise)} and generates light values {@link #calcLightDepths(int, int, int, int)}
	 * @param noiseGen Noise generator to use
	 * @param origin Position of chunk in the world
	 */
	public Chunk(OpenSimplexNoise noiseGen, ChunkCoordinates origin) {
		blocks = new byte[32768];
		heights = new int[CHUNK_SIZE][CHUNK_SIZE];
		this.origin = origin;
		generateChunk(noiseGen);
		this.lightDepths = new int[CHUNK_SIZE][World.WORLD_HEIGHT][CHUNK_SIZE];
	}
	
	/**
	 * Loads chunk using bytes
	 * @param blocks Given blocks array 
	 * @param origin Position of chunk in the world
	 */
	public Chunk(byte[] blocks, ChunkCoordinates origin) {
		this.blocks = blocks;
		if(Maths.indexOf(blocks, new byte[] { -1 }) != -1) { 
			for(int i = 0; i < blocks.length; i++) {
				blocks[i] = (byte) (blocks[i] + 1);
			}
		}
		
		heights = new int[CHUNK_SIZE][CHUNK_SIZE];
		this.lightDepths = new int[CHUNK_SIZE][World.WORLD_HEIGHT][CHUNK_SIZE];
		this.origin = origin;
		//calculateHeights();	
	}

	/**
	 * Creates blocks from the top layer (given by {@link net.oikmo.toolbox.noise.OpenSimplexNoise}) and is extended down to YLevel 0 in which it is refactored by it's height.
	 * @param noiseGen The noise generator used to have the heights
	 */
	private void generateChunk(OpenSimplexNoise noiseGen) {
		for (byte x = 0; x < CHUNK_SIZE; x++) {
			for (byte z = 0; z < CHUNK_SIZE; z++) {
				if(!Main.theWorld.superFlat) {
					int actualX = origin.x + x;
					int actualZ = origin.z + z;
					
					int height = (int) ((noiseGen.noise(actualX/14f, actualZ/14f)*7f) + (noiseGen.noise((-actualZ)/16f,(-actualX)/16f)*12f) + (noiseGen.noise((actualZ)/6f,(actualX)/6f)*4f))+60;
					//int height = 30;
					setBlock(x, height, z, Block.grass.getByteType());
					heights[x][z] = height+1;
					for (int y = 0; y < World.WORLD_HEIGHT; y++) {
						if(y < height) {
							if(y > height - 4) {
								setBlock(x, y, z, Block.dirt.getByteType());
							}  else if(y == 0) {
								setBlock(x, y, z, Block.bedrock.getByteType());
							} else {
								setBlock(x, y, z, Block.stone.getByteType());
							}
						}
					}
				} else {
					setBlock(x, 3, z, Block.grass.getByteType());
					heights[x][z] = 3+1;
					
					setBlock(x, 2, z, Block.dirt.getByteType());
					setBlock(x, 1, z, Block.dirt.getByteType());
					setBlock(x, 0, z, Block.bedrock.getByteType());
				}
				
			}
		}
		if(!Main.theWorld.superFlat) {
			generateTrees(noiseGen.getSeed());
		}
		
		//alculateHeights();
	}
	
	/**
	 * Gets the brightness values from given vector
	 * 
	 * @param x X position of block to check
	 * @param y Y position of block to check
	 * @param z Z position of block to check
	 * @return {@link Float}
	 */
	public float getBrightness(int x, int y, int z) {
		if(x == -1 || y == -1 || z == -1 || x == CHUNK_SIZE || y == CHUNK_SIZE || z == CHUNK_SIZE) {
			int actualX = origin.x + x;
			int actualZ = origin.z + z;
			ChunkCoordinates chunkPos = Maths.calculateChunkPosition(new Vector3f(actualX, y, actualZ));
			MasterChunk m = Main.theWorld.getChunkFromPosition(chunkPos);
			
			if(m != null) {
				return m.getChunk().isLit(actualX-chunkPos.x, y, actualZ-chunkPos.z) ? 1.0F : 0.6F;
			}
		}
		return this.isLit(x, y, z) ? 1.0F : 0.6F;
	}
	
	/**
	 * Calculates light brightness from a certain area in the chunk (going from the top to the bottom)
	 * @param minX Minimum X
	 * @param minZ Minimum Z
	 * @param maxX Maximum X
	 * @param maxZ Maximum Z
	 */
	public void calcLightDepths(int minX, int minZ, int maxX, int maxZ) {
		for(int x = minX; x < minX + maxX; ++x) {
			for(int z = minZ; z < minZ + maxZ; ++z) {
				for(int y = 0; y < World.WORLD_HEIGHT; y++) {
					int oldDepth = this.lightDepths[x][y][z];
					int calculatedY;
					
					for(calculatedY = World.WORLD_HEIGHT - 1; calculatedY > 0 && !this.isLightBlocker(x, calculatedY, z); --calculatedY) {}

					this.lightDepths[x][y][z] = calculatedY;
					if(oldDepth != y) {
						//update or something
					}
				}
			}
		}
	}
	
	/**
	 * Returns if the block at the given vector blocks the light
	 * @param x X position of block
	 * @param y Y position of block
	 * @param z Z position of block
	 * @return {@link Boolean}
	 */
	public boolean isLightBlocker(int x, int y, int z) {
		Block block = Block.getBlockFromOrdinal((byte)getBlock(x, y, z));
		return block == null ? false : block.blocksLight();
	}
	
	/**
	 * Checks if the specified vector has light or not
	 * @param x X position of block
	 * @param y Y position of block
	 * @param z Z position of block
	 * @return {@link Boolean}
	 */
	public boolean isLit(int x, int y, int z) {
		return x >= 0 && y >= 0 && z >= 0 && x < CHUNK_SIZE && y < World.WORLD_HEIGHT && z < CHUNK_SIZE ? y >= this.lightDepths[x][y][z] : false;
	}
	
	/**
	 * Chooses a random area of the chunk and attempts to generate at that position
	 * @param seed World seed
	 */
	private void generateTrees(long seed) {
		Random rand = new Random(seed);
		
		for(int i = 0; i < rand.nextInt(6);i++) {
			int x = new Random().nextInt(CHUNK_SIZE);
			int z = new Random().nextInt(CHUNK_SIZE);
			
			checkAndGenerateTree(x, z);
		}
	}
	
	/**
	 * Checks if it is in a valid range and has no oak leaves nearby, if so generate tree
	 * @param x X position of tree
	 * @param z Z position of tree
	 */
	private void checkAndGenerateTree(int x, int z) {
		if(x > 2 && x < CHUNK_SIZE-2 && z > 2 && z < CHUNK_SIZE-2) {
			int height = heights[x][z];
			if(!blockHasSpecificNeighbours(Block.oaklog, x,height,z)) {
				setBlock(x, height++, z, Block.oaklog.getByteType());
				setBlock(x, height++, z, Block.oaklog.getByteType());
				setBlock(x, height++, z, Block.oaklog.getByteType());
				setBlock(x, height++, z, Block.oaklog.getByteType());
				for(int j = 0; j < new Random().nextInt(2);j++) {
					setBlock(x, height++, z, Block.oaklog.getByteType());
				}
				int index = height;
				setBlock(Block.oakleaf, x, index, z);
				setBlock(Block.oakleaf, x-1, index, z);
				setBlock(Block.oakleaf, x+1, index, z);
				setBlock(Block.oakleaf, x, index, z-1);
				setBlock(Block.oakleaf, x, index, z+1);
				
				index--;
				setBlock(Block.oakleaf, x-1, index, z);
				setBlock(Block.oakleaf, x+1, index, z);
				setBlock(Block.oakleaf, x,index, z-1);
				setBlock(Block.oakleaf, x, index, z+1);
				index--;
				setBlock(Block.oakleaf, x-1, index, z);
				setBlock(Block.oakleaf, x+1, index, z);
				setBlock(Block.oakleaf, x, index, z-1);
				setBlock(Block.oakleaf, x, index, z+1);
				setBlock(Block.oakleaf, x-1, index, z);
				setBlock(Block.oakleaf, x+1, index, z);
				setBlock(Block.oakleaf, x-1, index, z-1);
				setBlock(Block.oakleaf, x+1, index, z+1);
				setBlock(Block.oakleaf, x+1, index, z-1);
				setBlock(Block.oakleaf, x-1, index, z+1);
				setBlock(Block.oakleaf, x, index, z-1);
				setBlock(Block.oakleaf, x, index, z+1);
				
				setBlock(Block.oakleaf, x-2, index, z);
				setBlock(Block.oakleaf, x+2, index, z);
				setBlock(Block.oakleaf, x, index, z-2);
				setBlock(Block.oakleaf, x, index, z+2);
				setBlock(Block.oakleaf, x-2, index, z);
				setBlock(Block.oakleaf, x+2, index, z);
				setBlock(Block.oakleaf, x-2, index, z-2);
				setBlock(Block.oakleaf, x-1, index, z-2);
				setBlock(Block.oakleaf, x-2, index, z-1);
				setBlock(Block.oakleaf, x+2, index, z+2);
				setBlock(Block.oakleaf, x+1, index, z+2);
				setBlock(Block.oakleaf, x+2, index, z+1);
				setBlock(Block.oakleaf, x+2, index, z-2);
				setBlock(Block.oakleaf, x+1, index, z-2);
				setBlock(Block.oakleaf, x+2, index, z-1);
				setBlock(Block.oakleaf, x-2, index, z+2);
				setBlock(Block.oakleaf, x-1, index, z+2);
				setBlock(Block.oakleaf, x-2, index, z+1);
				setBlock(Block.oakleaf, x, index, z-2);
				setBlock(Block.oakleaf, x, index, z+2);
				
				index--;
				setBlock(Block.oakleaf, x-1, index, z);
				setBlock(Block.oakleaf, x+1, index, z);
				setBlock(Block.oakleaf, x, index, z-1);
				setBlock(Block.oakleaf, x, index, z+1);
				setBlock(Block.oakleaf, x-1, index, z);
				setBlock(Block.oakleaf, x+1, index, z);
				setBlock(Block.oakleaf, x-1, index, z-1);
				setBlock(Block.oakleaf, x+1, index, z+1);
				setBlock(Block.oakleaf, x+1, index, z-1);
				setBlock(Block.oakleaf, x-1, index, z+1);
				setBlock(Block.oakleaf, x, index, z-1);
				setBlock(Block.oakleaf, x, index, z+1);
				
				setBlock(Block.oakleaf, x-2, index, z);
				setBlock(Block.oakleaf, x+2, index, z);
				setBlock(Block.oakleaf, x, index, z-2);
				setBlock(Block.oakleaf, x, index, z+2);
				setBlock(Block.oakleaf, x-2, index, z);
				setBlock(Block.oakleaf, x+2, index, z);
				setBlock(Block.oakleaf, x-2, index, z-2);
				setBlock(Block.oakleaf, x-1, index, z-2);
				setBlock(Block.oakleaf, x-2, index, z-1);
				setBlock(Block.oakleaf, x+2, index, z+2);
				setBlock(Block.oakleaf, x+1, index, z+2);
				setBlock(Block.oakleaf, x+2, index, z+1);
				setBlock(Block.oakleaf, x+2, index, z-2);
				setBlock(Block.oakleaf, x+1, index, z-2);
				setBlock(Block.oakleaf, x+2, index, z-1);
				setBlock(Block.oakleaf, x-2, index, z+2);
				setBlock(Block.oakleaf, x-1, index, z+2);
				setBlock(Block.oakleaf, x-2, index, z+1);
				setBlock(Block.oakleaf, x, index, z-2);
				setBlock(Block.oakleaf, x, index, z+2);
			}
		
		} else {
			x = new Random().nextInt(CHUNK_SIZE);
			z = new Random().nextInt(CHUNK_SIZE);
			checkAndGenerateTree(x, z);
		}
	}
	
	private void setBlock(Block block, int x, int y, int z) {
		if(getBlock(x,y,z) == Block.Type.AIR.ordinal()) {
			setBlock(x,y,z, block.getByteType());
		}
	}

	public boolean blockHasSpecificNeighbours(Block block, int x, int y, int z) {		
		for(int dx = -1; dx <= 1; dx++) {
			for(int dy = -1; dy <= 1; dy++) {
				for(int dz= -1; dz <= 1; dz++) {
					
					int checkerX = x + dx;
					int checkerY = y + dy;
					int checkerZ = z + dz;
					
					if(getBlock(checkerX,checkerY,checkerZ) == block.getByteType()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private void calculateHeights() {
		for (byte x = 0; x < CHUNK_SIZE; x++) {
			for (byte z = 0; z < CHUNK_SIZE; z++) {
				for (int y = World.WORLD_HEIGHT - 1; y >= 0; y--) {
					if (getBlock(x,y,z) != -1) {
						heights[x][z] = y;
						break;
					}
				}
			}
		}
	}
	
	public void recalculateHeight(int x, int z) {
		for (int y = World.WORLD_HEIGHT - 1; y >= 0; y--) {
			if (getBlock(x,y,z) != Block.Type.AIR.ordinal()) {
				heights[x][z] = y;
				break;
			}
		}
	}
	
	/**
	 * Returns the height from 
	 * @param origin
	 * @param position
	 * @return
	 */
	public int getHeightFromPosition(Vector3f position) {
		Vector3f rounded = Maths.roundVectorTo(position);
		int x = (int) (rounded.x - origin.x);
		if(x < 0) {x = 0;}
		if(x > 15) {x = 15;}
		int z = (int) (rounded.z - origin.z);
		if(z < 0) {z = 0;}
		if(z > 15) {z = 15;}
		return heights[x][z] + 2;
	}
	
	/**
	 * Returns the heighest point on specified coordinates
	 * @param x X coordinate
	 * @param z Y coordinate
	 * @return {@link Integer}
	 */
	public int getHeightFromPosition(int x, int z) {
		return heights[x][z];
	}
	
	/**
	 * 	Sets a block ID for a position in the chunk.
	 * @param x X position of block
	 * @param y Y position of block
	 * @param z Z position of block
	 * @param block ID of block to be set to
	 */
	public void setBlock(int x, int y, int z, int block) {
		blocks[x << 11 | z << 7 | y] = (byte)block;
	}
	
	/**
	 * Return the ID of a block in the chunk.
	 * @param x X position of block
	 * @param y Y position of block
	 * @param z Z position of block
	 * @return {@link Byte}
	 */
	public byte getBlock(int x, int y, int z) {
        return blocks[x << 11 | z << 7 | y];
    }
	
	/**
	 * Returns the {@link #blocks} array
	 * @return {@link #blocks}[]
	 */
	public byte[] getBlocks() {
		return blocks;
	}
}