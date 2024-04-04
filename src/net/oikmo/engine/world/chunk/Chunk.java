package net.oikmo.engine.world.chunk;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.AABB;
import net.oikmo.engine.world.World;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.toolbox.Maths;
import net.oikmo.toolbox.PerlinNoiseGenerator;

public class Chunk {
	public static final byte CHUNK_SIZE = 16;
	public byte[][][] blocks;

	private Vector3f origin;

	public Chunk(PerlinNoiseGenerator noiseGen, Vector3f origin) {
		blocks = new byte[CHUNK_SIZE][World.WORLD_HEIGHT][CHUNK_SIZE];
		this.origin = origin;
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
				for (int y = 0; y < World.WORLD_HEIGHT; y++) {
					if (y < height) {             
						blocks[x][y][z] = calculateBlockType(y).getByteType();
					} else {
						blocks[x][y][z] = -1;
					}
				}
			}
		}

		getTopLayer();
	}

	private void getTopLayer() {
		for (byte x = 0; x < CHUNK_SIZE; x++) {
			for (byte z = 0; z < CHUNK_SIZE; z++) {
				for (int y = World.WORLD_HEIGHT - 1; y >= 0; y--) {
					if (blocks[x][y][z] != -1) {
						blocks[x][y + 1][z] = Block.grass.getByteType();
						break;
					}
				}
			}
		}
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
	 * 
	 * Creates an AABB from. each. block.
	 * This isn't bad is it?
	 * 
	 * @return aabbs - {@link List}
	 */
	public List<AABB> getAABBs() {
		List<AABB> aabbs = new ArrayList<>();
		
		for(int x = 0; x < CHUNK_SIZE; ++x) {
			for(int y = 0; y < World.WORLD_HEIGHT; ++y) {
				for(int z = 0; z < CHUNK_SIZE; ++z) {
					if(blocks[x][y][z] != -1) {
						int blockX = Maths.roundFloat(x + origin.x);
						int blockY = y;
						int blockZ = Maths.roundFloat(z + origin.z);
						
						AABB other = new AABB(blockX, blockY, blockZ, blockX, blockY+1, blockZ);
						
						aabbs.add(other);
					}
				}
			}
		}

		return aabbs;
	}
}