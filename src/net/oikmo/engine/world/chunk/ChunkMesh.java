package net.oikmo.engine.world.chunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.entity.Entity;
import net.oikmo.engine.models.CubeModel;
import net.oikmo.engine.world.World;
import net.oikmo.engine.world.blocks.Block;

public class ChunkMesh {
	public List<Vertex> vertices;
	HashMap<Vector3f, Vertex> uniqueVertices = new HashMap<>();
	public float[] positions, uvs, normals;
	
	public Chunk chunk;
	public Entity entity;
	
	public ChunkMesh(Chunk chunk) {
		this.chunk = chunk;
		this.chunk.mesh = this;
		this.chunk.hasMesh = true;
		
		vertices = new ArrayList<Vertex>();
		
		buildMesh();
		populateLists();
	}
	
	private void buildMesh() {
		vertices.clear();
		uniqueVertices.clear();
		//loop thru each block in chunk and determine which faces are visible :3
		for (int x = 0; x < chunk.blocks.length; x++) {
		    for (int y = 0; y < World.WORLD_HEIGHT; y++) {
		        for (int z = 0; z < chunk.blocks[x][y].length; z++) {
		            Block blockI = chunk.blocks[x][y][z];
		            boolean px = false, nx = false, py = false, ny = false, pz = false, nz = false;
		            
		            if(blockI == null) { continue; }
		            
		            // Loop through the neighbouring blocks
		            for (int dx = -1; dx <= 1; dx++) {
		                for (int dy = -1; dy <= 1; dy++) {
		                    for (int dz = -1; dz <= 1; dz++) {
		                        // Skip the centre block (blockI)
		                        if (dx == 0 && dy == 0 && dz == 0) {
		                            continue;
		                        }
		                        
		                        // Calculate the neighbouring block's coordinates
		                        int neighborX = x + dx;
		                        int neighborY = y + dy;
		                        int neighborZ = z + dz;
		                        
		                        // Check if the neighbour is within the chunk bounds
		                        if (neighborX >= 0 && neighborX < Chunk.CHUNK_SIZE &&
		                            neighborY >= 0 && neighborY < World.WORLD_HEIGHT &&
		                            neighborZ >= 0 && neighborZ < Chunk.CHUNK_SIZE) {
		                            Block blockJ = chunk.blocks[neighborX][neighborY][neighborZ];
		                            
		                            if(blockJ == null) { continue; }
		                            
		                            //PX
		            				if(((x + 1) == (neighborX)) && ((y) == (neighborY)) && ((z) == (neighborZ))) {
		            					px = true;
		            				}
		            				//NX
		            				if(((x - 1) == (neighborX)) && ((y) == (neighborY)) && ((z) == (neighborZ))) {
		            					nx = true;
		            				}
		            				//PY
		            				if(((x) == (neighborX)) && ((y + 1) == (neighborY)) && ((z) == (neighborZ))) {
		            					py = true;
		            				}
		            				//NY
		            				if(((x) == (neighborX)) && ((y - 1) == (neighborY)) && ((z) == (neighborZ))) {
		            					ny = true;
		            				}
		            				//PZ
		            				if(((x) == (neighborX)) && ((y) == (neighborY)) && ((z + 1) == (neighborZ))) {
		            					pz = true;
		            				}
		            				//NZ
		            				if(((x) == (neighborX)) && ((y) == (neighborY)) && ((z - 1) == (neighborZ))) {
		            					nz = true;
		            				}
		                        }
		                    }
		                }
		            }
		          //Add visible face to the chunkMesh
					
		            if (!px) {
	                    addFaceVertices(uniqueVertices, vertices, blockI, x, y, z, CubeModel.PX_POS, CubeModel.UV_PX, CubeModel.NORMALS);
	                }

	                if (!nx) {
	                    addFaceVertices(uniqueVertices, vertices, blockI, x, y, z, CubeModel.NX_POS, CubeModel.UV_PX, CubeModel.NORMALS);
	                } 

	                if (!py) {
	                    addFaceVertices(uniqueVertices, vertices, blockI, x, y, z, CubeModel.PY_POS, CubeModel.UV_PY, CubeModel.NORMALS);
	                }

	                if (!ny) {
	                    addFaceVertices(uniqueVertices, vertices, blockI, x, y, z, CubeModel.NY_POS, CubeModel.UV_NY, CubeModel.NORMALS);
	                }

	                if (!pz) {
	                    addFaceVertices(uniqueVertices, vertices, blockI, x, y, z, CubeModel.PZ_POS, CubeModel.UV_PZ, CubeModel.NORMALS);
	                }

	                if (!nz) {
	                    addFaceVertices(uniqueVertices, vertices, blockI, x, y, z, CubeModel.NZ_POS, CubeModel.UV_PZ, CubeModel.NORMALS);
	                }
		        }	
		        
			}
		}
	}
	
	private void addFaceVertices(HashMap<Vector3f, Vertex> uniqueVertices, List<Vertex> vertices, Block block, int x, int y, int z, Vector3f[] positions, Vector2f[] uvs, Vector3f[] normals) {
	    int type = block.getType();
	    int startIndex = type * 6;

	    for (int k = 0; k < 6; k++) {
	        Vector3f position = new Vector3f(positions[k].x + x, positions[k].y + y, positions[k].z + z);
	        Vertex vertex = uniqueVertices.get(position);

	        if (vertex == null) {
	            vertex = new Vertex(position, normals[k], uvs[startIndex + k]);
	            uniqueVertices.put(position, vertex);
	            vertices.add(vertex);
	        }

	        //block.setFaceIndex(k, vertices.indexOf(vertex));
	    }
	}
	

	
	private void populateLists() {
		
		int numVertices = vertices.size();
	    positions = new float[numVertices * 3]; // Each vertex has 3 position components
	    uvs = new float[numVertices * 2]; // Each vertex has 2 uv components
	    normals = new float[numVertices * 3]; // Each vertex has 3 normal components

	    for(int i = 0; i < numVertices; i++) {
	        Vertex vertex = vertices.get(i);
	        int positionIndex = i * 3;
	        int uvIndex = i * 2;
	        int normalIndex = i * 3;

	        positions[positionIndex] = vertex.positions.x;
	        positions[positionIndex + 1] = vertex.positions.y;
	        positions[positionIndex + 2] = vertex.positions.z;

	        uvs[uvIndex] = vertex.uvs.x;
	        uvs[uvIndex + 1] = vertex.uvs.y;

	        normals[normalIndex] = vertex.normals.x;
	        normals[normalIndex + 1] = vertex.normals.y;
	        normals[normalIndex + 2] = vertex.normals.z;
	    }
		
	}
} 