package net.oikmo.engine.textures;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;

import net.oikmo.engine.DisplayManager;
import net.oikmo.toolbox.FastMath;

public class GuiTexture {
	
	private int textureID;
	private Vector2f position;
	private Vector2f scale;
	private int tilingSize = 1;
	private int zOrder;
	
	public GuiTexture(int textureID, Vector2f position, Vector2f scale) {
		this.textureID = textureID;
		
		this.scale = scale;
		this.position = position;
		
	}
	
	public void setTextureID(int texture) {
		this.textureID = texture;
	}

	public int getTextureID() {
		return textureID;
	}

	public Vector2f getPosition() {
		return position;
	}
	
	public void setPosition(Vector2f position) {
		this.position = position;
	}
	
	public void setPositionRelativeToScreen() {
		this.position = DisplayManager.getNormalizedDeviceCoords(position, scale);
	}
	
	public void setPositionRelativeToScreen(float x, float y) {
		this.position.x = x;this.position.y = y;
		this.position = DisplayManager.getNormalizedDeviceCoords(position, scale);
	}

	public Vector2f getScale() {
		return scale;
	}

	public void setScaleRelativeToScreen(Vector2f scale) {
		float offset = Display.getWidth() - Display.getHeight();
		float scaleX = FastMath.abs((scale.x / Display.getWidth()) * offset);
		float scaleY = FastMath.abs((scale.y / Display.getHeight()) * offset);
		
		this.scale = new Vector2f(scaleX, scaleY);
	}
	
	public void setPositionToTopLeft(int xOffset, int yOffset) {
		position = new Vector2f(-1+getScale().x+xOffset,1-getScale().y+yOffset);
	}
	public void setPositionToBottomLeft(int xOffset, int yOffset) {
		position = new Vector2f(-1+getScale().x+xOffset,1+getScale().y+yOffset);
	}
	public void setPositionToTopRight(int xOffset, int yOffset) {
		position = new Vector2f(1+getScale().x+xOffset,1-getScale().y+yOffset);
	}
	public void setPositionToBottomRight(int xOffset, int yOffset) {
		position = new Vector2f(1+getScale().x+xOffset,1+getScale().y+yOffset);
	}
	
	public void setScale(Vector2f scale) {
		this.scale = scale;
	}

	public int getTilingSize() {
		return tilingSize;
	}

	public void setTilingSize(int tilingSize) {
		this.tilingSize = tilingSize;
	}
	
	public int getZOrder() {
		return zOrder;
	}
}
