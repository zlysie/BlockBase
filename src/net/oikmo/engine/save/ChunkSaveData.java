package net.oikmo.engine.save;

import java.io.Serializable;

import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.inventory.Container;

public class ChunkSaveData implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public int x;
	public int z;
	public byte[][][] blocks;
	
	public ChunkSaveData(Vector3f vec, byte[][][] blocks) {
		this.x = (int)vec.x;
		this.z = (int)vec.z;
		this.blocks = blocks;
	}
}
