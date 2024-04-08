package net.oikmo.engine.world.chunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.entity.Entity;
import net.oikmo.engine.world.World;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.main.Main;
import net.oikmo.main.gui.GuiInGame;
import net.oikmo.toolbox.FastMath;
import net.oikmo.toolbox.Maths;
import net.oikmo.toolbox.noise.OpenSimplexNoise;

public class MasterChunk {
	public static Map<Vector3f, MasterChunk> chunkMap = new HashMap<>();
	public static List<Vector3f> usedPositions = new ArrayList<>();
	private int index;
	
	private Vector3f origin;
	private Chunk chunk;
	private ChunkMesh mesh;
	private Entity entity;
	
	public MasterChunk(OpenSimplexNoise noiseGen, Vector3f origin) {
		setIndex(usedPositions.size());
		this.origin = origin;
		this.chunk = new Chunk(noiseGen, origin);
		this.mesh = new ChunkMesh(this.chunk);
		MasterChunk.usedPositions.add(origin);
		MasterChunk.chunkMap.put(this.origin, this);
	}
	
	public MasterChunk(Vector3f origin, byte[][][] blocks) {
		setIndex(usedPositions.size());
		this.origin = origin;
		this.chunk = new Chunk(blocks);
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
				if(usedPositions.get(i) != null) {
					if((int)usedPositions.get(i).x == (int)pos.x && (int)usedPositions.get(i).z == (int)pos.z) {
						result = usedPositions.get(i);
					}
				}
				
			}
		}
		return result;
	}
	public boolean blockHasNeighbours(Vector3f position) {
		int localX = (int) (position.x - getOrigin().x);
		int localY = (int) position.y;
		int localZ = (int) (position.z - getOrigin().z);
		
		for(int dx = -1; dx <= 1; dx++) {
			for(int dy = -1; dy <= 1; dy++) {
				for(int dz= -1; dz <= 1; dz++) {
					
					int x = localX + dx;
					int y = localY + dy;
					int z = localZ + dz;
					if(x < 0) { continue; }
					if(y < 0 || y > World.WORLD_HEIGHT-1) { continue; }
					if(z < 0) { continue; }
					
					if(x > Chunk.CHUNK_SIZE-1) {
						x = Chunk.CHUNK_SIZE-1;
					}
					if(z > Chunk.CHUNK_SIZE-1) {
						z = Chunk.CHUNK_SIZE-1;
					}
					
					if(x == 16 || z == 16) {
						return true;
					}
					
					if(chunk.blocks[x][y][z] != -1) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	
	int prevX, prevY, prevZ;
	public Block getBlock(Vector3f position) {
		Chunk chunk = getChunk();
		int localX = Maths.roundFloat((Maths.roundFloat(position.x - getOrigin().x)));
		int localY = (int) position.y;
		int localZ = Maths.roundFloat((Maths.roundFloat(position.z - getOrigin().z)));
		
		int ceilX = (int) FastMath.ceil(position.x/16);
		if((int)position.x/16 == ceilX && ceilX != 0) {
			//System.out.println("x: " + ceilX);
			if(position.x < 0) {
				localX = localX;
			}
		}
		
		int ceilZ = (int) FastMath.ceil(position.z/16);
		if((int)position.z/16 == ceilZ && ceilZ != 0) {
			//System.out.println("z: " + ceilZ);
			if(position.z < 0) {
				localZ = localZ;
			}
		}
		
		if(Main.currentScreen instanceof GuiInGame) {
			((GuiInGame)Main.currentScreen).updatechunkpos(localX, localZ);
		}
		
		
		if(Maths.isWithinChunk(localX, localY, localZ)) {
			Block block = Block.getBlockFromOrdinal(chunk.blocks[localX][localY][localZ]);
			return block;
		}
		return null;
	}
	
	public void setBlock(Vector3f position, Block block) {
		Chunk chunk = getChunk();
		int localX = Maths.roundFloat((Maths.roundFloat(position.x - getOrigin().x)));
		int localY = (int) position.y;
		int localZ = Maths.roundFloat((Maths.roundFloat(position.z - getOrigin().z)));
		
		int ceilX = (int) FastMath.ceil(position.x/16);
		if((int)position.x/16 == ceilX && ceilX != 0) {
			System.out.println(localX + " x: " + ceilX + " " + getOrigin());
		}
		
		int ceilZ = (int) FastMath.ceil(position.z/16);
		if((int)position.z/16 == ceilZ && ceilZ != 0) {
			System.out.println(localZ + " z: " + ceilZ + " " + getOrigin());
		}
		
		
		if(Main.currentScreen instanceof GuiInGame) {
			((GuiInGame)Main.currentScreen).updatechunkpos(localX, localZ);
		}
		
		//System.out.println(localX + " " + localY + " " + localZ);
		
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
					Main.theWorld.refreshChunk(this);
				}
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

	public static void clear() {
		chunkMap.clear();
		usedPositions.clear();
	}
}
