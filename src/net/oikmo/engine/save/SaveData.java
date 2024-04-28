package net.oikmo.engine.save;

import java.io.Serializable;
import java.util.List;

public class SaveData implements Serializable {

	private static final long serialVersionUID = 1L;
	public long time;
	public long seed;
	public ChunkSaveData[] chunks;
	
	public SaveData(long seed, List<ChunkSaveData> chunks) {
		this.seed = seed;
		this.chunks = new ChunkSaveData[chunks.size()];
		for(int i = 0; i < chunks.size(); i++) {
			this.chunks[i] = chunks.get(i);
		}
	}
}
