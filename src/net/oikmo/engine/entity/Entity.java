package net.oikmo.engine.entity;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.mojang.minecraft.phys.AABB;

import net.oikmo.engine.models.RawModel;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.sound.SoundMaster;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.engine.world.chunk.MasterChunk;
import net.oikmo.engine.world.chunk.coordinate.ChunkCoordinates;
import net.oikmo.main.Main;
import net.oikmo.network.shared.PacketPlaySoundAt;
import net.oikmo.toolbox.FastMath;
import net.oikmo.toolbox.Maths;

public class Entity {
	private TexturedModel model;
	private Vector3f position;
	protected Vector3f motion;
	private Vector3f rotation;
	private float scale;
	private Vector3f roundPos;
	private Vector3f chunkPos;
	public float distanceWalkedModified;
	protected float fallDistance;
	private int nextStepDistance;

	protected AABB aabb;
	protected boolean onGround;
	protected float heightOffset = 0.0F;
	protected float bbWidth = 0.6F;
	protected float bbHeight = 1.8F;
	
	protected boolean remove = false;
	
	public Entity(TexturedModel model, Vector3f position, Vector3f rotation, float scale) {
		this.model = model;
		this.position = position;
		this.motion = new Vector3f();
		this.rotation = rotation;
		this.scale = scale;
		setPos(position.x, position.y, position.z);
	}
	
	public Entity(Vector3f position, Vector3f rotation, float scale) {
		this.position = position;
		this.motion = new Vector3f();
		this.rotation = rotation;
		this.scale = scale;
		setPos(position.x, position.y, position.z);
	}

	protected void setSize(float width, float height) {
		this.setBbWidth(width);
		this.setBbHeight(height);
		float x = position.x;
		float y = position.y;
		float z = position.z;
		float w = this.getBBWidth() / 1.0F;
		float h = this.getBBHeight() / 1.0F;
		this.aabb = new AABB(x, y, z, x + w, y + h, z + w);
	}

	public void setPos(float x, float y, float z) {
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
		float w = this.getBBWidth() / 1.0F;
		float h = this.getBBHeight() / 1.0F;
		this.aabb = new AABB(x, y, z, x + w, y + h, z + w);
	}

	/**
	 * handles aabb collision
	 * 
	 * @param xa - ({@link Float})
	 * @param ya - ({@link Float})
	 * @param za - ({@link Float})
	 */
	public void move(float xa, float ya, float za, int size) {
		double prevX = position.x;
		double prevZ = position.z;
		moveWithoutSound(xa,ya,za, size);
		
		double offsetX = position.x - prevX;
		double offsetZ = position.z - prevZ;
		distanceWalkedModified += (double)FastMath.sqrt((float) (offsetX * offsetX + offsetZ * offsetZ)) * 0.7D;
		int posX = (int)(position.x);
		int posY = (int)(position.y)-1;
		int posZ = (int)(position.z);
		if(Main.theWorld != null) {
			Block block = Main.theWorld.getBlock(new Vector3f(posX,posY,posZ));
			if(distanceWalkedModified > (float)nextStepDistance && block != null) {
				nextStepDistance++;
				SoundMaster.playBlockPlaceSFX(block, posX, posY, posZ);
				if(Main.theNetwork != null) {
					PacketPlaySoundAt packet = new PacketPlaySoundAt();
					packet.place = true;
					packet.blockID = block.getByteType();
					packet.x = posX;
					packet.y = posY;
					packet.z = posZ;
					Main.theNetwork.client.sendTCP(packet);
				}
			}
		}
		
	}
	
