package net.oikmo.engine.models;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.Loader;
import net.oikmo.engine.world.blocks.Block;

public class CubeModel {
	
	private static int blockCount = Block.blocks.length;
	private static int texturesPerBlock = 6;
	private static float sizeOfAtlas = 16f;

	public static float[] vertices;

	private static Vector2f[][] UVS;
	private static Vector3f[][] VERTS;
	
	private static Map<Block, RawModel> models = new HashMap<>();
	
	public static Vector3f[] PX_POS = {
			new Vector3f(1f,0f,1f),
			new Vector3f(1f,0f,0f),
			new Vector3f(1f,1f,0f),
			new Vector3f(1f,1f,0f),
			new Vector3f(1f,1f,1f),
			new Vector3f(1f,0f,1f),
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
			new Vector3f(1f,1f,1f),
			new Vector3f(1f,1f,0f),
			new Vector3f(1f,1f,0f),
			new Vector3f(0f,1f,0f),
			new Vector3f(0f,1f,1f),
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
			new Vector3f(1f,1f,0f),
			new Vector3f(1f,0f,0f),
			new Vector3f(1f,0f,0f),
			new Vector3f(0f,0f,0f),
			new Vector3f(0f,1f,0f),
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
	}
	
	public static Vector2f[] setupUVPX() {
		Vector2f[] uvArray = new Vector2f[blockCount * texturesPerBlock];
		boolean flip = false;
		addTextureFromXYWeird(uvArray, Block.grass.getType(), 1, 0, flip);
		addTextureFromXYWeird(uvArray, Block.dirt.getType(), 2, 0, flip);
		addTextureFromXYWeird(uvArray, Block.stone.getType(), 3, 0, flip);
		addTextureFromXYWeird(uvArray, Block.bedrock.getType(), 4, 0, flip);
		addTextureFromXYWeird(uvArray, Block.cobble.getType(), 5, 0, flip);
		addTextureFromXYWeird(uvArray, Block.mossycobble.getType(), 6, 0, flip);
		addTextureFromXYWeird(uvArray, Block.obsidian.getType(), 7, 0, flip);
		addTextureFromXYWeird(uvArray, Block.oakleaf.getType(), 0, 2, flip);
		addTextureFromXYWeird(uvArray, Block.oaklog.getType(), 1, 2, flip);
		addTextureFromXYWeird(uvArray, Block.oakplanks.getType(), 3, 2, flip);
		addTextureFromXYWeird(uvArray, Block.glass.getType(), 0, 3, flip);
		addTextureFromXYWeird(uvArray, Block.smoothstone.getType(), 1, 3, flip);
		addTextureFromXYWeird(uvArray, Block.brick.getType(), 3, 3, flip);
		addTextureFromXYWeird(uvArray, Block.ironBlock.getType(), 14, 0, flip);
		addTextureFromXYWeird(uvArray, Block.goldBlock.getType(), 14, 1, flip);
		addTextureFromXYWeird(uvArray, Block.diamondBlock.getType(), 14, 2, flip);
		addTextureFromXYWeird(uvArray, Block.tnt.getType(), 0, 4, flip);
		
		return uvArray;
	}	
	public static Vector2f[] setupUVNX() {
		Vector2f[] uvArray = new Vector2f[blockCount * texturesPerBlock];

		addTextureFromXY(uvArray, Block.grass.getType(), 1, 0);
		addTextureFromXY(uvArray, Block.dirt.getType(), 2, 0);
		addTextureFromXY(uvArray, Block.stone.getType(), 3, 0);
		addTextureFromXY(uvArray, Block.bedrock.getType(), 4, 0);
		addTextureFromXY(uvArray, Block.cobble.getType(), 5, 0);
		addTextureFromXY(uvArray, Block.mossycobble.getType(), 6, 0);
		addTextureFromXY(uvArray, Block.obsidian.getType(), 7, 0);
		addTextureFromXY(uvArray, Block.oakleaf.getType(), 0, 2);
		addTextureFromXY(uvArray, Block.oaklog.getType(), 1, 2);
		addTextureFromXY(uvArray, Block.oakplanks.getType(), 3, 2);
		addTextureFromXY(uvArray, Block.glass.getType(), 0, 3);
		addTextureFromXY(uvArray, Block.smoothstone.getType(), 1, 3);
		addTextureFromXY(uvArray, Block.brick.getType(), 3, 3);
		addTextureFromXY(uvArray, Block.ironBlock.getType(), 14, 0);
		addTextureFromXY(uvArray, Block.goldBlock.getType(), 14, 1);
		addTextureFromXY(uvArray, Block.diamondBlock.getType(), 14, 2);
		addTextureFromXY(uvArray, Block.tnt.getType(), 0, 4);
		
		return uvArray;
	}
	public static Vector2f[] setupUVPY() {
		Vector2f[] uvArray = new Vector2f[blockCount * texturesPerBlock];
		addTextureFromXY(uvArray, Block.grass.getType(), 0, 0);
		addTextureFromXY(uvArray, Block.dirt.getType(), 2, 0);
		addTextureFromXY(uvArray, Block.stone.getType(), 3, 0);
		addTextureFromXY(uvArray, Block.bedrock.getType(), 4, 0);
		addTextureFromXY(uvArray, Block.cobble.getType(), 5, 0);
		addTextureFromXY(uvArray, Block.mossycobble.getType(), 6, 0);
		addTextureFromXY(uvArray, Block.obsidian.getType(), 7, 0);
		addTextureFromXY(uvArray, Block.oakleaf.getType(), 0, 2);
		addTextureFromXY(uvArray, Block.oaklog.getType(), 2, 2);
		addTextureFromXY(uvArray, Block.oakplanks.getType(), 3, 2);
		addTextureFromXY(uvArray, Block.glass.getType(), 0, 3);
		addTextureFromXY(uvArray, Block.smoothstone.getType(), 2, 3);
		addTextureFromXY(uvArray, Block.brick.getType(), 3, 3);
		addTextureFromXY(uvArray, Block.ironBlock.getType(), 13, 0);
		addTextureFromXY(uvArray, Block.goldBlock.getType(), 13, 1);
		addTextureFromXY(uvArray, Block.diamondBlock.getType(), 13, 2);
		addTextureFromXY(uvArray, Block.tnt.getType(), 1, 4);
		
		return uvArray;
	}
	public static Vector2f[] setupUVNY() { 
		Vector2f[] uvArray = new Vector2f[blockCount * texturesPerBlock];
		addTextureFromXY(uvArray, Block.grass.getType(), 2, 0);
		addTextureFromXY(uvArray, Block.dirt.getType(), 2, 0);
		addTextureFromXY(uvArray, Block.stone.getType(), 3, 0);
		addTextureFromXY(uvArray, Block.bedrock.getType(), 4, 0);
		addTextureFromXY(uvArray, Block.cobble.getType(), 5, 0);
		addTextureFromXY(uvArray, Block.mossycobble.getType(), 6, 0);
		addTextureFromXY(uvArray, Block.obsidian.getType(), 7, 0);
		addTextureFromXY(uvArray, Block.oakleaf.getType(), 0, 2);
		addTextureFromXY(uvArray, Block.oaklog.getType(), 2, 2);
		addTextureFromXY(uvArray, Block.oakplanks.getType(), 3, 2);
		addTextureFromXY(uvArray, Block.glass.getType(), 0, 3);
		addTextureFromXY(uvArray, Block.smoothstone.getType(), 2, 3);
		addTextureFromXY(uvArray, Block.brick.getType(), 3, 3);
		addTextureFromXY(uvArray, Block.ironBlock.getType(), 15, 0);
		addTextureFromXY(uvArray, Block.goldBlock.getType(), 15, 1);
		addTextureFromXY(uvArray, Block.diamondBlock.getType(), 15, 2);
		addTextureFromXY(uvArray, Block.tnt.getType(), 2, 4);
		
		return uvArray;
	}
	public static Vector2f[] setupUVPZ() {
		Vector2f[] uvArray = new Vector2f[blockCount * texturesPerBlock];

		addTextureFromXY(uvArray, Block.grass.getType(), 1, 0);
		addTextureFromXY(uvArray, Block.dirt.getType(), 2, 0);
		addTextureFromXY(uvArray, Block.stone.getType(), 3, 0);
		addTextureFromXY(uvArray, Block.bedrock.getType(), 4, 0);
		addTextureFromXY(uvArray, Block.cobble.getType(), 5, 0);
		addTextureFromXY(uvArray, Block.mossycobble.getType(), 6, 0);
		addTextureFromXY(uvArray, Block.obsidian.getType(), 7, 0);
		addTextureFromXY(uvArray, Block.oakleaf.getType(), 0, 2);
		addTextureFromXY(uvArray, Block.oaklog.getType(), 1, 2);
		addTextureFromXY(uvArray, Block.oakplanks.getType(), 3, 2);
		addTextureFromXY(uvArray, Block.glass.getType(), 0, 3);
		addTextureFromXY(uvArray, Block.smoothstone.getType(), 1, 3);
		addTextureFromXY(uvArray, Block.brick.getType(), 3, 3);
		addTextureFromXY(uvArray, Block.ironBlock.getType(), 14, 0);
		addTextureFromXY(uvArray, Block.goldBlock.getType(), 14, 1);
		addTextureFromXY(uvArray, Block.diamondBlock.getType(), 14, 2);
		addTextureFromXY(uvArray, Block.tnt.getType(), 0, 4);
		
		return uvArray;
	}	
	public static Vector2f[] setupUVNZ() {
		Vector2f[] uvArray = new Vector2f[blockCount * texturesPerBlock];
		
		boolean flip = true;
		
		addTextureFromXY(uvArray, Block.grass.getType(), 1, 0, flip);
		addTextureFromXY(uvArray, Block.dirt.getType(), 2, 0, flip);
		addTextureFromXY(uvArray, Block.stone.getType(), 3, 0, flip);
		addTextureFromXY(uvArray, Block.bedrock.getType(), 4, 0, flip);
		addTextureFromXY(uvArray, Block.cobble.getType(), 5, 0, flip);
		addTextureFromXY(uvArray, Block.mossycobble.getType(), 6, 0, flip);
		addTextureFromXY(uvArray, Block.obsidian.getType(), 7, 0, flip);
		addTextureFromXY(uvArray, Block.oakleaf.getType(), 0, 2, flip);
		addTextureFromXY(uvArray, Block.oaklog.getType(), 1, 2, flip);
		addTextureFromXY(uvArray, Block.oakplanks.getType(), 3, 2, flip);
		addTextureFromXY(uvArray, Block.glass.getType(), 0, 3, flip);
		addTextureFromXY(uvArray, Block.smoothstone.getType(), 1, 3, flip);
		addTextureFromXY(uvArray, Block.brick.getType(), 3, 3, flip);
		addTextureFromXY(uvArray, Block.ironBlock.getType(), 14, 0, flip);
		addTextureFromXY(uvArray, Block.goldBlock.getType(), 14, 1, flip);
		addTextureFromXY(uvArray, Block.diamondBlock.getType(), 14, 2, flip);
		addTextureFromXY(uvArray, Block.tnt.getType(), 0, 4, flip);
		
		return uvArray;
	}

