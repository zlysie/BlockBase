package net.oikmo.engine.renderers.entity;

import org.lwjgl.util.vector.Matrix4f;

import net.oikmo.engine.entity.Camera;
import net.oikmo.engine.renderers.ShaderProgram;
import net.oikmo.toolbox.Maths;

public class EntityShader extends ShaderProgram {

	private static String vertexFile = "/net/oikmo/engine/renderers/entity/entityVertex.glsl";
	private static String fragmentFile = "/net/oikmo/engine/renderers/entity/entityFragment.glsl";
	
	int location_transformationMatrix;
	int location_projectionMatrix;
	int location_viewMatrix;
	
	public EntityShader() {
		super(vertexFile, fragmentFile);	
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}
	
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix) {
		super.loadMatrix(location_projectionMatrix, matrix);
	}
	public void loadViewMatrix(Camera camera) {
		super.loadMatrix(location_viewMatrix, Maths.createViewMatrix(camera));
	}
}
