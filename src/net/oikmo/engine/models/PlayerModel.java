package net.oikmo.engine.models;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.Loader;
import net.oikmo.engine.PlayerSkin;
import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.textures.ModelTexture;
import net.oikmo.engine.world.blocks.Block;

public class PlayerModel {
	
	private static int blockCount = Block.blocks.length;
	private static int texturesPerBlock = 6;
	private static float sizeOfAtlas = 16f;

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
	
	public static void setup() {
		UV_PX = setupUVPX(); //side
		UV_NX = setupUVNX(); //side
		UV_PY = setupUVPY(); //top
		UV_NY = setupUVNY(); //bottom
		UV_PZ = setupUVPZ(); //side
		UV_NZ = setupUVNZ(); //side
		createVertices();
		
		for(PlayerSkin skin : PlayerSkin.players) {
			if(skin != null) {
				skin.setModel(new TexturedModel(getRawModel(skin), new ModelTexture(ResourceLoader.loadTexture("textures/players"))));
			}
		}
	}
	
	
	public static Vector2f[] setupUVPX() {
		Vector2f[] uvArray = new Vector2f[blockCount * texturesPerBlock];

		addTextureFromXY(uvArray, PlayerSkin.none.getType(), 1, 0);
		addTextureFromXY(uvArray, PlayerSkin.dev.getType(), 4, 0);
		addTextureFromXY(uvArray, PlayerSkin.oikmo.getType(), 6, 0);
		
		return uvArray;
	}
	
	public static Vector2f[] setupUVNX() {
		Vector2f[] uvArray = new Vector2f[blockCount * texturesPerBlock];

		addTextureFromXY(uvArray, PlayerSkin.none.getType(), 0, 0);
		addTextureFromXY(uvArray, PlayerSkin.dev.getType(), 2, 0);
		addTextureFromXY(uvArray, PlayerSkin.oikmo.getType(), 5, 0);
		
		return uvArray;
	}	
	
	
	public static Vector2f[] setupUVPY() {
		Vector2f[] uvArray = new Vector2f[blockCount * texturesPerBlock];
		
		addTextureFromXY(uvArray, PlayerSkin.none.getType(), 1, 0);
		addTextureFromXY(uvArray, PlayerSkin.dev.getType(), 4, 0);
		addTextureFromXY(uvArray, PlayerSkin.oikmo.getType(), 6, 0);
		
		return uvArray;
	}
	public static Vector2f[] setupUVNY() { 
		Vector2f[] uvArray = new Vector2f[blockCount * texturesPerBlock];
		
		addTextureFromXY(uvArray, PlayerSkin.none.getType(), 1, 0);
		addTextureFromXY(uvArray, PlayerSkin.dev.getType(), 4, 0);
		addTextureFromXY(uvArray, PlayerSkin.oikmo.getType(), 6, 0);
		
		return uvArray;
	}
	
	public static Vector2f[] setupUVPZ() {
		Vector2f[] uvArray = new Vector2f[blockCount * texturesPerBlock];

		addTextureFromXY(uvArray, PlayerSkin.none.getType(), 1, 0);
		addTextureFromXY(uvArray, PlayerSkin.dev.getType(), 3, 0);
		addTextureFromXY(uvArray, PlayerSkin.oikmo.getType(), 6, 0);
		
		return uvArray;
	}	
	public static Vector2f[] setupUVNZ() {
		Vector2f[] uvArray = new Vector2f[blockCount * texturesPerBlock];

		addTextureFromXY(uvArray, PlayerSkin.none.getType(), 1, 0);
		addTextureFromXY(uvArray, PlayerSkin.dev.getType(), 3, 0);
		addTextureFromXY(uvArray, PlayerSkin.oikmo.getType(), 6, 0);
		
		return uvArray;
	}
	

	public static float[] getUVs(int is) {
		UVS = UVS == null ? new Vector2f[][]{PlayerModel.UV_PX, PlayerModel.UV_NX, PlayerModel.UV_PY, PlayerModel.UV_NY, PlayerModel.UV_PZ, PlayerModel.UV_NZ} : UVS;
		int totalLength = UVS.length * 6 * 2; // 5 vectors for each array

		float[] result = new float[totalLength];
		int index = 0;
		int startIndex = is * 6;

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

	public static int addTextureFromXY(Vector2f[] uv, int index, int x, int y) {
		float xOffset = (float) x / 16.f;
		float yOffset = (float) y / 16.f;
		int privIndex = new Integer(index*6);
		uv[privIndex++] = new Vector2f(xOffset, yOffset);
		uv[privIndex++] = new Vector2f(xOffset, (float) (y + 1) / sizeOfAtlas);
		uv[privIndex++] = new Vector2f((float) (x + 1) / sizeOfAtlas, (float) (y + 1) / sizeOfAtlas);
		uv[privIndex++] = new Vector2f((float) (x + 1) / sizeOfAtlas, (float) (y + 1) / sizeOfAtlas);
		uv[privIndex++] = new Vector2f((float) (x + 1) / sizeOfAtlas, yOffset);
		uv[privIndex++] = new Vector2f(xOffset, yOffset);

		return index;
	}
	
	private static Map<PlayerSkin, RawModel> models = new HashMap<>();

	public static RawModel getRawModel(PlayerSkin block) {
		if(models.get(block) == null) {
			models.put(block, Loader.loadToVAO(vertices, getUVs(block.getByteType()), normals));
		}
		return models.get(block);
	}
}