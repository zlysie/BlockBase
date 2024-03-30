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
	
	public static Vector2f[] UV_PX = {

			// GRASS
			new Vector2f(1.f / 16.f, 0.f),
			new Vector2f(1.f / 16.f, 1.f / 16.f),
			new Vector2f(2.f / 16.f, 1.f / 16.f),
			new Vector2f(2.f / 16.f, 1.f / 16.f),
			new Vector2f(2.f / 16.f, 0.f / 16.f),
			new Vector2f(1.f / 16.f, 0.f),

			// DIRT
			new Vector2f(2f / 16.f, 0.f),
			new Vector2f(2f / 16.f, 1.f / 16.f),
			new Vector2f(3f / 16.f, 1.f / 16.f),
			new Vector2f(3f / 16.f, 1.f / 16.f),
			new Vector2f(3f / 16.f, 0.f / 16.f),
			new Vector2f(2f / 16.f, 0.f),

			// STONE
			new Vector2f(3.f / 16.f, 0.f),
			new Vector2f(3.f / 16.f, 1.f / 16.f),
			new Vector2f(4.f / 16.f, 1.f / 16.f),
			new Vector2f(4.f / 16.f, 1.f / 16.f),
			new Vector2f(4.f / 16.f, 0.f / 16.f),
			new Vector2f(3.f / 16.f, 0.f),

			// TREEBARK
			new Vector2f(4.f / 16.f, 0.f),
			new Vector2f(4.f / 16.f, 1.f / 16.f),
			new Vector2f(5.f / 16.f, 1.f / 16.f),
			new Vector2f(5.f / 16.f, 1.f / 16.f),
			new Vector2f(5.f / 16.f, 0.f / 16.f),
			new Vector2f(4.f / 16.f, 0.f),

			// TREELEAF
			new Vector2f(6.f / 16.f, 0.f),
			new Vector2f(6.f / 16.f, 1.f / 16.f),
			new Vector2f(7.f / 16.f, 1.f / 16.f),
			new Vector2f(7.f / 16.f, 1.f / 16.f),
			new Vector2f(7.f / 16.f, 0.f / 16.f),
			new Vector2f(6.f / 16.f, 0.f),

			// COBBLESTONE
			new Vector2f(7.f / 16.f, 0.f),
			new Vector2f(7.f / 16.f, 1.f / 16.f),
			new Vector2f(8.f / 16.f, 1.f / 16.f),
			new Vector2f(8.f / 16.f, 1.f / 16.f),
			new Vector2f(8.f / 16.f, 0.f / 16.f),
			new Vector2f(7.f / 16.f, 0.f),

			// BEDROCK
			new Vector2f(8.f / 16.f, 0.f),
			new Vector2f(8.f / 16.f, 1.f / 16.f),
			new Vector2f(9.f / 16.f, 1.f / 16.f),
			new Vector2f(9.f / 16.f, 1.f / 16.f),
			new Vector2f(9.f / 16.f, 0.f / 16.f),
			new Vector2f(8.f / 16.f, 0.f)

	};	
	public static Vector2f[] UV_NX = {

			// GRASS
			new Vector2f(1.f / 16.f, 0.f),
			new Vector2f(1.f / 16.f, 1.f / 16.f),
			new Vector2f(2.f / 16.f, 1.f / 16.f),
			new Vector2f(2.f / 16.f, 1.f / 16.f),
			new Vector2f(2.f / 16.f, 0.f / 16.f),
			new Vector2f(1.f / 16.f, 0.f),

			// DIRT
			new Vector2f(2.f / 16.f, 0.f),
			new Vector2f(2.f / 16.f, 1.f / 16.f),
			new Vector2f(3.f / 16.f, 1.f / 16.f),
			new Vector2f(3.f / 16.f, 1.f / 16.f),
			new Vector2f(3.f / 16.f, 0.f / 16.f),
			new Vector2f(2.f / 16.f, 0.f),

			// STONE
			new Vector2f(3.f / 16.f, 0.f),
			new Vector2f(3.f / 16.f, 1.f / 16.f),
			new Vector2f(4.f / 16.f, 1.f / 16.f),
			new Vector2f(4.f / 16.f, 1.f / 16.f),
			new Vector2f(4.f / 16.f, 0.f / 16.f),
			new Vector2f(3.f / 16.f, 0.f),

			// TREEBARK
			new Vector2f(4.f / 16.f, 0.f),
			new Vector2f(4.f / 16.f, 1.f / 16.f),
			new Vector2f(5.f / 16.f, 1.f / 16.f),
			new Vector2f(5.f / 16.f, 1.f / 16.f),
			new Vector2f(5.f / 16.f, 0.f / 16.f),
			new Vector2f(4.f / 16.f, 0.f),

			// TREELEAF
			new Vector2f(6.f / 16.f, 0.f),
			new Vector2f(6.f / 16.f, 1.f / 16.f),
			new Vector2f(7.f / 16.f, 1.f / 16.f),
			new Vector2f(7.f / 16.f, 1.f / 16.f),
			new Vector2f(7.f / 16.f, 0.f / 16.f),
			new Vector2f(6.f / 16.f, 0.f),

			// COBBLESTONE
			new Vector2f(7.f / 16.f, 0.f),
			new Vector2f(7.f / 16.f, 1.f / 16.f),
			new Vector2f(8.f / 16.f, 1.f / 16.f),
			new Vector2f(8.f / 16.f, 1.f / 16.f),
			new Vector2f(8.f / 16.f, 0.f / 16.f),
			new Vector2f(7.f / 16.f, 0.f),

			// BEDROCK
			new Vector2f(8.f / 16.f, 0.f),
			new Vector2f(8.f / 16.f, 1.f / 16.f),
			new Vector2f(9.f / 16.f, 1.f / 16.f),
			new Vector2f(9.f / 16.f, 1.f / 16.f),
			new Vector2f(9.f / 16.f, 0.f / 16.f),
			new Vector2f(8.f / 16.f, 0.f)
	};
	public static Vector2f[] UV_PY = {

			// GRASS
			new Vector2f(0.f, 0.f),
			new Vector2f(0.f, 1.f / 16.f),
			new Vector2f(1.f / 16.f, 1.f / 16.f),
			new Vector2f(1.f / 16.f, 1.f / 16.f),
			new Vector2f(1.f / 16.f, 0.f),
			new Vector2f(0.f, 0.f),

			// DIRT
			new Vector2f(2.f / 16.f, 0.f),
			new Vector2f(2.f / 16.f, 1.f / 16.f),
			new Vector2f(3.f / 16.f, 1.f / 16.f),
			new Vector2f(3.f / 16.f, 1.f / 16.f),
			new Vector2f(3.f / 16.f, 0.f / 16.f),
			new Vector2f(2.f / 16.f, 0.f),

			// STONE
			new Vector2f(3.f / 16.f, 0.f),
			new Vector2f(3.f / 16.f, 1.f / 16.f),
			new Vector2f(4.f / 16.f, 1.f / 16.f),
			new Vector2f(4.f / 16.f, 1.f / 16.f),
			new Vector2f(4.f / 16.f, 0.f / 16.f),
			new Vector2f(3.f / 16.f, 0.f),

			// TREEBARK
			new Vector2f(5.f / 16.f, 0.f),
			new Vector2f(5.f / 16.f, 1.f / 16.f),
			new Vector2f(6.f / 16.f, 1.f / 16.f),
			new Vector2f(6.f / 16.f, 1.f / 16.f),
			new Vector2f(6.f / 16.f, 0.f / 16.f),
			new Vector2f(5.f / 16.f, 0.f),

			// TREELEAF
			new Vector2f(6.f / 16.f, 0.f),
			new Vector2f(6.f / 16.f, 1.f / 16.f),
			new Vector2f(7.f / 16.f, 1.f / 16.f),
			new Vector2f(7.f / 16.f, 1.f / 16.f),
			new Vector2f(7.f / 16.f, 0.f / 16.f),
			new Vector2f(6.f / 16.f, 0.f),

			// COBBLESTONE
			new Vector2f(7.f / 16.f, 0.f),
			new Vector2f(7.f / 16.f, 1.f / 16.f),
			new Vector2f(8.f / 16.f, 1.f / 16.f),
			new Vector2f(8.f / 16.f, 1.f / 16.f),
			new Vector2f(8.f / 16.f, 0.f / 16.f),
			new Vector2f(7.f / 16.f, 0.f),

			// BEDROCK
			new Vector2f(8.f / 16.f, 0.f),
			new Vector2f(8.f / 16.f, 1.f / 16.f),
			new Vector2f(9.f / 16.f, 1.f / 16.f),
			new Vector2f(9.f / 16.f, 1.f / 16.f),
			new Vector2f(9.f / 16.f, 0.f / 16.f),
			new Vector2f(8.f / 16.f, 0.f)
	};
	public static Vector2f[] UV_NY = {

			// GRASS
			new Vector2f(2.f / 16.f, 0.f),
			new Vector2f(2.f / 16.f, 1.f / 16.f),
			new Vector2f(3.f / 16.f, 1.f / 16.f),
			new Vector2f(3.f / 16.f, 1.f / 16.f),
			new Vector2f(3.f / 16.f, 0.f / 16.f),
			new Vector2f(2.f / 16.f, 0.f),

			// DIRT
			new Vector2f(2.f / 16.f, 0.f),
			new Vector2f(2.f / 16.f, 1.f / 16.f),
			new Vector2f(3.f / 16.f, 1.f / 16.f),
			new Vector2f(3.f / 16.f, 1.f / 16.f),
			new Vector2f(3.f / 16.f, 0.f / 16.f),
			new Vector2f(2.f / 16.f, 0.f),

			// STONE
			new Vector2f(3.f / 16.f, 0.f),
			new Vector2f(3.f / 16.f, 1.f / 16.f),
			new Vector2f(4.f / 16.f, 1.f / 16.f),
			new Vector2f(4.f / 16.f, 1.f / 16.f),
			new Vector2f(4.f / 16.f, 0.f / 16.f),
			new Vector2f(3.f / 16.f, 0.f),

			// TREEBARK
			new Vector2f(5.f / 16.f, 0.f),
			new Vector2f(5.f / 16.f, 1.f / 16.f),
			new Vector2f(6.f / 16.f, 1.f / 16.f),
			new Vector2f(6.f / 16.f, 1.f / 16.f),
			new Vector2f(6.f / 16.f, 0.f / 16.f),
			new Vector2f(5.f / 16.f, 0.f),

			// TREELEAF
			new Vector2f(6.f / 16.f, 0.f),
			new Vector2f(6.f / 16.f, 1.f / 16.f),
			new Vector2f(7.f / 16.f, 1.f / 16.f),
			new Vector2f(7.f / 16.f, 1.f / 16.f),
			new Vector2f(7.f / 16.f, 0.f / 16.f),
			new Vector2f(6.f / 16.f, 0.f),

			// COBBLESTONE
			new Vector2f(7.f / 16.f, 0.f),
			new Vector2f(7.f / 16.f, 1.f / 16.f),
			new Vector2f(8.f / 16.f, 1.f / 16.f),
			new Vector2f(8.f / 16.f, 1.f / 16.f),
			new Vector2f(8.f / 16.f, 0.f / 16.f),
			new Vector2f(7.f / 16.f, 0.f),

			// BEDROCK
			new Vector2f(8.f / 16.f, 0.f),
			new Vector2f(8.f / 16.f, 1.f / 16.f),
			new Vector2f(9.f / 16.f, 1.f / 16.f),
			new Vector2f(9.f / 16.f, 1.f / 16.f),
			new Vector2f(9.f / 16.f, 0.f / 16.f),
			new Vector2f(8.f / 16.f, 0.f)
	};	
	public static Vector2f[] UV_PZ = {

			// GRASS
			new Vector2f(1.f / 16.f, 0.f),
			new Vector2f(1.f / 16.f, 1.f / 16.f),
			new Vector2f(2.f / 16.f, 1.f / 16.f),
			new Vector2f(2.f / 16.f, 1.f / 16.f),
			new Vector2f(2.f / 16.f, 0.f / 16.f),
			new Vector2f(1.f / 16.f, 0.f),

			// DIRT
			new Vector2f(2.f / 16.f, 0.f),
			new Vector2f(2.f / 16.f, 1.f / 16.f),
			new Vector2f(3.f / 16.f, 1.f / 16.f),
			new Vector2f(3.f / 16.f, 1.f / 16.f),
			new Vector2f(3.f / 16.f, 0.f / 16.f),
			new Vector2f(2.f / 16.f, 0.f),

			// STONE
			new Vector2f(3.f / 16.f, 0.f),
			new Vector2f(3.f / 16.f, 1.f / 16.f),
			new Vector2f(4.f / 16.f, 1.f / 16.f),
			new Vector2f(4.f / 16.f, 1.f / 16.f),
			new Vector2f(4.f / 16.f, 0.f / 16.f),
			new Vector2f(3.f / 16.f, 0.f),

			// TREEBARK
			new Vector2f(4.f / 16.f, 0.f),
			new Vector2f(4.f / 16.f, 1.f / 16.f),
			new Vector2f(5.f / 16.f, 1.f / 16.f),
			new Vector2f(5.f / 16.f, 1.f / 16.f),
			new Vector2f(5.f / 16.f, 0.f / 16.f),
			new Vector2f(4.f / 16.f, 0.f),

			// TREELEAF
			new Vector2f(6.f / 16.f, 0.f),
			new Vector2f(6.f / 16.f, 1.f / 16.f),
			new Vector2f(7.f / 16.f, 1.f / 16.f),
			new Vector2f(7.f / 16.f, 1.f / 16.f),
			new Vector2f(7.f / 16.f, 0.f / 16.f),
			new Vector2f(6.f / 16.f, 0.f),

			// COBBLESTONE
			new Vector2f(7.f / 16.f, 0.f),
			new Vector2f(7.f / 16.f, 1.f / 16.f),
			new Vector2f(8.f / 16.f, 1.f / 16.f),
			new Vector2f(8.f / 16.f, 1.f / 16.f),
			new Vector2f(8.f / 16.f, 0.f / 16.f),
			new Vector2f(7.f / 16.f, 0.f),

			// BEDROCK
			new Vector2f(8.f / 16.f, 0.f),
			new Vector2f(8.f / 16.f, 1.f / 16.f),
			new Vector2f(9.f / 16.f, 1.f / 16.f),
			new Vector2f(9.f / 16.f, 1.f / 16.f),
			new Vector2f(9.f / 16.f, 0.f / 16.f),
			new Vector2f(8.f / 16.f, 0.f)
	};
	public static Vector2f[] UV_NZ = {

			// GRASS
			new Vector2f(1.f / 16.f, 0.f),
			new Vector2f(1.f / 16.f, 1.f / 16.f),
			new Vector2f(2.f / 16.f, 1.f / 16.f),
			new Vector2f(2.f / 16.f, 1.f / 16.f),
			new Vector2f(2.f / 16.f, 0.f / 16.f),
			new Vector2f(1.f / 16.f, 0.f),

			// DIRT
			new Vector2f(2.f / 16.f, 0.f),
			new Vector2f(2.f / 16.f, 1.f / 16.f),
			new Vector2f(3.f / 16.f, 1.f / 16.f),
			new Vector2f(3.f / 16.f, 1.f / 16.f),
			new Vector2f(3.f / 16.f, 0.f / 16.f),
			new Vector2f(2.f / 16.f, 0.f),

			// STONE
			new Vector2f(3.f / 16.f, 0.f),
			new Vector2f(3.f / 16.f, 1.f / 16.f),
			new Vector2f(4.f / 16.f, 1.f / 16.f),
			new Vector2f(4.f / 16.f, 1.f / 16.f),
			new Vector2f(4.f / 16.f, 0.f / 16.f),
			new Vector2f(3.f / 16.f, 0.f),

			// TREEBARK
			new Vector2f(4.f / 16.f, 0.f),
			new Vector2f(4.f / 16.f, 1.f / 16.f),
			new Vector2f(5.f / 16.f, 1.f / 16.f),
			new Vector2f(5.f / 16.f, 1.f / 16.f),
			new Vector2f(5.f / 16.f, 0.f / 16.f),
			new Vector2f(4.f / 16.f, 0.f),

			// TREELEAF
			new Vector2f(6.f / 16.f, 0.f),
			new Vector2f(6.f / 16.f, 1.f / 16.f),
			new Vector2f(7.f / 16.f, 1.f / 16.f),
			new Vector2f(7.f / 16.f, 1.f / 16.f),
			new Vector2f(7.f / 16.f, 0.f / 16.f),
			new Vector2f(6.f / 16.f, 0.f),

			// COBBLESTONE
			new Vector2f(7.f / 16.f, 0.f),
			new Vector2f(7.f / 16.f, 1.f / 16.f),
			new Vector2f(8.f / 16.f, 1.f / 16.f),
			new Vector2f(8.f / 16.f, 1.f / 16.f),
			new Vector2f(8.f / 16.f, 0.f / 16.f),
			new Vector2f(7.f / 16.f, 0.f),

			// BEDROCK
			new Vector2f(8.f / 16.f, 0.f),
			new Vector2f(8.f / 16.f, 1.f / 16.f),
			new Vector2f(9.f / 16.f, 1.f / 16.f),
			new Vector2f(9.f / 16.f, 1.f / 16.f),
			new Vector2f(9.f / 16.f, 0.f / 16.f),
			new Vector2f(8.f / 16.f, 0.f)
	};

	public static Vector3f[] NORMALS = {
			new Vector3f(1.f, 0.f, 0.f),   // Normal for Vertex 1
			new Vector3f(1.f, 0.f, 0.f),   // Normal for Vertex 2
			new Vector3f(1.f, 0.f, 0.f),   // Normal for Vertex 3
			new Vector3f(1.f, 0.f, 0.f),   // Normal for Vertex 4
			new Vector3f(1.f, 0.f, 0.f),   // Normal for Vertex 5
			new Vector3f(1.f, 0.f, 0.f)    // Normal for Vertex 6
	};
	
	public static float[] vertices = {
            -0.5f,0.5f,-0.5f,
            -0.5f,-0.5f,-0.5f,
            0.5f,-0.5f,-0.5f,
            0.5f,0.5f,-0.5f,

            -0.5f,0.5f,0.5f,
            -0.5f,-0.5f,0.5f,
            0.5f,-0.5f,0.5f,
            0.5f,0.5f,0.5f,

            0.5f,0.5f,-0.5f,
            0.5f,-0.5f,-0.5f,
            0.5f,-0.5f,0.5f,
            0.5f,0.5f,0.5f,

            -0.5f,0.5f,-0.5f,
            -0.5f,-0.5f,-0.5f,
            -0.5f,-0.5f,0.5f,
            -0.5f,0.5f,0.5f,

            -0.5f,0.5f,0.5f,
            -0.5f,0.5f,-0.5f,
            0.5f,0.5f,-0.5f,
            0.5f,0.5f,0.5f,

            -0.5f,-0.5f,0.5f,
            -0.5f,-0.5f,-0.5f,
            0.5f,-0.5f,-0.5f,
            0.5f,-0.5f,0.5f

    };

    public static float[] uv = {

            0,0,
            0,1,
            1,1,
            1,0,
            0,0,
            
            0,1,
            1,1,
            1,0,
            0,0,
            0,1,
            
            1,1,
            1,0,
            0,0,
            0,1,
            1,1,
            
            1,0,
            0,0,
            0,1,
            1,1,
            
            1,0,
            0,0,
            0,1,
            1,1,
            1,0
    };

    public static int[] indices = {
            0,1,3,
            3,1,2,
            4,5,7,
            7,5,6,
            8,9,11,
            11,9,10,
            12,13,15,
            15,13,14,
            16,17,19,
            19,17,18,
            20,21,23,
            23,21,22
	};
    

    static Vector2f[][] UVS;
    static Vector3f[][] VERTS;
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
	
	public static RawModel getRawModel(Block block) {
		return Loader.getInstance().loadToVAO(vertices, getUVs(block.getType()));
	}
}