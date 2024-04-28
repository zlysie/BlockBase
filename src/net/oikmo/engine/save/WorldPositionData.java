package net.oikmo.engine.save;

import java.io.Serializable;

public class WorldPositionData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public int xSpawn,zSpawn;
	
	public WorldPositionData(int xSpawn, int zSpawn) {
		this.xSpawn = xSpawn;
		this.zSpawn = zSpawn;
	}
}
