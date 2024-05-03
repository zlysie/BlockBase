package net.oikmo.engine.world.chunk;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.entity.Entity;
import net.oikmo.engine.world.World;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.main.Main;
import net.oikmo.toolbox.Maths;
import net.oikmo.toolbox.noise.OpenSimplexNoise;

public class MasterChunk {
	private Vector3f origin;
	private Chunk chunk;
	private ChunkMesh mesh;
	private Entity entity;
	public static final int maxTime = 60*(5);
	public int timer = maxTime;
	
	public MasterChunk(OpenSimplexNoise noiseGen, Vector3f origin) {
		this.origin = origin;
		this.chunk = new Chunk(noiseGen, origin);
	}
	
	public MasterChunk(Vector3f origin, byte[][][] blocks) {
		this.origin = origin;
		this.chunk = new Chunk(blocks);
	}
	
	public Block getBlock(Vector3f position) {
		Chunk chunk = getChunk();
		int localX = (int)(position.x + getOrigin().x)%16;
		int localY = (int) position.y;
		int localZ = (int)(position.z + getOrigin().z)%16;
		
		if(localX < 0) {
			localX = localX+16;
		}
		if(localZ < 0) {
			localZ = localZ+16;
		}
		
		if(Maths.isWithinChunk(localX, localY, localZ)) {
			Block block = Block.getBlockFromOrdinal(chunk.blocks[localX][localY][localZ]);
			return block;
		}
		return null;
	}
	
	public void setBlock(Vector3f position, Block block) {
		Chunk chunk = getChunk();
		//Main.theWorld.refreshChunk(this);
		int localX = (int)(position.x + getOrigin().x)%16;
		int localY = (int) position.y;
		int localZ = (int)(position.z + getOrigin().z)%16;
		
		if(localX < 0) {
			localX = localX+16;
		}
		if(localZ < 0) {
			localZ = localZ+16;
		}
		
		if (Maths.isWithinChunk(localX, localY, localZ)) {
			if (block != null) {
				if (chunk.blocks[localX][localY][localZ] == -1) {
					if(chunk.blocks[localX][localY][localZ] != block.getByteType()) {
						chunk.blocks[localX][localY][localZ] = block.getByteType();
						if(chunk.getHeightFromPosition(localX, localZ) < localY) {
							chunk.recalculateHeight(localX, localZ);
						}
						Main.theWorld.refreshChunk(this);
					}
				} else {
					return;
				}
			} else {
				if(chunk.blocks[localX][localY][localZ] != -1) {
					chunk.blocks[localX][localY][localZ] = -1;
				}
				Main.theWorld.refreshChunk(this);
			}
		}
	}
	
	private Vector2f position;
	public void setBlockFromTopLayer(int x, int z, Block block) {
		position = position == null ? new Vector2f(x,z) : position;
		if((int)position.x != x && (int)position.y != z) {
			position.x = x;
			position.y = z;
			int localX = (int) (x - getOrigin().x);
			int localZ = (int) (z - getOrigin().z);
			if(Maths.isWithinChunk(localX, localZ)) {
				for (int y = World.WORLD_HEIGHT - 1; y >= 0; y--) {
					try {
						if (getChunk().blocks[localX][y][localZ] != -1) {
							if(getChunk().blocks[localX][y][localZ] != block.getByteType()) {
								getChunk().blocks[localX][y - 0][localZ] = block.getByteType();
								Main.theWorld.refreshChunk(this);
								break;
							}
						}
					} catch(ArrayIndexOutOfBoundsException e) {}
				}
			}
		}
	}
	
	public void replaceBlocks(byte[][][] blocks) {
		this.chunk = new Chunk(blocks);
		this.mesh = new ChunkMesh(chunk);
		this.entity = null;
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
}
