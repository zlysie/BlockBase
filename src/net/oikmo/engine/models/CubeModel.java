package net.oikmo.engine.models;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.Loader;
import net.oikmo.engine.world.blocks.Block;

public class CubeModel {

	public static Vector3f[] PX_POS = {
			
			new Vector3f(0.5f,0.5f,-0.5f),
			new Vector3f(0.5f,-0.5f,-0.5f),
			new Vector3f(0.5f,-0.5f,0.5f),
			new Vector3f(0.5f,-0.5f,0.5f),
			new Vector3f(0.5f,0.5f,0.5f),
			new Vector3f(0.5f,0.5f,-0.5f)
			
	};
	public static Vector3f[] NX_POS = {
			
			new Vector3f(-0.5f,0.5f,-0.5f),
			new Vector3f(-0.5f,-0.5f,-0.5f),
			new Vector3f(-0.5f,-0.5f,0.5f),
			new Vector3f(-0.5f,-0.5f,0.5f),
			new Vector3f(-0.5f,0.5f,0.5f),
			new Vector3f(-0.5f,0.5f,-0.5f)
			
	};
	public static Vector3f[] PY_POS = {
			
			new Vector3f(-0.5f,0.5f,0.5f),
			new Vector3f(-0.5f,0.5f,-0.5f),
			new Vector3f(0.5f,0.5f,-0.5f),
			new Vector3f(0.5f,0.5f,-0.5f),
			new Vector3f(0.5f,0.5f,0.5f),
			new Vector3f(-0.5f,0.5f,0.5f)
			
	};
	public static Vector3f[] NY_POS = {
			
			new Vector3f(-0.5f,-0.5f,0.5f),
			new Vector3f(-0.5f,-0.5f,-0.5f),
			new Vector3f(0.5f,-0.5f,-0.5f),
			new Vector3f(0.5f,-0.5f,-0.5f),
			new Vector3f(0.5f,-0.5f,0.5f),
			new Vector3f(-0.5f,-0.5f,0.5f)
			
	};
	public static Vector3f[] PZ_POS = {
			
			new Vector3f(-0.5f,0.5f,0.5f),
			new Vector3f(-0.5f,-0.5f,0.5f),
			new Vector3f(0.5f,-0.5f,0.5f),
			new Vector3f(0.5f,-0.5f,0.5f),
			new Vector3f(0.5f,0.5f,0.5f),
			new Vector3f(-0.5f,0.5f,0.5f)
			
	};
	public static Vector3f[] NZ_POS = {
			
			new Vector3f(-0.5f,0.5f,-0.5f),
			new Vector3f(-0.5f,-0.5f,-0.5f),
			new Vector3f(0.5f,-0.5f,-0.5f),
			new Vector3f(0.5f,-0.5f,-0.5f),
			new Vector3f(0.5f,0.5f,-0.5f),
			new Vector3f(-0.5f,0.5f,-0.5f)
			
	};
	
	public static Vector2f[] UV_PX;
	public static Vector2f[] UV_NX;
	public static Vector2f[] UV_PY;
	public static Vector2f[] UV_NY;
	public static Vector2f[] UV_PZ;
	public static Vector2f[] UV_NZ;
	
	public static Vector3f[] NORMALS = {
			new Vector3f(1.f, 0.f, 0.f),   // Normal for Vertex 1
			new Vector3f(1.f, 0.f, 0.f),   // Normal for Vertex 2
			new Vector3f(1.f, 0.f, 0.f),   // Normal for Vertex 3
			new Vector3f(1.f, 0.f, 0.f),   // Normal for Vertex 4
			new Vector3f(1.f, 0.f, 0.f),   // Normal for Vertex 5
			new Vector3f(1.f, 0.f, 0.f)    // Normal for Vertex 6
	};
	
	public static float[] vertices;

    static Vector2f[][] UVS;
    static Vector3f[][] VERTS;
    
    public static void setup() {
		UV_PX = setupUVPX();
		UV_NX = setupUVNX();
		UV_PY = setupUVPY();
		UV_NY = setupUVNY();
		UV_PZ = setupUVPX();
		UV_NZ = setupUVNX();
	}
	
