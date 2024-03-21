package net.oikmo.engine.renderers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Logger.LogLevel;

public abstract class ShaderProgram {
	
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	
	/**
	 * Shader Constructor
	 * @author <i>Oikmo</i>
	 */
	public ShaderProgram(String vertexFile,String fragmentFile) {
		vertexShaderID = loadShader(vertexFile,GL20.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragmentFile,GL20.GL_FRAGMENT_SHADER);
		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);
		bindAttributes();
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
		getAllUniformLocations();
	}
	
	/**
	 * Assigns all uniform locations from the shaders
	 * @author <i>Oikmo</i>
	 */
	protected abstract void getAllUniformLocations();
	
	protected int getUniformLocation(String uniformName) {
		return GL20.glGetUniformLocation(programID, uniformName);
	}
	
	public void start() {
		GL20.glUseProgram(programID);
	}
	
	public void stop() {
		GL20.glUseProgram(0);
	}
	
	public void cleanUp() {
		stop();
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		GL20.glDeleteProgram(programID);
	}
	
	/**
	 * Binds attributes from the shaders.
	 * @author <i>Oikmo</i>
	 */
	protected abstract void bindAttributes();
	
	protected void bindAttribute(int attribute, String variableName) {
		GL20.glBindAttribLocation(programID, attribute, variableName);
	}
	
	protected void loadFloat(int location, float value) {
		GL20.glUniform1f(location, value);
	}
	
	protected void loadInt(int location, int value) {
		GL20.glUniform1i(location, value);
	}
	
	protected void load4DVector(int location, Vector4f value) {
		GL20.glUniform4f(location, value.x, value.y, value.z, value.w);
	}
	
	protected void load3DVector(int location, Vector3f value) {
		GL20.glUniform3f(location, value.x, value.y, value.z);
	}
	
	protected void load2DVector(int location, Vector2f value) {
		GL20.glUniform2f(location, value.x, value.y);
	}
	
	protected void loadBoolean(int location, boolean value) {
		float bool = 0;
		if(value) {bool=1;}
		GL20.glUniform1f(location, bool);
	}
	
	protected void loadMatrix(int location, Matrix4f matrix) {
		matrix.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4(location, false, matrixBuffer);
	}
	
	private static int loadShader(String file, int type) {
		//System.out.println(type);
		StringBuilder shaderSource = new StringBuilder();
		try {
			InputStream in = ShaderProgram.class.getResourceAsStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while((line = reader.readLine()) != null) {
				shaderSource.append(line).append("//\n");
			}
			reader.close();
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS ) == GL11.GL_FALSE) {
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
			String shaderName = file.split("/")[5];
			if(type == GL20.GL_VERTEX_SHADER) {
				System.err.println("Could not compile vertex shader! " + shaderName);
			} else if(type == GL20.GL_FRAGMENT_SHADER) {
				
				System.err.println("Could not compile fragment shader! " + shaderName);
			} else {
				System.err.println("Could not compile shader! " + shaderName);
			}
			
			Logger.log(LogLevel.ERROR, "Shader couldn't be compiled. (" + shaderName + ")");
		}
		return shaderID;
	}

}