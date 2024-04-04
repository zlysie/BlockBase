package net.oikmo.engine.collision;

import java.text.DecimalFormat;

import org.lwjgl.util.vector.Vector3f;

/**
 * Axis Aligned Bounding Box.<br>
 * Collision boxes with no rotation :P
 * 
 * @author Oikmo
 */
public class AABB {
	
	private static Vector3f CUBE = new Vector3f(0.5f,0.5f,0.5f);
	private Vector3f center, halfExtents;
	
	public AABB(Vector3f center) {
		this.center = new Vector3f(center);
		this.halfExtents = CUBE;
	}
	
	public AABB(Vector3f center, Vector3f halfExtents) {
		this.center = new Vector3f(center);
		this.halfExtents = halfExtents;
	}
	
	public Collision intersects(AABB other) {
		Vector3f dist = Vector3f.sub(other.center, center, new Vector3f());
		dist.x = (float) Math.abs(dist.x);
		dist.y = (float) Math.abs(dist.y);
		dist.z = (float) Math.abs(dist.z);
		
		Vector3f temp = new Vector3f();
        temp = Vector3f.add(halfExtents, other.halfExtents, temp);
        //Logger.log(LogLevel.INFO, temp.toString());
        dist = Vector3f.sub(dist, temp, dist);
		
        return new Collision(dist, dist.x < 0 && dist.y < 0 && dist.z < 0);
	}
	
	public void correctPosition(AABB other, Collision data) {
		Vector3f correction = Vector3f.sub(other.center, center, new Vector3f());
		
		if (data.distance.x > data.distance.y && data.distance.x > data.distance.z) {
			if (correction.x > 0) {
				center.translate(data.distance.x, 0, 0);
			} else {
				center.translate(-data.distance.x, 0, 0);
			}
		} else if (data.distance.y > data.distance.z) {
			if (correction.y > 0) {
				center.translate(0, data.distance.y, 0);
			} else {
				center.translate(0, -data.distance.y, 0);
			}
		} else {
			if (correction.z > 0) {
				center.translate(0, 0, data.distance.z);
			} else {
				center.translate(0, 0, -data.distance.z);
			}
		}
	}
	
	DecimalFormat format = new DecimalFormat("#.#");
	@Override
	public String toString() {
		return "AABB[X="+format.format(getCenter().x)+", Y="+format.format(getCenter().y)+", Z="+format.format(getCenter().z) + " | X=" + format.format(getHalfExtent().x)+", Y="+format.format(getHalfExtent().y)+", Z="+format.format(getHalfExtent().z) + "]";
	}
	
	public Vector3f getCenter() { return center; }
	public void setHalfExtent(float x, float y, float z) {
		this.center.x = x;
		this.center.y = y;
		this.center.z = z;
	}
	public Vector3f getHalfExtent() { return halfExtents; }
	
}
