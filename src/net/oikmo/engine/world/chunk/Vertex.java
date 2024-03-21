package net.oikmo.engine.world.chunk;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Vertex {
	
	public Vector3f positions, normals;
	public Vector2f uvs;
	public Vertex(Vector3f positions, Vector3f normals, Vector2f uvs) {
		this.positions = positions;
		this.normals = normals;
		this.uvs = uvs;
	}
	
	
	
}
