package net.oikmo.engine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import net.oikmo.engine.models.RawModel;
import net.oikmo.main.Main;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Logger.LogLevel;

/**
 * Loader class.
 * Loads textures, cubemaps, vbos (and updates them) and texture atlase.
 * 
 * @author Oikmo
 */
public class Loader {	

	private static Loader instance;
	public static Loader getInstance() {
		if(instance == null) {
			instance = new Loader();
		}
		return instance;
	}


	private static final int BYTES_PER_PIXEL = 4;//3 for RGB, 4 for RGBA

	private static List<Integer> vaos = new ArrayList<Integer>();
	private static List<Integer> vbos = new ArrayList<Integer>();
	private static List<Integer> textures = new ArrayList<Integer>();

	private int createVAO() {
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);

		return vaoID;
	}
	
	/**
	 * Quad maker (a plane)
	 * 
	 * @param positions
	 * @param dimensions
	 * @return RawModel
	 */
	public RawModel loadToVAO(float[] positions, int dimensions) {
		int vaoID = createVAO();
		storeDataInAttributeList(positions, 0, dimensions);
		unbindVAO();
		return new RawModel(vaoID, positions.length / dimensions);
	}
	
	public RawModel loadToVAO(float[] vertices, int[] indices, float[] uv) { 
		int vaoID = createVAO();
		storeDataInAttributeList(vertices, 0, 3);
		storeDataInAttributeList(uv, 1, 2);
		bindIndicesBuffer(indices);
		unbindVAO();
		return new RawModel(vaoID, indices.length); 
	}
	
	public RawModel loadToVAO(float[] vertices,  float[] uv) { 
		int vaoID = createVAO();
		storeDataInAttributeList(vertices, 0, 3);
		storeDataInAttributeList(uv, 1, 2);
		unbindVAO();
		return new RawModel(vaoID, vertices.length); 
	}
	
	public int loadQuadToVAO(float[] positions, float[] textureCoords) {
		int vaoID = createVAO();
		storeDataInAttributeList(positions, 0, 2);
		storeDataInAttributeList(textureCoords, 1, 2);
		unbindVAO();
		return vaoID;
	}
	
	public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(positions, 0, 3);
		storeDataInAttributeList(textureCoords, 1, 2);
		storeDataInAttributeList(normals, 2, 3);
		unbindVAO();
		return new RawModel(vaoID, indices.length);
	}
	
	private void unbindVAO() {
		GL30.glBindVertexArray(0);
	}

	private void storeDataInAttributeList(float[] data, int attributeNumber, int dimensions) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = this.storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, dimensions, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	private void bindIndicesBuffer(int[] indices) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = this.storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}

	private IntBuffer storeDataInIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	private FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}


	/**
	 * 
	 * @param name
	 * @return texture - {@code Integer}
	 */
	public int loadTexture(String name) {

		BufferedImage image = null;
		try {
			image = ImageIO.read(Loader.class.getResourceAsStream("/assets/" + name + ".png"));
		} catch (FileNotFoundException e) {
			try {
				image = ImageIO.read(Loader.class.getResourceAsStream("missingTexture.png"));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			Logger.log(LogLevel.ERROR, "Texture name \"" + name + "\" could not be found! Loading placeholder!");
		} catch (IOException e) {
			try {
				image = ImageIO.read(Loader.class.getResourceAsStream("missingTexture.png"));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		} catch(IllegalArgumentException e) {
			Logger.log(LogLevel.ERROR, "Texture name \"" + name + "\" could not be found! Loading placeholder!");
			try {
				image = ImageIO.read(Loader.class.getResourceAsStream("missingTexture.png"));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL); //4 for RGBA, 3 for RGB

		for(int y = 0; y < image.getHeight(); y++){
			for(int x = 0; x < image.getWidth(); x++){
				int pixel = pixels[y * image.getWidth() + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
				buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
				buffer.put((byte) (pixel & 0xFF));               // Blue component
				buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
			}
		}

		buffer.flip(); //DONT FORGET THIS OMG

		int textureID = GL11.glGenTextures(); //Generate texture ID
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID); //Bind texture ID
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		//Setup wrap mode
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

		//Setup texture scaling filtering
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST); //sharp

		//Send texel data to OpenGL
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		//GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -1f);

		textures.add(textureID);
		//Return the texture ID so we can bind it later again
		return textureID;
	}
	
	public int loadCustomTexture(String name) {

		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(Main.getResources()+"/custom/textures/"+name+".png"));
		} catch (FileNotFoundException e) {
			try {
				image = ImageIO.read(Loader.class.getResourceAsStream("missingTexture.png"));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			Logger.log(LogLevel.ERROR, "Texture name \"" + name + "\" could not be found! Loading placeholder!");
		} catch (IOException e) {
			try {
				image = ImageIO.read(Loader.class.getResourceAsStream("missingTexture.png"));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		} catch(IllegalArgumentException e) {
			Logger.log(LogLevel.ERROR, "Texture name \"" + name + "\" could not be found! Loading placeholder!");
			try {
				image = ImageIO.read(Loader.class.getResourceAsStream("missingTexture.png"));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL); //4 for RGBA, 3 for RGB

		for(int y = 0; y < image.getHeight(); y++){
			for(int x = 0; x < image.getWidth(); x++){
				int pixel = pixels[y * image.getWidth() + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
				buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
				buffer.put((byte) (pixel & 0xFF));               // Blue component
				buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
			}
		}

		buffer.flip(); //DONT FORGET THIS OMG

		int textureID = GL11.glGenTextures(); //Generate texture ID
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID); //Bind texture ID
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		//Setup wrap mode
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

		//Setup texture scaling filtering
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST); //sharp

		//Send texel data to OpenGL
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		//GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -1f);

		textures.add(textureID);
		//Return the texture ID so we can bind it later again
		return textureID;
	}

	/**
	 * Cleans up all the buffers and data stored on memory
	 */
	public static void cleanUp() {
		vaos.forEach(GL30::glDeleteVertexArrays);
		vbos.forEach(GL15::glDeleteBuffers);
		textures.forEach(GL11::glDeleteTextures);
	}
}
