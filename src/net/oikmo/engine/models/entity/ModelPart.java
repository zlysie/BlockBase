package net.oikmo.engine.models.entity;

import net.oikmo.engine.models.TexturedModel;

public class ModelPart {
	
	private TexturedModel cube;
	private float x, y, z;
	private float rotx, roty, rotz;
	
	public ModelPart(TexturedModel cube, float x, float y, float z, float rotx, float roty, float rotz) {
		this.cube = cube;
		this.x = x;
		this.y = y;
		this.z = z;
		this.rotx = rotx;
		this.roty = roty;
		this.rotz = rotz;
	}
	
	public TexturedModel getCube() {
		return cube;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float getRotx() {
		return rotx;
	}

	public void setRotx(float rotx) {
		this.rotx = rotx;
	}

	public float getRoty() {
		return roty;
	}

	public void setRoty(float roty) {
		this.roty = roty;
	}

	public float getRotz() {
		return rotz;
	}

	public void setRotz(float rotz) {
		this.rotz = rotz;
	}
}
