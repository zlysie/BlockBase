package net.oikmo.engine.network;

import org.newdawn.slick.ImageBuffer;

import com.esotericsoftware.kryonet.Connection;

import net.oikmo.engine.models.TexturedModel;

/**
 * Represents players on a server
 * @author Oikmo
 */
public class OtherPlayer {
	
	/** Active connection */
	public Connection c;
	/** Active connection id */
	public int id;
	
	/** Player name */
	public String userName;
	
	/** World position */
	public float x, y, z;
	/** Model rotation */
	public float rotX, rotY, rotZ;
	
	/** Texture buffer for the model */
	public ImageBuffer buffer;
	/** The player model with texture */
	public TexturedModel model;
	
	/** Currently selected block (shows on top of the head of the player) */
	public byte block = -1;
	
	/**
	 * Sets the position to given parameters
	 * @param x X position
	 * @param y Y position
	 * @param x Z position
	 */
	public void updatePosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Sets the rotation to given parameters
	 * @param rotX X rotation
	 * @param rotY Y rotation
	 * @param rotZ Z rotation
	 */
	public void updateRotation(float rotX, float rotY, float rotZ) {
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
	}
}
