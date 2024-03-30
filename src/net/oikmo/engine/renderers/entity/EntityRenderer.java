package net.oikmo.engine.renderers.entity;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import net.oikmo.engine.entity.Camera;
import net.oikmo.engine.entity.Entity;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.toolbox.Maths;

public class EntityRenderer {
	
	private EntityShader shader;
	public EntityRenderer(Matrix4f projectionMatrix, float r, float g, float b) {
		shader = new EntityShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.loadSkyColour(r, g, b);
		shader.stop();
	}
	
	public void render(Map<TexturedModel, List<Entity>> entities, Camera camera) {
		synchronized(entities) {

			for(TexturedModel model : entities.keySet()) {
				shader.start();
				shader.loadViewMatrix(camera);
				GL30.glBindVertexArray(model.getRawModel().getVaoID());
				GL20.glEnableVertexAttribArray(0);
				GL20.glEnableVertexAttribArray(1);
				
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureID());
				
				List<Entity> batch = entities.get(model);
				
				for(int i = 0; i < batch.size(); i++) {
					
					Entity entity = batch.get(i);
					
					Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotation(), entity.getScale());
					shader.loadTransformationMatrix(transformationMatrix);
					shader.loadWhiteOffset(entity.getWhiteOffset()/10);
					GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getRawModel().getVertexCount());
					
				}
				
				GL20.glDisableVertexAttribArray(0);
				GL20.glDisableVertexAttribArray(1);
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