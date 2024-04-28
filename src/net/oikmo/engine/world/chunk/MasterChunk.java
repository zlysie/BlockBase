package net.oikmo.engine.world.chunk;

import org.lwjgl.util.vector.Vector3f;

import net.oikmo.toolbox.noise.OpenSimplexNoise;

public class MasterChunk {
	private Vector3f origin;
	private Chunk chunk;
	
	public MasterChunk(OpenSimplexNoise noiseGen, Vector3f origin) {
		this.origin = origin;
		this.chunk = new Chunk(noiseGen, origin);
	}
	
	public MasterChunk(Vector3f origin, byte[][][] blocks) {
		this.origin = origin;
		this.chunk = new Chunk(blocks);
	}
	
	public Vector3f getOrigin() {
		return origin;
	}
	
	public Chunk getChunk() {
		return chunk;
	}

	public void replaceBlocks(byte[][][] blocks) {
		this.chunk.blocks = blocks;
		this.chunk.calculateHeights();
	}
}
