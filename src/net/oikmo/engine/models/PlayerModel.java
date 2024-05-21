package net.oikmo.engine.models;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.Loader;

public class PlayerModel {
	private static int texturesPerBlock = 6;
	private static float sizeOfAtlas = 4f;
	
	public static float[] vertices;

	static Vector2f[][] UVS;
	static Vector3f[][] VERTS;
	
	public static Vector3f[] PX_POS = {
			new Vector3f(1f,1f,0f),
			new Vector3f(1f,0f,0f),
			new Vector3f(1f,0f,1f),
			new Vector3f(1f,0f,1f),
			new Vector3f(1f,1f,1f),
			new Vector3f(1f,1f,0f)
	};
	public static Vector3f[] NX_POS = {
			new Vector3f(0f,1f,0f),
			new Vector3f(0f,0f,0f),
			new Vector3f(0f,0f,1f),
			new Vector3f(0f,0f,1f),
			new Vector3f(0f,1f,1f),
			new Vector3f(0f,1f,0f)
	};
	public static Vector3f[] PY_POS = {
			new Vector3f(0f,1f,1f),
			new Vector3f(0f,1f,0f),
			new Vector3f(1f,1f,0f),
			new Vector3f(1f,1f,0f),
			new Vector3f(1f,1f,1f),
			new Vector3f(0f,1f,1f)
	};
	public static Vector3f[] NY_POS = {
			new Vector3f(0f,0f,1f),
			new Vector3f(0f,0f,0f),
			new Vector3f(1f,0f,0f),
			new Vector3f(1f,0f,0f),
			new Vector3f(1f,0f,1f),
			new Vector3f(0f,0f,1f)
	};
	public static Vector3f[] PZ_POS = {
			new Vector3f(0f,1f,1f),
			new Vector3f(0f,0f,1f),
			new Vector3f(1f,0f,1f),
			new Vector3f(1f,0f,1f),
			new Vector3f(1f,1f,1f),
			new Vector3f(0f,1f,1f)
	};
	public static Vector3f[] NZ_POS = {
			new Vector3f(0f,1f,0f),
			new Vector3f(0f,0f,0f),
			new Vector3f(1f,0f,0f),
			new Vector3f(1f,0f,0f),
			new Vector3f(1f,1f,0f),
			new Vector3f(0f,1f,0f)
	};

	public static Vector2f[] UV_PX;
	public static Vector2f[] UV_NX;
	public static Vector2f[] UV_PY;
	public static Vector2f[] UV_NY;
	public static Vector2f[] UV_PZ;
	public static Vector2f[] UV_NZ;
	
	public static float[] normals = {
			0.8f, 0.8f, 0.8f,
			0.8f, 0.8f, 0.8f,
			0.8f, 0.8f, 0.8f,
			0.8f, 0.8f, 0.8f,
			0.8f, 0.8f, 0.8f,
			0.8f, 0.8f, 0.8f,
			
			0.65f, 0.65f, 0.65f,
			0.65f, 0.65f, 0.65f,
			0.65f, 0.65f, 0.65f,
			0.65f, 0.65f, 0.65f,
			0.65f, 0.65f, 0.65f,
			0.65f, 0.65f, 0.65f,
			
			1f, 1f, 1f,
			1f, 1f, 1f,
			1f, 1f, 1f,
			1f, 1f, 1f,
			1f, 1f, 1f,
			1f, 1f, 1f,
			
			1f, 1f, 1f,
			1f, 1f, 1f,
			1f, 1f, 1f,
			1f, 1f, 1f,
			1f, 1f, 1f,
			1f, 1f, 1f,
			
			0.8f, 0.8f, 0.8f,
			0.8f, 0.8f, 0.8f,
			0.8f, 0.8f, 0.8f,
			0.8f, 0.8f, 0.8f,
			0.8f, 0.8f, 0.8f,
			0.8f, 0.8f, 0.8f,
			
			0.65f, 0.65f, 0.65f,
			0.65f, 0.65f, 0.65f,
			0.65f, 0.65f, 0.65f,
			0.65f, 0.65f, 0.65f,
			0.65f, 0.65f, 0.65f,
			0.65f, 0.65f, 0.65f,
	};
	
	private static RawModel model;
	
	public static void setup() {
		
		UV_PX = new Vector2f[texturesPerBlock];
		UV_NX = new Vector2f[texturesPerBlock];
		UV_PY = new Vector2f[texturesPerBlock];
		UV_NY = new Vector2f[texturesPerBlock];
		UV_PZ = new Vector2f[texturesPerBlock];
		UV_NZ = new Vector2f[texturesPerBlock];
		addTextureFromXY(UV_PX, 1, 0);
		addTextureFromXY(UV_NX, 0, 0);
		addTextureFromXY(UV_PY, 1, 0);
		addTextureFromXY(UV_NY, 1, 0);
		addTextureFromXY(UV_PZ, 1, 0);
		addTextureFromXY(UV_NZ, 1, 0);
		
		
		
		System.out.println(UV_PX);
		
		createVertices();
		
		model = Loader.loadToVAO(vertices, getUVs(), normals);
		
		System.out.println(model);
	}
	
	public static RawModel getRawModel() {
		return model;
	}

	public static float[] getUVs() {
		UVS = UVS == null ? new Vector2f[][]{PlayerModel.UV_PX, PlayerModel.UV_NX, PlayerModel.UV_PY, PlayerModel.UV_NY, PlayerModel.UV_PZ, PlayerModel.UV_NZ} : UVS;
		int totalLength = UVS.length * 6 * 2; // 5 vectors for each array

		float[] result = new float[totalLength];
		int index = 0;
		int startIndex = 0;

		for (Vector2f[] uvArray : UVS) {
			for (int i = startIndex; i < startIndex + 6; i++) {
				Vector2f uv = uvArray[i];
				result[index++] = uv.x;
				result[index++] = uv.y;
			}
		}

		return result;
	}
	public static void createVertices() {
		VERTS = VERTS == null ? new Vector3f[][]{PlayerModel.PX_POS, PlayerModel.NX_POS, PlayerModel.PY_POS, PlayerModel.NY_POS, PlayerModel.PZ_POS, PlayerModel.NZ_POS} : VERTS;
		int totalLength = VERTS.length * 6 * 3; // 6 vectors for each array

		vertices = new float[totalLength];
		int index = 0;

		for (Vector3f[] uvArray : VERTS) {
			for (int i = 0; i < 6; i++) {
				Vector3f uv = uvArray[i];
				vertices[index++] = uv.x;
				vertices[index++] = uv.y;
				vertices[index++] = uv.z;

			}
		}
	}

	public static void addTextureFromXY(Vector2f[] uv, int x, int y) {
	
		float xOffset = (float) x / 16.f;
		float yOffset = (float) y / 16.f;
		int privIndex = 0;
		uv[privIndex++] = new Vector2f(xOffset, yOffset);
		uv[privIndex++] = new Vector2f(xOffset, (float) (y + 1) / sizeOfAtlas);
		uv[privIndex++] = new Vector2f((float) (x + 1) / sizeOfAtlas, (float) (y + 1) / sizeOfAtlas);
		uv[privIndex++] = new Vector2f((float) (x + 1) / sizeOfAtlas, (float) (y + 1) / sizeOfAtlas);
		uv[privIndex++] = new Vector2f((float) (x + 1) / sizeOfAtlas, yOffset);
		uv[privIndex++] = new Vector2f(xOffset, yOffset);
	}
}