	public static float[] getUVs(int is) {
		UVS = UVS == null ? new Vector2f[][]{CubeModel.UV_PX, CubeModel.UV_NX, CubeModel.UV_PY, CubeModel.UV_NY, CubeModel.UV_PZ, CubeModel.UV_NZ} : UVS;
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
		VERTS = VERTS == null ? new Vector3f[][]{CubeModel.PX_POS, CubeModel.NX_POS, CubeModel.PY_POS, CubeModel.NY_POS, CubeModel.PZ_POS, CubeModel.NZ_POS} : VERTS;
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

	public static void addTextureFromXY(Vector2f[] uv, int index, int x, int y) {
		addTextureFromXY(uv, index, x, y, false);
	}
	
	public static void addTextureFromXYWeird(Vector2f[] uv, int index, int x, int y, boolean flip) {
		y = (int) (sizeOfAtlas-y)-1;
		float xOffset = (float) x / 16f;
		float yOffset = (float) y / 16f;
		
		int privIndex = new Integer(index*6);
		System.out.println(privIndex);
		uv[privIndex+0] = new Vector2f(xOffset, yOffset);
		uv[privIndex+0].y = -uv[privIndex+0].y;
		uv[privIndex+1] = new Vector2f((x + 1) / sizeOfAtlas, yOffset);
		uv[privIndex+1].y = -uv[privIndex+1].y;
		uv[privIndex+2] = new Vector2f((x + 1) / sizeOfAtlas, (y + 1) / sizeOfAtlas);
		uv[privIndex+2].y = -uv[privIndex+2].y;
		uv[privIndex+3] = new Vector2f((x + 1) / sizeOfAtlas, (y + 1) / sizeOfAtlas);
		uv[privIndex+3].y = -uv[privIndex+3].y;
		uv[privIndex+4] = new Vector2f(xOffset, (float) (y + 1) / sizeOfAtlas);
		uv[privIndex+4].y = -uv[privIndex+4].y;
		uv[privIndex+5] = new Vector2f(xOffset, yOffset);
		uv[privIndex+5].y = -uv[privIndex+5].y;
	}
	
	
	public static void addTextureFromXY(Vector2f[] uv, int index, int x, int y, boolean flip) {
		x = flip ? (int) (sizeOfAtlas-x)-1 : x;
		float xOffset = (float) x / 16.f;
		float yOffset = (float) y / 16.f;
		int privIndex = new Integer(index*6);
		if(!flip) {
			uv[privIndex++] = new Vector2f(xOffset, yOffset);
			uv[privIndex++] = new Vector2f(xOffset, (float) (y + 1) / sizeOfAtlas);
			uv[privIndex++] = new Vector2f((float) (x + 1) / sizeOfAtlas, (float) (y + 1) / sizeOfAtlas);
			uv[privIndex++] = new Vector2f((float) (x + 1) / sizeOfAtlas, (float) (y + 1) / sizeOfAtlas);
			uv[privIndex++] = new Vector2f((float) (x + 1) / sizeOfAtlas, yOffset);
			uv[privIndex++] = new Vector2f(xOffset, yOffset);
		} else {
			uv[privIndex] = new Vector2f(xOffset, yOffset);
			uv[privIndex].x = -uv[privIndex].x;
			uv[privIndex+1] = new Vector2f((x + 1) / sizeOfAtlas, yOffset);
			uv[privIndex+1].x = -uv[privIndex+1].x;
			uv[privIndex+2] = new Vector2f((x + 1) / sizeOfAtlas, (y + 1) / sizeOfAtlas);
			uv[privIndex+2].x = -uv[privIndex+2].x;
			uv[privIndex+3] = new Vector2f((x + 1) / sizeOfAtlas, (y + 1) / sizeOfAtlas);
			uv[privIndex+3].x = -uv[privIndex+3].x;
			uv[privIndex+4] = new Vector2f(xOffset, (float) (y + 1) / sizeOfAtlas);
			uv[privIndex+4].x = -uv[privIndex+4].x;
			uv[privIndex+5] = new Vector2f(xOffset, yOffset);
			uv[privIndex+5].x = -uv[privIndex+5].x;
		}
	}
	
	public static RawModel getRawModel(Block block) {
		if(models.get(block) == null) {
			models.put(block, Loader.loadToVAO(vertices, getUVs(block.getByteType()), normals));
		}
		return models.get(block);
	}
}