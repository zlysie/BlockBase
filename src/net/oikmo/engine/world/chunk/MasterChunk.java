package net.oikmo.engine.world.chunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.entity.Entity;
import prime.PerlinNoise;

public class MasterChunk {
	private static Map<Vector3f, MasterChunk> chunkMap = new HashMap<>();
	public static List<Vector3f> usedPositions = new ArrayList<>();
	private int index;
	
	private Vector3f origin;
	private Chunk chunk;
	private ChunkMesh mesh;
	private Entity entity;
	
	public MasterChunk(PerlinNoise noiseGen, Vector3f origin) {
		setIndex(usedPositions.size());
		this.origin = origin;
		this.chunk = new Chunk(noiseGen, origin);
		this.mesh = new ChunkMesh(this.chunk);
		MasterChunk.usedPositions.add(origin);
		MasterChunk.chunkMap.put(this.origin, this);
	}
	
	public static MasterChunk getChunkFromPosition(Vector3f position) {
		return chunkMap.get(getPosition(position));
	}
	
	public static boolean isPositionUsed(Vector3f pos) {
		boolean result = false;
		synchronized(usedPositions) {
			result = usedPositions.contains(new Vector3f(pos.x, 0, pos.z));
		}
		return result;
	}
	
	public static Vector3f getPosition(Vector3f pos) {
		Vector3f result = null;
		synchronized(usedPositions) {
			for(int i = 0; i < usedPositions.size(); i++) {
				if(usedPositions.get(i).x == pos.x && usedPositions.get(i).z == pos.z) {
					result = usedPositions.get(i);
				}
			}
		}
		return result;
	}
	
	public Vector3f getOrigin() {
		return origin;
	}
	
	public Chunk getChunk() {
		return chunk;
	}
	
	public ChunkMesh getMesh() {
		return mesh;
	}
	
	public void destroyMesh() {
		this.mesh = null;
	}
	
	public void createMesh() {
		this.mesh = new ChunkMesh(this.chunk);
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public int getIndex() {
		return index;
	}

	private void setIndex(int index) {
		this.index = index;
	}
}
