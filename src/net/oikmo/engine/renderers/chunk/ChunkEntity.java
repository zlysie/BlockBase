package net.oikmo.engine.renderers.chunk;

import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.world.chunk.coordinate.ChunkCoordinates;

public class ChunkEntity {
	private TexturedModel model;
	private ChunkCoordinates position;
	
	protected boolean remove = false;
	
	public ChunkEntity(TexturedModel model, ChunkCoordinates position) {
		this.model = model;
		this.position = position;
	}
	
	public ChunkCoordinates getPosition() {
		return position;
	}

	public TexturedModel getModel() {
		return model;
	}
}