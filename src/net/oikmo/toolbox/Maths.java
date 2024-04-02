package net.oikmo.toolbox;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.entity.Camera;
import net.oikmo.engine.world.World;
import net.oikmo.engine.world.chunk.Chunk;
import net.oikmo.engine.world.chunk.MasterChunk;

public class Maths {

	private static Vector3f rxTable = new Vector3f(1,0,0);
	private static Vector3f ryTable = new Vector3f(0,1,0);
	private static Vector3f rzTable = new Vector3f(0,0,1);

	private static List<Vector3f> scaleTable = new ArrayList<>();

	private static Vector3f getScaleFromPool(float scale) {
		Vector3f result = null;
		if(scaleTable.size() != 0) {
			for(Vector3f vec : scaleTable) {
				if(vec.x == scale && vec.x == scale && vec.x == scale) {
					result = vec;
				}
			}
			if(result == null) {
				Vector3f scaleVec = new Vector3f(scale,scale,scale);
				scaleTable.add(scaleVec);
				return scaleVec;
			}
		} else {
			scaleTable.add(new Vector3f(scale, scale, scale));
			result =  scaleTable.get(0);
		}
		return result;
	}

	public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, float scale) {	
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();

		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.x), rxTable, matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.y), ryTable, matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.z), rzTable, matrix, matrix);
		Matrix4f.scale(getScaleFromPool(scale), matrix, matrix);
		return matrix;

	}
	
	/**
	 * Creates and returns a transformation matrix so that 2D is real.<br>
	 * 
	 * @param translation - to be positioned. ({@link Vector2f})
	 * @param scale - to be scaled. ({@link Vector2f})
	 * 
	 * @return <b>matrix</b> - ({@link Matrix4f})
	 */
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		return matrix;
	}

	public static Matrix4f createViewMatrix(Camera camera) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		
		Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1,0,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0,1,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(camera.getRoll()), new Vector3f(0,0,1), matrix, matrix);
		Matrix4f.translate(new Vector3f(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z), matrix, matrix);
		
		return matrix;
	}
	
	public static int roundFloat(float number) {
		int rounded;
		if (number - (int) number >= 0.0 && number - (int) number < 1.0) {
			rounded = Math.round(number - 0.1f);
        } else {
            rounded = Math.round(number);
        }
		return rounded;
	}
	
	public static void roundVector(Vector3f vector) {
		vector.x = Maths.roundFloat(vector.x);
		vector.y = Maths.roundFloat(vector.y);
		vector.z = Maths.roundFloat(vector.z);
	}
	
	public static void roundVector(Vector3f input, Vector3f output) {
		output.x = Maths.roundFloat(input.x);
		output.y = Maths.roundFloat(input.y);
		output.z = Maths.roundFloat(input.z);
	}
	
	/**
	 * Converts string to long via taking each character of the string and converting it into a number. Then that number is added to string to be parsed to {@link Long#valueOf(String)}
	 * @param name - {@link String}
	 * @return {@link Long}
	 */
	public static long getSeedFromName(String name) {
		String finalString = "";
		for(int i = 0; i < name.length(); i++) {
			char ch = (char) name.getBytes()[i];
			int pos = Math.abs(ch - 'a' + 1);
			finalString += pos;
		}

		return Long.valueOf(finalString);
	}
	
	public static boolean isWithinChunk(int localX, int localY, int localZ) {
		return localX >= 0 && localX < Chunk.CHUNK_SIZE &&
				localY >= 0 && localY < World.WORLD_HEIGHT &&
				localZ >= 0 && localZ < Chunk.CHUNK_SIZE;
	}

	public static boolean isWithinChunk(int localX, int localZ) {
		return localX >= 0 && localX < Chunk.CHUNK_SIZE &&
				localZ >= 0 && localZ < Chunk.CHUNK_SIZE;
	}
	
	public static void calculateChunkPosition(Vector3f input, Vector3f output) {
		if(input.x >= 0) {
			output.x = (int) (input.x / Chunk.CHUNK_SIZE)*16;
		} else {
			if(input.x > -16) {
				output.x = (int)-1*16;
			} else {
				output.x = (int) ((input.x / Chunk.CHUNK_SIZE)-1)*16;
			}
		}

		if(input.z >= 0) {
			output.z = (int) (input.z / Chunk.CHUNK_SIZE) * 16;
		} else {
			if(input.z > -16) {
				output.z = (int)-1 * 16;
			} else {
				output.z = (int) ((input.z / Chunk.CHUNK_SIZE)-1)*16;
			}
		}
	}
}
