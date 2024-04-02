package net.oikmo.engine.world.chunk;

import java.util.ArrayList;

import net.oikmo.engine.AABB;
import net.oikmo.engine.world.World;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.toolbox.Maths;
import net.oikmo.toolbox.PerlinNoiseGenerator;

import org.lwjgl.util.vector.Vector3f;

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
	
 	ArrayList<AABB> aABBs = new ArrayList<>();
 	public ArrayList<AABB> getCubes(AABB aABB) {
		aABBs.clear();
		int x0 = (int)aABB.x0;
		int x1 = (int)(aABB.x1 + 1.0F);
		int y0 = (int)aABB.y0;
		int y1 = (int)(aABB.y1 + 1.0F);
		int z0 = (int)aABB.z0;
		int z1 = (int)(aABB.z1 + 1.0F);
		
		int X0 = (int) (x0 - origin.x);
		int Y0 = (int) y0;
		int Z0 = (int) (z0 - origin.z);
		int X1 = (int) (x1 - origin.x);
		int Y1 = (int) y1;
		int Z1 = (int) (z1 - origin.z);
		
		for(int x = X0; x < X1; ++x) {
			for(int y = Y0; y < Y1; ++y) {
				for(int z = Z0; z < Z1; ++z) {
					try {
						if(blocks[x][y][z] != -1) {
							aABBs.add(new AABB((float)x, (float)y, (float)z, (float)(x + 1), (float)(y + 1), (float)(z + 1)));
						}
					} catch(ArrayIndexOutOfBoundsException e) {
						
					}
					
				}
			}
		}
		System.out.println(aABBs.size());
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