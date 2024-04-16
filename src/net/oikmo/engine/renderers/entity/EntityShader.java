package net.oikmo.engine.renderers.entity;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.entity.Camera;
import net.oikmo.engine.renderers.ShaderProgram;
import net.oikmo.toolbox.Maths;

public class EntityShader extends ShaderProgram {

	private static String vertexFile = "/net/oikmo/engine/renderers/entity/entityVertex.glsl";
	private static String fragmentFile = "/net/oikmo/engine/renderers/entity/entityFragment.glsl";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_whiteOffset;
	private int location_skyColour;
	
	public EntityShader() {
		super(vertexFile, fragmentFile);	
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_whiteOffset = super.getUniformLocation("whiteOffset");
		location_skyColour = super.getUniformLocation("skyColour");
	}
	
	public void loadSkyColour(float r, float g, float b) {
		super.load3DVector(location_skyColour, new Vector3f(r,g,b));
	}
	
	public void loadWhiteOffset(float offset) {
		super.loadFloat(location_whiteOffset, offset);
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
