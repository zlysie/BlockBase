package net.oikmo.engine.world.chunk;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.github.matthewdawsey.collisionres.AABB;

import net.oikmo.engine.world.World;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.toolbox.Maths;
import net.oikmo.toolbox.PerlinNoiseGenerator;

public class Chunk {
	public static final byte CHUNK_SIZE = 16;
	public byte[][][] blocks;
	private int[][] heights;
	
	public Chunk(PerlinNoiseGenerator noiseGen, Vector3f origin) {
		blocks = new byte[CHUNK_SIZE][World.WORLD_HEIGHT][CHUNK_SIZE];
		heights = new int[CHUNK_SIZE][CHUNK_SIZE];
		generateChunk(origin, noiseGen);
	}

	public Chunk(byte[][][] blocks) {
		this.blocks = blocks;
	}

	/**
	 * Creates blocks from the top layer (given by {@link PerlinNoiseGenerator}) and is extended down to YLevel 0 in which it is refactored via {@link #calculateBlockType(int)}
	 * @param origin
	 * @param noiseGen
	 */
	private void generateChunk(Vector3f origin, PerlinNoiseGenerator noiseGen) {
		for (byte x = 0; x < CHUNK_SIZE; x++) {
			for (byte z = 0; z < CHUNK_SIZE; z++) {
				int actualX = (int) (origin.x + x);
				int actualZ = (int) (origin.z + z);

				int height = (int) noiseGen.generateHeight(actualX, actualZ)+60;
				blocks[x][height][z] = Block.grass.getByteType();
				heights[x][z] = height+1;
				for (int y = 0; y < World.WORLD_HEIGHT; y++) {
					if (y < height) {
						blocks[x][y][z] = calculateBlockType(y).getByteType();
					} else {
						if(y != height) {
							blocks[x][y][z] = -1;
						}
						
					}
				}
			}
		}
	}
	
	public void recalculateHeight(int x, int z) {
		for (int y = World.WORLD_HEIGHT - 1; y >= 0; y--) {
			if (blocks[x][y][z] != -1) {
				heights[x][z] = y;
				break;
			}
		}
	}
	
	public int getHeightFromPosition(Vector3f origin, Vector3f position) {
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

	/**
	 * Returns block based on height.<br>
	 * 
	 * <br>60 to 40 and higher - {@link Block#dirt}
	 * <br>0 - {@link Block#bedrock}
	 * <br>else - {@link Block#stone}
	 * 
	 * @param height
	 * @return {@link Block}
	 */
	private Block calculateBlockType(int height) {
		if (height >= 60) {
			return Block.dirt;
		} else if (height >= 40) {
			return Block.dirt;
		} else if(height == 0) {
			return Block.bedrock;
		} else {
			return Block.stone;
		}
	}

	/**
	 * Creates an AABB from. each. block.
	 * This isn't bad is it?
	 * 
	 * @return aabbs - {@link List}
	 */
	public List<AABB> getAABBs(Vector3f origin, AABB aabb) {
		List<AABB> aabbs = new ArrayList<>();
		
		int roundedY = (int)(aabb.start.y);
		int offset = 2;
		int minY = roundedY-offset;
		int maxY = roundedY+offset+2;
		
		if(minY < 0) {
			minY = 0;
		}
		if(maxY > World.WORLD_HEIGHT) {
			maxY = World.WORLD_HEIGHT;
		}
		for(int x = 0; x < CHUNK_SIZE; x++) {
			for(int z = 0; z < CHUNK_SIZE; z++) {
				for(int y = minY; y < maxY; y++) {
					if(blocks[x][y][z] == -1) {
						continue;
					}
					float blockX = (x + origin.x);
					float blockY = y;
					float blockZ = (z + origin.z);
					AABB other = new AABB(new Vector3f(-0.5f, -0.5f, -0.5f), new Vector3f(0.5f, 0.5f, 0.5f));
					other.updatePosition(new Vector3f(blockX, blockY, blockZ));
					aabbs.add(other);
					
					
				}
			}
		}
		return aabbs;
	}
}