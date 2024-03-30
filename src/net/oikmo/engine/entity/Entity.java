package net.oikmo.engine.entity;

import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.models.RawModel;
import net.oikmo.engine.models.TexturedModel;

public class Entity {
	
	public boolean line;
	private TexturedModel model;
	private Vector3f position, rotation;
	private float scale;
	
	public Entity(TexturedModel model, Vector3f position, Vector3f rotation, float scale) {
		this.model = model;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}
	
	public Entity(boolean line, TexturedModel model, Vector3f position, Vector3f rotation, float scale) {
		this.line = line;
		this.model = model;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}
	
	public void setRawModel(RawModel model) {
		this.model.setRawModel(model);
	}
	
	public void setTextureID(int textureID) {
		this.model.getTexture().setTextureID(textureID);
	}
	
	private float whiteOffset;
	public void setWhiteOffset(float whiteOffset) {
		this.whiteOffset = whiteOffset;
	}
	public float getWhiteOffset() {
		return whiteOffset;
	}
	
	public TexturedModel getModel() {
		return model;
	}
	
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	
	public void increasePosition(float x, float y, float z) {
		this.position.x += x;
		this.position.y += y;
		this.position.z += z;	
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}
	
	public void increaseRotation(float x, float y, float z) {
		this.rotation.x += x;
		this.rotation.y += y;
		this.rotation.z += z;	
	}
	
	
	public Vector3f getRotation() {
		return rotation;
	}
	
	public float getScale() {
		return scale;
	}
}
