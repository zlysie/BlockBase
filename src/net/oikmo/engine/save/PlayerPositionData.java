package net.oikmo.engine.save;

import java.io.Serializable;

public class PlayerPositionData implements Serializable {
	private static final long serialVersionUID = 1L;
	public float x, y, z;
	public float rotx, roty, rotz;
	
	public PlayerPositionData(float x, float y, float z, float rotx, float roty, float rotz) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.rotx = rotx;
		this.roty = roty;
		this.rotz = rotz;
	}
}
