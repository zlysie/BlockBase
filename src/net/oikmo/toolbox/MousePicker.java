package net.oikmo.toolbox;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import net.oikmo.engine.entity.Camera;

public class MousePicker {
	private Vector3f currentRay;

	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	private Camera camera;

	public MousePicker(Camera camera, Matrix4f projectionMatrix) {
		this.camera = camera;
		this.projectionMatrix = projectionMatrix;
		this.viewMatrix = Maths.createViewMatrix(camera);
	}

	public Vector3f getCurrentRay() {
		return currentRay;
	}

	public void update() {
		viewMatrix = Maths.createViewMatrix(camera);
		currentRay = calculateMouseRay();
	}
	
	public final int BASE_DISTANCE = 5;
	public int distance = BASE_DISTANCE;
	
	public Vector3f getPoint() {
		return binarySearch(0, distance, currentRay);
	}
	
	public Vector3f getPointRounded() {
		Vector3f point =  binarySearch(0, distance, currentRay);
		point.x = Maths.roundFloat(point.x);
		point.y = Maths.roundFloat(point.y);
		point.z = Maths.roundFloat(point.z);
		
		return point;
	}
	
	public Vector3f getPointRounded(int distance) {
		Vector3f point =  binarySearch(0, distance, currentRay);
		point.x = Maths.roundFloat(point.x);
		point.y = Maths.roundFloat(point.y);
		point.z = Maths.roundFloat(point.z);
		
		return point;
	}
	
	public Vector3f getPoint(int distance) {
		return binarySearch(0, distance, currentRay);
	}

	public Vector3f calculateMouseRay() {
		Vector2f normalisedCoords = getNormalisedDeviceCoords(Display.getWidth()/2, Display.getHeight()/2);
		Vector4f clipCoords = new Vector4f(normalisedCoords.x, normalisedCoords.y, -1f, 1f);
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		Vector3f worldRay = toWorldCoords(eyeCoords);
		return worldRay;
	}

	private Vector3f toWorldCoords(Vector4f eyeCoords) {
		Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
		Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoords, null);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalise();
		return mouseRay;
	}

	private Vector4f toEyeCoords(Vector4f clipCoords) {
		Matrix4f invertedProjection = Matrix4f.invert(projectionMatrix, null);
		Vector4f eyeCoords = Matrix4f.transform(invertedProjection, clipCoords, null);
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1, 0f);
	}

	private Vector2f getNormalisedDeviceCoords(float mouseX, float mouseY) {
		float x = (2f * mouseX) / Display.getWidth() - 1;
		float y = (2f * mouseY) / Display.getHeight() - 1;
		return new Vector2f(x,y);
	}

	public Vector3f getPointOnRay(float distance) {
		if(getCurrentRay() == null) { return new Vector3f(0,0,0); } 
		Vector3f camPos = camera.getPosition();
		Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
		Vector3f scaledRay = new Vector3f(getCurrentRay().x * distance, getCurrentRay().y * distance, getCurrentRay().z * distance);
		return Vector3f.add(start, scaledRay, null);
	}

	public Vector3f getPointOnRay(Vector3f ray, float distance) {
		Vector3f camPos = camera.getPosition();
		Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
		Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
		return Vector3f.add(start, scaledRay, null);
	}

	private Vector3f binarySearch(float start, float finish, Vector3f ray) {
		float half = finish;
		Vector3f endPoint = getPointOnRay(ray, half);
		return endPoint;
	}
}
