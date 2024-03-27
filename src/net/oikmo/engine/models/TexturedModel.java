package net.oikmo.engine.models;

import net.oikmo.engine.textures.ModelTexture;

public class TexturedModel {
	
	RawModel model;
	ModelTexture texture;

	public TexturedModel(RawModel model, ModelTexture texture) {
		this.model = model;
		this.texture = texture;
	}

	public RawModel getRawModel() {
		return model;
	}

	public ModelTexture getTexture() {
		return texture;
	}

	public void setRawModel(RawModel model) {
		this.model = model;
	}
}