	public void moveWithoutSound(float xa, float ya, float za, int size) {
		float xaOrg = xa;
		float yaOrg = ya;
		float zaOrg = za;

		if(Main.theWorld != null) {
			List<AABB> aabbs = Main.theWorld.getSurroundingAABBsPhys(this.aabb, size);
			
			int i;
			for(i = 0; i < aabbs.size(); ++i) {
				ya = aabbs.get(i).clipYCollide(this.aabb, ya);
			}

			this.aabb.move(0.0F, ya, 0.0F);

			for(i = 0; i < aabbs.size(); ++i) {
				xa = aabbs.get(i).clipXCollide(this.aabb, xa);
			}

			this.aabb.move(xa, 0.0F, 0.0F);

			for(i = 0; i < aabbs.size(); ++i) {
				za = aabbs.get(i).clipZCollide(this.aabb, za);
			}

			this.aabb.move(0.0F, 0.0F, za);
			this.setOnGround(yaOrg != ya && yaOrg < 0.0F);
			if(xaOrg != xa) {
				this.motion.x = 0.0F;
			}

			if(yaOrg != ya) {
				this.motion.y = 0.0F;
			}

			if(zaOrg != za) {
				this.motion.z = 0.0F;
			}
		}
		
		if(this instanceof Player) {
			this.position.x = ((this.aabb.minX + this.aabb.maxX) / 2.0F);
			this.position.y = this.aabb.minY + this.heightOffset;
			this.position.z = ((this.aabb.minZ + this.aabb.maxZ) / 2.0F);
		} else {
			this.position.x = ((this.aabb.minX + this.aabb.maxX) / 2.0F)-0.5f;
			this.position.y = this.aabb.minY + this.heightOffset;
			this.position.z = ((this.aabb.minZ + this.aabb.maxZ) / 2.0F)-0.5f;
		}
		
	}
	public float getBrightness() {
		int x = (int)this.position.x;
		int y = (int)(this.position.y + this.heightOffset / 2.0F);
		int z = (int)this.position.z;
		if(Main.theWorld.getChunkFromPosition(getCurrentChunkPosition()) != null) {
			return Main.theWorld.getChunkFromPosition(getCurrentChunkPosition()).getChunk().getBrightness(x, y, z);
		}
		return 1;
	}

	/**
	 * handles aabb collision
	 * 
	 * @param xa - ({@link Float})
	 * @param ya - ({@link Float})
	 * @param za - ({@link Float})
	 */
	public void move(float xa, float ya, float za) {
		move(xa,ya,za,2);
	}

	/**
	 * This shit aint even mine. this is minecraft source. :sob:
	 * 
	 * @param xa - ({@link Float})
	 * @param za - ({@link Float})
	 * @param speed - ({@link Float})
	 */
	public void moveRelative(float xa, float za, float speed) {
		float dist = xa * xa + za * za;
		if(dist >= 0.01F) {
			dist = speed / (float)Math.sqrt((double)dist);
			xa *= dist;
			za *= dist;
			float sin = (float)Math.sin((double)this.rotation.y * Math.PI / 180.0D);
			float cos = (float)Math.cos((double)this.rotation.y * Math.PI / 180.0D);
			this.motion.x += xa * cos - za * sin;
			this.motion.z += za * cos + xa * sin;
		}
	}

	public void setRawModel(RawModel model) {
		this.model.setRawModel(model);
	}

	public void setTextureID(int textureID) {
		this.model.getTexture().setTextureID(textureID);
	}

	private float whiteOffset;
	public boolean elements = false;
	public void setWhiteOffset(float whiteOffset) {
		this.whiteOffset = whiteOffset;
	}
	public float getWhiteOffset() {
		return whiteOffset;
	}

	public TexturedModel getModel() {
		return model;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	protected void setPosition(float x, float y, float z) {
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
	}

	public void increasePosition(float x, float y, float z) {
		this.position.x += x;
		this.position.y += y;
		this.position.z += z;	
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}

	public void increaseRotation(float x, float y, float z) {
		this.rotation.x += x;
		this.rotation.y += y;
		this.rotation.z += z;	
	}	

	public void setRotation(float pitch, float yaw, float roll) {
		this.rotation.x = pitch;
		this.rotation.y = yaw;
		this.rotation.z = roll;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public float getScale() {
		return scale;
	}

	public Vector3f getCurrentChunkPosition() {
		getCurrentChunk();
		return chunkPos;
	}

	public void resetMotion() {
		this.motion.x = 0;
		this.motion.y = 0;
		this.motion.z = 0;
	}

	public Vector3f getRoundedPosition() {
		if(roundPos == null) { roundPos = new Vector3f(); }
		Maths.roundVector(getPosition(), roundPos);
		return roundPos;
	}
	
	public boolean shouldBeRemoved() {
		return remove;
	}

	public MasterChunk getCurrentChunk() {
		if(chunkPos == null) { chunkPos = new Vector3f(); }
		ChunkCoordinates chunkPos = Maths.calculateChunkPosition(position);
		if(Main.theWorld != null) {
			synchronized(Main.theWorld.chunkMap) {
				return Main.theWorld.getChunkFromPosition(chunkPos);
			}
		}
		return null;
	}

	public void setAABB(AABB aabb) {
		this.aabb = aabb;
	}

	public AABB getAABB() {
		return aabb;
	}

	public float getHeightOffset() {
		return heightOffset;
	}

	public void tick() {}

	public boolean isOnGround() {
		return onGround;
	}

	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}

	public float getBBWidth() {
		return bbWidth;
	}

	public void setBbWidth(float bbWidth) {
		this.bbWidth = bbWidth;
	}

	public float getBBHeight() {
		return bbHeight;
	}

	public void setBbHeight(float bbHeight) {
		this.bbHeight = bbHeight;
	}
}