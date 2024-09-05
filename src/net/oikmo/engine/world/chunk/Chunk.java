package net.oikmo.engine.world.chunk;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.world.World;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.engine.world.chunk.coordinate.ChunkCoordinates;
import net.oikmo.toolbox.Maths;
import net.oikmo.toolbox.noise.OpenSimplexNoise;

/**
 * Generates chunk and holds blocks along with height maps
 * 
 * @author Oikmo
 */
public class Chunk {
	public static final byte CHUNK_SIZE = 16;
	private byte[] blocks;
	public int[][] heights;
	
	public Chunk(OpenSimplexNoise noiseGen, ChunkCoordinates origin) {
		blocks = new byte[32768];
		heights = new int[CHUNK_SIZE][CHUNK_SIZE];
		generateChunk(origin, noiseGen);
	}

	public Chunk(byte[] blocks) {
		this.blocks = blocks;
		
		if(Maths.indexOf(blocks, new byte[] { -1 }) != -1) { 
			System.out.println("has a minus!!");
			for(int i = 0; i < blocks.length; i++) {
				blocks[i] = (byte) (blocks[i] + 1);
			}
		}
		
		heights = new int[CHUNK_SIZE][CHUNK_SIZE];
		calculateHeights();
		calculateHeights();
	}

	/**
	 * Creates blocks from the top layer (given by {@link PerlinNoiseGenerator}) and is extended down to YLevel 0 in which it is refactored via {@link #calculateBlockType(int)}
	 * @param origin
	 * @param noiseGen
	 */
	private void generateChunk(ChunkCoordinates origin, OpenSimplexNoise noiseGen) {
		for (byte x = 0; x < CHUNK_SIZE; x++) {
			for (byte z = 0; z < CHUNK_SIZE; z++) {
				int actualX = (int) (origin.x + x);
				int actualZ = (int) (origin.z + z);

				int height = (int) ((noiseGen.noise(actualX/14f, actualZ/14f)*7f) + (noiseGen.noise((-actualZ)/16f,(-actualX)/16f)*12f) + (noiseGen.noise((actualZ)/6f,(actualX)/6f)*4f))+60;
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
			}
		}
		generateTrees(noiseGen.getSeed());
		calculateHeights();
	}
	
	private void generateTrees(long seed) {
		Random rand = new Random(seed);
		
		for(int i = 0; i < rand.nextInt(6);i++) {
			int x = new Random().nextInt(CHUNK_SIZE);
			int z = new Random().nextInt(CHUNK_SIZE);
			
			checkForTree(x, z);
		}
		
	}
	
	private void checkForTree(int x, int z) {
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
			checkForTree(x, z);
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
					if (getBlock(x,y,z) != Block.Type.AIR.ordinal()) {
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
	
	public int getHeightFromPosition(ChunkCoordinates origin, Vector3f position) {
		Vector3f rounded = Maths.roundVectorTo(position);
		int x = (int) (rounded.x - origin.x);
		if(x < 0) {x = 0;}
		if(x > 15) {x = 15;}
		int z = (int) (rounded.z - origin.z);
		if(z < 0) {z = 0;}
		if(z > 15) {z = 15;}
		return heights[x][z] + 2;
	}
	
	public int getHeightFromPosition(int x, int z) {
		return heights[x][z];
	}
	
	public void setBlock(int x, int y, int z, int block) {
		blocks[x << 11 | z << 7 | y] = (byte)block;
	}
	
	public byte getBlock(int x, int y, int z) {
        return blocks[x << 11 | z << 7 | y];
    }

	public byte[] getBlocks() {
		return blocks;
	}
}