	public static Vector2f[] setupUVPX() {
        int textureCount = 7; // Number of textures
        int texturesPerBlock = 6; // Number of UVs per block
        Vector2f[] uvArray = new Vector2f[textureCount * texturesPerBlock];
        
        addTextureFromXY(uvArray, Block.grass.getType(), 1, 0); //grass
        addTextureFromXY(uvArray, Block.dirt.getType(), 2, 0); //dirt
        addTextureFromXY(uvArray, Block.stone.getType(), 3, 0); //stone
        addTextureFromXY(uvArray, Block.treebark.getType(), 1, 1); //treebark
        addTextureFromXY(uvArray, Block.treeleaf.getType(), 0, 1); //treeleaf
        addTextureFromXY(uvArray, Block.cobble.getType(), 4, 0); //cobble
        addTextureFromXY(uvArray, Block.bedrock.getType(), 5, 0); //bedrock

        return uvArray;
    }	
	public static Vector2f[] setupUVNX() {
	    int textureCount = 7; // Number of textures
        int texturesPerBlock = 6; // Number of UVs per block
        Vector2f[] uvArray = new Vector2f[textureCount * texturesPerBlock];
        
        addTextureFromXY(uvArray, Block.grass.getType(), 1, 0); //grass
        addTextureFromXY(uvArray, Block.dirt.getType(), 2, 0); //dirt
        addTextureFromXY(uvArray, Block.stone.getType(), 3, 0); //stone
        addTextureFromXY(uvArray, Block.treebark.getType(), 1, 1); //treebark
        addTextureFromXY(uvArray, Block.treeleaf.getType(), 0, 1); //treeleaf
        addTextureFromXY(uvArray, Block.cobble.getType(), 4, 0); //cobble
        addTextureFromXY(uvArray, Block.bedrock.getType(), 5, 0); //bedrock

        return uvArray;
    }
	public static Vector2f[] setupUVPY() {
	    int textureCount = 7; // Number of textures
        int texturesPerBlock = 6; // Number of UVs per block
        Vector2f[] uvArray = new Vector2f[textureCount * texturesPerBlock];
        addTextureFromXY(uvArray, Block.grass.getType(), 0, 0); //grass
        addTextureFromXY(uvArray, Block.dirt.getType(), 2, 0); //dirt
        addTextureFromXY(uvArray, Block.stone.getType(), 3, 0); //stone
        addTextureFromXY(uvArray, Block.treebark.getType(), 2, 1); //treebark
        addTextureFromXY(uvArray, Block.treeleaf.getType(), 0, 1); //treeleaf
        addTextureFromXY(uvArray, Block.cobble.getType(), 4, 0); //cobble
        addTextureFromXY(uvArray, Block.bedrock.getType(), 5, 0); //bedrock

        return uvArray;
    }
	public static Vector2f[] setupUVNY() {
	    int textureCount = 7; // Number of textures
        int texturesPerBlock = 6; // Number of UVs per block
        Vector2f[] uvArray = new Vector2f[textureCount * texturesPerBlock];
        
        addTextureFromXY(uvArray, Block.grass.getType(), 2, 0); //grass
        addTextureFromXY(uvArray, Block.dirt.getType(), 2, 0); //dirt
        addTextureFromXY(uvArray, Block.stone.getType(), 3, 0); //stone
        addTextureFromXY(uvArray, Block.treebark.getType(), 2, 1); //treebark
        addTextureFromXY(uvArray, Block.treeleaf.getType(), 0, 1); //treeleaf
        addTextureFromXY(uvArray, Block.cobble.getType(), 4, 0); //cobble
        addTextureFromXY(uvArray, Block.bedrock.getType(), 5, 0); //bedrock

        return uvArray;
    }
	public static Vector2f[] setupUVPZ() {
        int textureCount = 7; // Number of textures
        int texturesPerBlock = 6; // Number of UVs per block
        Vector2f[] uvArray = new Vector2f[textureCount * texturesPerBlock];
        
        addTextureFromXY(uvArray, Block.grass.getType(), 1, 0); //grass
        addTextureFromXY(uvArray, Block.dirt.getType(), 2, 0); //dirt
        addTextureFromXY(uvArray, Block.stone.getType(), 3, 0); //stone
        addTextureFromXY(uvArray, Block.treebark.getType(), 1, 1); //treebark
        addTextureFromXY(uvArray, Block.treeleaf.getType(), 0, 1); //treeleaf
        addTextureFromXY(uvArray, Block.cobble.getType(), 4, 0); //cobble
        addTextureFromXY(uvArray, Block.bedrock.getType(), 5, 0); //bedrock


        return uvArray;
    }	
	public static Vector2f[] setupUVNZ() {
	    int textureCount = 7; // Number of textures
        int texturesPerBlock = 6; // Number of UVs per block
        Vector2f[] uvArray = new Vector2f[textureCount * texturesPerBlock];
        
        addTextureFromXY(uvArray, Block.grass.getType(), 1, 0); //grass
        addTextureFromXY(uvArray, Block.dirt.getType(), 2, 0); //dirt
        addTextureFromXY(uvArray, Block.stone.getType(), 3, 0); //stone
        addTextureFromXY(uvArray, Block.treebark.getType(), 1, 1); //treebark
        addTextureFromXY(uvArray, Block.treeleaf.getType(), 0, 1); //treeleaf
        addTextureFromXY(uvArray, Block.cobble.getType(), 4, 0); //cobble
        addTextureFromXY(uvArray, Block.bedrock.getType(), 5, 0); //bedrock

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
	
	public static int addTextureFromXY(Vector2f[] uv, int index, int x, int y) {
        float xOffset = (float) x / 16.f;
        float yOffset = (float) y / 16.f;
        int privIndex = new Integer(index*6);
        uv[privIndex++] = new Vector2f(xOffset, yOffset);
        uv[privIndex++] = new Vector2f(xOffset, (float) (y + 1) / 16.f);
        uv[privIndex++] = new Vector2f((float) (x + 1) / 16.f, (float) (y + 1) / 16.f);
        uv[privIndex++] = new Vector2f((float) (x + 1) / 16.f, (float) (y + 1) / 16.f);
        uv[privIndex++] = new Vector2f((float) (x + 1) / 16.f, yOffset);
        uv[privIndex++] = new Vector2f(xOffset, yOffset);

        return index;
    }
	
	public static RawModel getRawModel(Block block) {
		return Loader.getInstance().loadToVAO(vertices, getUVs(block.getByteType()));
	}
}