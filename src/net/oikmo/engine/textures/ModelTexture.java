package net.oikmo.engine.textures;

public class ModelTexture {
	
	private int textureID;

	public ModelTexture(int textureID) {
		this.textureID = textureID;
	}

	public void setTextureID(int textureID) {
		if(this.textureID != textureID) {
			this.textureID = textureID;
		}
	}
	
	public int getTextureID() {
		return textureID;
	}
}
