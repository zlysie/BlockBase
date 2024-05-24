package net.oikmo.engine.save;

import java.io.Serializable;

import net.oikmo.engine.world.chunk.coordinate.ChunkCoordinates;

public class ChunkSaveData implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public int x;
	public int z;
	public byte[][][] blocks;
	
	public ChunkSaveData(ChunkCoordinates vec, byte[][][] blocks) {
		this.x = (int)vec.x;
		this.z = (int)vec.z;
		this.blocks = blocks;
	}
}
