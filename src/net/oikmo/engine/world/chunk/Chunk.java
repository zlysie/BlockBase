package net.oikmo.engine.world.chunk;

import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.world.World;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.toolbox.FastMath;
import net.oikmo.toolbox.Noise;

public class Chunk {
	public static final byte CHUNK_SIZE = 16;
	public byte[][][] blocks;
	
	public Chunk(long seed, Vector3f origin) {
		blocks = new byte[CHUNK_SIZE][World.WORLD_HEIGHT][CHUNK_SIZE];
		generateChunk(origin, seed);
	}
	
	public Chunk(byte[][][] blocks) {
		this.blocks = blocks;
	}
	
 	private void generateChunk(Vector3f origin, long seed) {
	    for (byte x = 0; x < CHUNK_SIZE; x++) {
	        for (byte z = 0; z < CHUNK_SIZE; z++) {
	        	int actualX = (int) (origin.x + x);
	        	int actualZ = (int) (origin.z + z);
	        	
	        	int height = (int) FastMath.abs((float)Noise.noise(actualX*100, 50, actualZ*100))+60;
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