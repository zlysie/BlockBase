package net.oikmo.engine.world.chunk;

import java.util.Random;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.mojang.minecraft.particle.Particle;

import net.oikmo.engine.entity.Entity;
import net.oikmo.engine.entity.PrimedTNT;
import net.oikmo.engine.sound.SoundMaster;
import net.oikmo.engine.world.World;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.main.Main;
import net.oikmo.network.shared.PacketPlaySoundAt;
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
	
	public void setBlockNoUpdate(Vector3f position, Block block) {
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
					}
				}
			} else {
				if(chunk.blocks[localX][localY][localZ] != -1 && chunk.blocks[localX][localY][localZ] != Block.bedrock.getByteType()) {
					chunk.blocks[localX][localY][localZ] = -1;
				}
			}
		}
	}
	
	/**
	 * Sets block and refreshes this chunk if operation was successful.
	 * If the block broken was not the owner then create a PrimedTNT entity.
	 * 
	 * @param position
	 * @param block
	 * @param owner
	 */
	public void setBlock(Vector3f position, Block block, boolean owner) {
		Chunk chunk = getChunk();
		int x = (int)position.x;
		int y = (int)position.y;
		int z = (int)position.z;
		int localX = (int)(position.x + getOrigin().x)%16;
		int localY = (int) position.y;
		int localZ = (int)(position.z + getOrigin().z)%16;
		
		if(localX < 0) {
			localX = localX+16;
		}
		if(localZ < 0) {
			localZ = localZ+16;
		}
		
		if(localY == -1) {
			Main.theWorld.refreshChunk(this);
		}
		
		
		if (Maths.isWithinChunk(localX, localY, localZ)) {
			if (block != null) {
				if (chunk.blocks[localX][localY][localZ] == -1 ) {
					if(chunk.blocks[localX][localY][localZ] != block.getByteType()) {
						chunk.blocks[localX][localY][localZ] = block.getByteType();
						if(chunk.getHeightFromPosition(localX, localZ) < localY) {
							chunk.recalculateHeight(localX, localZ);
						}
						
						SoundMaster.playBlockPlaceSFX(block, x, y, z);
						if(Main.theNetwork != null) {
							PacketPlaySoundAt packet = new PacketPlaySoundAt();
							packet.place = true;
							packet.blockID = block.getByteType();
							packet.x = x;
							packet.y = y;
							packet.z = z;
							Main.theNetwork.client.sendTCP(packet);
						}
						getChunk().calcLightDepths(localX, localZ, 1, 1);
						Main.theWorld.refreshChunk(this);
						
					}
				} else {
					return;
				}
			} else {
				if(chunk.blocks[localX][localY][localZ] != Block.bedrock.getByteType()) {
					Block whatUsedToBeThere = Block.getBlockFromOrdinal(chunk.blocks[localX][localY][localZ]);
					if(chunk.blocks[localX][localY][localZ] != -1) {
						chunk.blocks[localX][localY][localZ] = -1;
					}
					SoundMaster.playBlockBreakSFX(whatUsedToBeThere, x,y,z);
					if(Main.theNetwork != null) {
						PacketPlaySoundAt packet = new PacketPlaySoundAt();
						packet.blockID = whatUsedToBeThere.getByteType();
						packet.x = x;
						packet.y = y;
						packet.z = z;
						Main.theNetwork.client.sendTCP(packet);
					}
					for(int px = 0; px < 4; ++px) {
						for(int py = 0; py < 4; ++py) {
							for(int pz = 0; pz < 4; ++pz) {
								float particleX = (float)x + ((float)px) / (float)4;
								float particleY = (float)y + ((float)py) / (float)4;
								float particleZ = (float)z + ((float)pz) / (float)4;
								Particle particle = new Particle(particleX+0.125f, particleY+0.125f, particleZ+0.125f, particleX - (float)x, particleY - (float)y, particleZ - (float)z, whatUsedToBeThere);
								Main.theWorld.spawnParticle(particle);
							}
						}
					}
					if(Main.theNetwork != null && !owner) {
						if(whatUsedToBeThere.getByteType() == Block.tnt.getType()) {
							Main.theWorld.addEntity(new PrimedTNT(new Vector3f(x,y,z), new Random().nextInt(10)/10f, 0.1f, new Random().nextInt(10)/10f, false));
						}
					}
					
					
					getChunk().calcLightDepths(localX, localZ, 1, 1);
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
