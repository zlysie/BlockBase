package net.oikmo.engine.world.chunk;

import java.util.ArrayList;

import net.oikmo.engine.AABB;
import net.oikmo.engine.world.World;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.toolbox.PerlinNoiseGenerator;

import org.lwjgl.util.vector.Vector3f;

public class Chunk {
	public static final byte CHUNK_SIZE = 16;
	public byte[][][] blocks;
	
	public Chunk(PerlinNoiseGenerator noiseGen, Vector3f origin) {
		blocks = new byte[CHUNK_SIZE][World.WORLD_HEIGHT][CHUNK_SIZE];
		generateChunk(origin, noiseGen);
	}
	
	public Chunk(byte[][][] blocks) {
		this.blocks = blocks;
	}
	
 	private void generateChunk(Vector3f origin, PerlinNoiseGenerator noiseGen) {
	    for (byte x = 0; x < CHUNK_SIZE; x++) {
	        for (byte z = 0; z < CHUNK_SIZE; z++) {
	        	int actualX = (int) (origin.x + x);
	        	int actualZ = (int) (origin.z + z);
	        	
	        	int height = (int) noiseGen.generateHeight(actualX, actualZ)+60;
	            for (int y = 0; y < World.WORLD_HEIGHT; y++) {
	                if (y < height) {
	                    // Create a solid block at this position                    
	                	blocks[x][y][z] = calculateBlockType(y).getByteType();
	                } else {
	                    // Air block above the height limit
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
		if(x0 < 0) {
			x0 = 0;
		}

		if(y0 < 0) {
			y0 = 0;
		}

		if(z0 < 0) {
			z0 = 0;
		}

		if(x1 > CHUNK_SIZE) {
			x1 = CHUNK_SIZE;
		}

		if(y1 > CHUNK_SIZE) {
			y1 = CHUNK_SIZE;
		}

		if(z1 > World.WORLD_HEIGHT) {
			z1 = World.WORLD_HEIGHT;
		}

		for(int x = x0; x < x1; ++x) {
			for(int y = y0; y < y1; ++y) {
				for(int z = z0; z < z1; ++z) {
					Block tile = Block.getBlockFromOrdinal(blocks[x][y][z]);
					if(tile != null) {
						AABB aabb = tile.getAABB(x, y, z);
						if(aabb != null) {
							aABBs.add(aabb);
						}
					}
				}
			}
		}

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
	
}