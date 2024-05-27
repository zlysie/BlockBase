package net.oikmo.engine.world.chunk;

import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.world.chunk.coordinate.ChunkCoordinates;
import net.oikmo.toolbox.Maths;
import net.oikmo.toolbox.noise.OpenSimplexNoise;

public class MasterChunk {
	private ChunkCoordinates origin;
	private Chunk chunk;
	
	public MasterChunk(OpenSimplexNoise noiseGen, ChunkCoordinates origin) {
		this.origin = origin;
		this.chunk = new Chunk(noiseGen, origin);
	}
	
	public MasterChunk(ChunkCoordinates origin, byte[] blocks) {
		this.origin = origin;
		this.chunk = new Chunk(blocks);
	}
	
	public ChunkCoordinates getOrigin() {
		return origin;
	}
	
	public Chunk getChunk() {
		return chunk;
	}
	
	public void setBlock(Vector3f position, byte block) {
		Chunk chunk = getChunk();
		//Main.theWorld.refreshChunk(this);
		int localX = (int)(position.x + getOrigin().x)%16;
		int localY = (int) position.y;
		int localZ = (int)(position.z + getOrigin().z)%16;
		
		if(localX < 0) {
			localX = localX+16;
		}
		if(localZ < 0) {
			localZ = localZ+16;
		}
		
		if (Maths.isWithinChunk(localX, localY, localZ)) {
			if(chunk.getBlock(localX, localY, localZ) != block) {
				chunk.setBlock(localX, localY, localZ, block);
			}
		}
	}
}
