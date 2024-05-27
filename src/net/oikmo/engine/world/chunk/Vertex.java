package net.oikmo.engine.world.chunk;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Vertex {
	
	public int posX, posY, posZ;
	public int normal;
	
	public Vector2f uvs;
	public Vertex(Vector3f positions, float normal, Vector2f uvs) {
		posX = (int)positions.x;
		posY = (int)positions.y;
		posZ = (int)positions.z;
		this.normal = (int)(normal*100f);
		this.uvs = uvs;
	}
	
	
	
}
