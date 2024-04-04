package net.oikmo.engine.save;

import java.io.Serializable;
import java.util.List;

import net.oikmo.engine.entity.Player;

public class SaveData implements Serializable {

	private static final long serialVersionUID = 1L;
	public long time;
	public long seed;
	public ChunkSaveData[] chunks;
	public float x, y, z;
	public float rotX, rotY, rotZ;
	
	public SaveData(long seed, List<ChunkSaveData> chunks, Player player) {
		this.seed = seed;
		this.chunks = new ChunkSaveData[chunks.size()];
		for(int i = 0; i < chunks.size(); i++) {
			this.chunks[i] = chunks.get(i);
		}
		this.x = player.getPosition().x;
		this.y = player.getPosition().y;
		this.z = player.getPosition().z;
		this.rotX = player.getCamera().getPitch();
		this.rotY = player.getCamera().getYaw();
		this.rotZ = player.getCamera().getRoll();
	}
}
