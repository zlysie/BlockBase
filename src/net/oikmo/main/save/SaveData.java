package net.oikmo.main.save;

import java.io.Serializable;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.world.blocks.Block;

public class SaveData implements Serializable {

	private static final long serialVersionUID = 1L;
	public long time;
	public Map<Vector3f, Block[][][]> chunks;
	
	public SaveData(Map<Vector3f, Block[][][]> chunks) {
		this.chunks = chunks;
	}
}
