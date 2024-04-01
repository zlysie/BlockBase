package net.oikmo.engine.world.chunk;

import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.world.World;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.toolbox.FastMath;
import prime.PerlinNoise;

public class Chunk {
	public static final byte CHUNK_SIZE = 16;
	public byte[][][] blocks;
	private Vector3f origin;
	
	private PerlinNoise noiseGen;

	public Chunk(PerlinNoise gen, Vector3f origin) {
		blocks = new byte[CHUNK_SIZE][World.WORLD_HEIGHT][CHUNK_SIZE];
		noiseGen = gen;
		generateChunk(origin);
	}
	
	public Chunk(Vector3f origin, byte[][][] blocks) {
		this.blocks = blocks;
		this.origin = origin;
	}
	
	public Vector3f getOrigin() {
		return origin;
	}
	
 	private void generateChunk(Vector3f origin) {
	    for (byte x = 0; x < CHUNK_SIZE; x++) {
	        for (byte z = 0; z < CHUNK_SIZE; z++) {
	        	int actualX = (int) (origin.x + x);
	        	int actualZ = (int) (origin.z + z);
	        	
	        	int height = (int) FastMath.abs((float)noiseGen.getHeight(actualX, actualZ)) + 60;
	        	//System.out.println(height);
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