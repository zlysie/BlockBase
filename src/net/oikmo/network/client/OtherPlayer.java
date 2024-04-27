package net.oikmo.network.client;

import com.esotericsoftware.kryonet.Connection;

public class OtherPlayer {
	
	public float x, y, z;
	public float rotX, rotY, rotZ;
	public int id;
	public String userName;
	public Connection c;
	
	public void updatePosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void updateRotation(float rotX, float rotY, float rotZ) {
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
	}
}
