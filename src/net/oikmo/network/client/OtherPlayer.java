package net.oikmo.network.client;

import org.newdawn.slick.ImageBuffer;

import com.esotericsoftware.kryonet.Connection;

import net.oikmo.engine.models.TexturedModel;

public class OtherPlayer {
	
	public Connection c;
	public int id;
	
	public String userName;
	
	public float x, y, z;
	public float rotX, rotY, rotZ;
	
	public ImageBuffer buffer;
	public TexturedModel model;
	
	public byte block = -1;
	
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
