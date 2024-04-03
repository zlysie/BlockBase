package net.oikmo.engine.world.chunk;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.AABB;
import net.oikmo.engine.world.World;
import net.oikmo.engine.world.blocks.Block;
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

	public ArrayList<AABB> getCubes(AABB aABB) {
		ArrayList<AABB> aABBs = new ArrayList<>();
		int x0 = (int)aABB.x0;
		int x1 = (int)(aABB.x1 + 1.0F);
		int y0 = (int)aABB.y0;
		int y1 = (int)(aABB.y1 + 1.0F);
		int z0 = (int)aABB.z0;
		int z1 = (int)(aABB.z1 + 1.0F);

		for(int x = x0; x < x1; ++x) {
			for(int y = y0; y < y1; ++y) {
				for(int z = z0; z < z1; ++z) {
					try {
						if(blocks[x][y][z] != -1) {
							Block block = Block.getBlockFromOrdinal(blocks[x][y][z]);

							AABB toCompare = block.getCollisionBoundingBoxFromPool(x,y,z);
							if(aABB.intersects(toCompare)) {
								aABBs.add(toCompare);
							}
						}
					} catch(ArrayIndexOutOfBoundsException e) {
						return null;
					}

				}
			}
		}
		//System.out.println(aABBs.size());
		return aABBs;
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
	
	
	public List<AABB> getAABBs() {
		List<AABB> aabbs = new ArrayList<>();

		byte blockSize = Block.blockSize;

		for (byte x = 0; x < CHUNK_SIZE; x++) {
			for (int y = 0; y < World.WORLD_HEIGHT; y++) {
				for (byte z = 0; z < CHUNK_SIZE; z++) {
					byte blockType = blocks[x][y][z];
					if (blockType != -1) {
						float blockX0 = origin.x + x * blockSize;
						float blockY0 = origin.y + y * blockSize;
						float blockZ0 = origin.z + z * blockSize;
						float blockX1 = blockX0 + blockSize;
						float blockY1 = blockY0 + blockSize;
						float blockZ1 = blockZ0 + blockSize;
						
						//offsets block if the origin is negative (weird shit happens if this isn't applied)
						if (origin.x < 0) {
							blockX0 -= blockSize;
							blockX1 -= blockSize;
						}
						if (origin.y < 0) {
							blockY0 -= blockSize;
							blockY1 -= blockSize;
						}
						if (origin.z < 0) {
							blockZ0 -= blockSize;
							blockZ1 -= blockSize;
						}

						AABB aabb = new AABB(blockX0, blockY0, blockZ0, blockX1, blockY1, blockZ1);
						aabbs.add(aabb);

					}
				}
			}
		}

		return aabbs;
	}
}