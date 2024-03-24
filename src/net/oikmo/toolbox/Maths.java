package net.oikmo.toolbox;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.entity.Camera;

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
}
