package net.oikmo.engine.renderers.chunk;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.entity.Camera;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.toolbox.Maths;

public class ChunkRenderer {
	
	private ChunkShader shader;
	public ChunkRenderer(Matrix4f projectionMatrix, float r, float g, float b) {
		shader = new ChunkShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.loadSkyColour(r, g, b);
		shader.stop();
	}
	
	public void render(Map<TexturedModel, List<ChunkEntity>> chunkEntities, Camera camera) {
		synchronized(chunkEntities) {

			for(TexturedModel model : chunkEntities.keySet()) {
				shader.start();
				shader.loadViewMatrix(camera);
				GL30.glBindVertexArray(model.getRawModel().getVaoID());
				GL20.glEnableVertexAttribArray(0);
				GL20.glEnableVertexAttribArray(1);
				GL20.glEnableVertexAttribArray(2);
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());
				
				List<ChunkEntity> batch = chunkEntities.get(model);
				
				for(int i = 0; i < batch.size(); i++) {
					
					ChunkEntity chunk = batch.get(i);
					
					Vector3f position = new Vector3f(chunk.getPosition().x, 0, chunk.getPosition().z);
					
					Matrix4f transformationMatrix = Maths.createTransformationMatrix(position, new Vector3f(0,0,0), 1);
					shader.loadTransformationMatrix(transformationMatrix);
					GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getRawModel().getVertexCount());
					
				}
				
				GL20.glDisableVertexAttribArray(0);
				GL20.glDisableVertexAttribArray(1);
				GL20.glDisableVertexAttribArray(2);
				GL30.glBindVertexArray(0);
				shader.stop();
			}
		}
	}

	public void updateProjectionMatrix(Matrix4f projectionMatrix) {
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
		
	}
}