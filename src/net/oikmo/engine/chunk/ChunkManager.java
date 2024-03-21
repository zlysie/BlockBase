package net.oikmo.engine.chunk;

import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.Loader;
import net.oikmo.engine.chunk.blocks.Block;
import net.oikmo.engine.textures.ModelTexture;
import net.oikmo.main.Main;

public class ChunkManager {
	static Loader loader = Loader.getInstance();
	static ModelTexture texture = new ModelTexture(loader.loadTexture("defaultPack"));
	public static Block getBlock(Vector3f globalOrigin, ChunkMesh chunkMesh) {
		Chunk chunk = chunkMesh.chunk;
		int localX = (int) (globalOrigin.x - chunk.origin.x);
		int localY = (int) globalOrigin.y;
		int localZ = (int) (globalOrigin.z - chunk.origin.z);
		
		Block block = null;
		
		if (isWithinChunk(localX, localY, localZ)) {
			block = chunk.blocks[localX][localY][localZ];
			//chunkMesh.chunk = chunk;
		}
		return block;
	}
	
	public static void literallyJustForTesting(Vector3f globalOrigin, Chunk chunk) {
		int localX = (int) (globalOrigin.x + chunk.origin.x);
		int localY = (int) globalOrigin.y;
		int localZ = (int) (globalOrigin.z + chunk.origin.z);
		System.out.println("(" + localX + " " + localY + " " + localZ + ") (" + (int)globalOrigin.x + " " + (int)globalOrigin.y + " " + (int)globalOrigin.z + ")");
	}
	
	public static void setBlock(Vector3f globalOrigin, Block block, Chunk chunk) {
		int localX = (int) (globalOrigin.x - chunk.origin.x);
		int localY = (int) globalOrigin.y;
		int localZ = (int) (globalOrigin.z - chunk.origin.z);
		
		if (isWithinChunk(localX, localY, localZ)) {
			
			if (block != null) {
	            if (chunk.blocks[localX][localY][localZ] == null) {
	                chunk.blocks[localX][localY][localZ] = block;
	            } else {
	                return;
	            }
	        } else {
	            chunk.blocks[localX][localY][localZ] = null;
	        }
			
			Main.refreshChunks();

		} else {
			//System.out.println("am i out? (" + localX + " " + localY + " " + localZ + ")");
		}
	}

	public static boolean isWithinChunk(int localX, int localY, int localZ) {
		return localX >= 0 && localX < Chunk.CHUNK_SIZE &&
				localY >= 0 && localY < Main.WORLD_HEIGHT &&
				localZ >= 0 && localZ < Chunk.CHUNK_SIZE;
	}
}
