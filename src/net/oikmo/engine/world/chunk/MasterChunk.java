package net.oikmo.engine.world.chunk;

import java.util.Random;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.mojang.minecraft.particle.Particle;

import net.oikmo.engine.entity.PrimedTNT;
import net.oikmo.engine.network.packet.PacketPlaySoundAt;
import net.oikmo.engine.renderers.chunk.ChunkEntity;
import net.oikmo.engine.sound.SoundMaster;
import net.oikmo.engine.world.World;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.engine.world.chunk.coordinate.ChunkCoordinates;
import net.oikmo.main.Main;
import net.oikmo.toolbox.Maths;
import net.oikmo.toolbox.noise.OpenSimplexNoise;

public class MasterChunk {
	private ChunkCoordinates origin;
	private Chunk chunk;
	private ChunkMesh mesh;
	private ChunkEntity entity;
	public static final int networkMaxTime = 60*(5);
	public static final int localMaxTime = 60*(30);
	public int timer = networkMaxTime;
	
	public boolean dirty = true;
	
	public MasterChunk(OpenSimplexNoise noiseGen, ChunkCoordinates origin) {
		this.origin = origin;
		this.chunk = new Chunk(noiseGen, origin);
	}
	
	public MasterChunk(ChunkCoordinates origin, byte[] blocks) {
		this.origin = origin;
		this.chunk = new Chunk(blocks, origin);
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
			Block block = Block.getBlockFromOrdinal(chunk.getBlock(localX, localY, localZ));
			return block;
		}
		return null;
	}
	
	public void setBlockNoUpdate(Vector3f position, Block block) {
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
		
		if (Maths.isWithinChunk(localX, localY, localZ)) {
			if (block != null) {
				if (chunk.getBlock(localX, localY, localZ) == Block.Type.AIR.ordinal()) {
					if(chunk.getBlock(localX, localY, localZ) != block.getByteType()) {
						chunk.setBlock(localX, localY, localZ, block.getByteType());
						dirty = true;
					}
				}
			} else {
				if(chunk.getBlock(localX, localY, localZ) != Block.Type.AIR.ordinal() && localY != 0) {
					byte blockID = chunk.getBlock(localX, localY, localZ);
					if(Main.theNetwork == null) {
						if(blockID == Block.tnt.getType()) {
							Main.theWorld.addEntity(new PrimedTNT(new Vector3f(x, y, z), new Random().nextInt(10)/10f, 0.1f, new Random().nextInt(10)/10f, true));
						}
					}
					
					chunk.setBlock(localX, localY, localZ, Block.Type.AIR.ordinal());
					dirty = true;
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
		
		if (Maths.isWithinChunk(localX, localY, localZ)) {
			if (block != null) {
				if(chunk.getBlock(localX, localY, localZ) == Block.Type.AIR.ordinal()) {
					if(chunk.getBlock(localX, localY, localZ) != block.getByteType()) {
						chunk.setBlock(localX, localY, localZ, block.getByteType());
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
						dirty = true;
						
					}
				} else {
					return;
				}
			} else {
				if(localY != 0) {
					byte blockID = chunk.getBlock(localX, localY, localZ);
					if(chunk.getBlock(localX, localY, localZ) != Block.Type.AIR.ordinal()) {
						chunk.setBlock(localX, localY, localZ, Block.Type.AIR.ordinal());
					}
					if(blockID != -1) {
						SoundMaster.playBlockBreakSFX(Block.getBlockFromOrdinal(blockID), x,y,z);
						
						for(int px = 0; px < 4; ++px) {
							for(int py = 0; py < 4; ++py) {
								for(int pz = 0; pz < 4; ++pz) {
									float particleX = (float)x + ((float)px) / (float)4;
									float particleY = (float)y + ((float)py) / (float)4;
									float particleZ = (float)z + ((float)pz) / (float)4;
									Particle particle = new Particle(particleX+0.125f, particleY+0.125f, particleZ+0.125f, particleX - (float)x, particleY - (float)y, particleZ - (float)z, Block.getBlockFromOrdinal(blockID));
									Main.theWorld.spawnParticle(particle);
								}
							}
						}
					}
					if(Main.theNetwork != null) {
						if(blockID == Block.tnt.getType()) {
							Main.theWorld.addEntity(new PrimedTNT(new Vector3f(x, y, z), new Random().nextInt(10)/10f, 0.1f, new Random().nextInt(10)/10f, true));
						}
					}
					
					getChunk().calcLightDepths(localX, localZ, 1, 1);
					dirty = true;
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
						if(getChunk().getBlock(localX, y, localZ) != -1) {
							if(getChunk().getBlock(localX, y, localZ) != block.getByteType()) {
								getChunk().setBlock(localX, y - 0, localZ, block.getByteType());
								dirty = true;
								break;
							}
						}
					} catch(ArrayIndexOutOfBoundsException e) {}
				}
			}
		}
	}
	
	public void replaceBlocks(byte[] blocks) {
		this.chunk = new Chunk(blocks, origin);
		this.mesh = new ChunkMesh(chunk);
		this.entity = null;
	}
	
	public ChunkCoordinates getOrigin() {
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
	
	public ChunkEntity getEntity() {
		return entity;
	}
	
	public void setEntity(ChunkEntity entity) {
		this.entity = entity;
	}
}
