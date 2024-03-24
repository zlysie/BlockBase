package net.oikmo.engine.world.chunk;

import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.world.World;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.main.Main;

public class ChunkManager {
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

	public static void setBlock(Vector3f globalOrigin, Block block, MasterChunk chunk) {
		int localX = (int) (globalOrigin.x - chunk.getOrigin().x);
		int localY = (int) globalOrigin.y;
		int localZ = (int) (globalOrigin.z - chunk.getOrigin().z);

		if (isWithinChunk(localX, localY, localZ)) {
			if (block != null) {
				if (chunk.getChunk().blocks[localX][localY][localZ] == null) {
					if(chunk.getChunk().blocks[localX][localY][localZ] != block || chunk.getChunk().blocks[localX][localY][localZ].getType() != block.getType()) {
						chunk.getChunk().blocks[localX][localY][localZ] = block;
						Main.theWorld.refreshChunk(chunk);
					}
				} else {
					return;
				}
			} else {
				if(chunk.getChunk().blocks[localX][localY][localZ] != null) {
					chunk.getChunk().blocks[localX][localY][localZ] = null;
					Main.theWorld.refreshChunk(chunk);
				}
			}
		}
	}

	public static void setBlockFromTopLayer(int x, int z, Block block, MasterChunk chunk) {
		int localX = (int) (x - chunk.getOrigin().x);
		int localZ = (int) (z - chunk.getOrigin().z);
		if(isWithinChunk(localX, localZ)) {
			for (int y = World.WORLD_HEIGHT - 1; y >= 0; y--) {
				try {
					if (chunk.getChunk().blocks[localX][y][localZ] != null) {
						chunk.getChunk().blocks[localX][y - 0][localZ] = block;
						Main.theWorld.refreshChunk(chunk);
						break;
					}
				} catch(ArrayIndexOutOfBoundsException e) {}
			}
		}
	}

	public static boolean isWithinChunk(int localX, int localY, int localZ) {
		return localX >= 0 && localX < Chunk.CHUNK_SIZE &&
				localY >= 0 && localY < World.WORLD_HEIGHT &&
				localZ >= 0 && localZ < Chunk.CHUNK_SIZE;
	}

	public static boolean isWithinChunk(int localX, int localZ) {
		return localX >= 0 && localX < Chunk.CHUNK_SIZE &&
				localZ >= 0 && localZ < Chunk.CHUNK_SIZE;
	}
}
