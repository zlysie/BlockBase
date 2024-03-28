package net.oikmo.engine.world.chunk;

import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.world.World;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.toolbox.PerlinNoiseGenerator;

public class Chunk {
	
	public static final byte CHUNK_SIZE = 16;
	public Block[][][] blocks;
	private PerlinNoiseGenerator noiseGen;

	public Chunk(PerlinNoiseGenerator gen, Vector3f origin) {
		blocks = new Block[CHUNK_SIZE][World.WORLD_HEIGHT][CHUNK_SIZE];
		noiseGen = gen;
		generateChunk(origin);
	}
	
	private void generateChunk(Vector3f origin) {
	    for (int x = 0; x < CHUNK_SIZE; x++) {
	        for (int z = 0; z < CHUNK_SIZE; z++) {
	        	int actualX = (int) (origin.x + x);
	        	int actualZ = (int) (origin.z + z);

	        	int height = (int) noiseGen.generateHeight(actualX, actualZ);
	            for (int y = 0; y < World.WORLD_HEIGHT; y++) {
	                if (y < height) {
	                    // Create a solid block at this position                    
	                	blocks[x][y][z] = calculateBlockType(y);
	                } else {
	                    // Air block above the height limit
	                    blocks[x][y][z] = null;
	                }
	            }
	        }
	    }
	    
	    getTopLayer();
	}
	
	private void getTopLayer() {

	    for (int x = 0; x < CHUNK_SIZE; x++) {
	        for (int z = 0; z < CHUNK_SIZE; z++) {
	            for (int y = World.WORLD_HEIGHT - 1; y >= 0; y--) {
	                if (blocks[x][y][z] != null) {
	                	blocks[x][y + 1][z] = Block.grass;
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
	    } else {
	        return Block.stone;
	    }
	}
	
}