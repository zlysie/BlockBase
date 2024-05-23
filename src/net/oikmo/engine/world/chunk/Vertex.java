package net.oikmo.engine.world.chunk;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Vertex {
	
	public Vector3f normals;
	
	public int posX, posY, posZ;
	
	public Vector2f uvs;
	public Vertex(Vector3f positions, Vector3f normals, Vector2f uvs) {
		posX = (int)positions.x;
		posY = (int)positions.y;
		posZ = (int)positions.z;
		this.normals = normals;
		this.uvs = uvs;
	}
	
	
	
}
