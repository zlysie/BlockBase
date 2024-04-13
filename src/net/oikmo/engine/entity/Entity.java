package net.oikmo.engine.entity;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.mojang.minecraft.phys.AABB;

import net.oikmo.engine.models.RawModel;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.world.chunk.MasterChunk;
import net.oikmo.main.Main;
import net.oikmo.toolbox.Maths;

public class Entity {
	private TexturedModel model;
	private Vector3f position;
	protected Vector3f motion;
	private Vector3f rotation;
	private float scale;
	private Vector3f roundPos;
	private Vector3f chunkPos;
	
	protected AABB bb;
	protected boolean onGround;
	protected float heightOffset = 0.0F;
	protected float bbWidth = 0.6F;
	protected float bbHeight = 1.8F;
	
	public Entity(TexturedModel model, Vector3f position, Vector3f rotation, float scale) {
		this.model = model;
		this.position = position;
		this.motion = new Vector3f();
		this.rotation = rotation;
		this.scale = scale;
		setPos(position.x, position.y, position.z);
	}

	protected void setSize(float w, float h) {
		this.bbWidth = w;
		this.bbHeight = h;
	}

	protected void setPos(float x, float y, float z) {
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
		float w = this.bbWidth / 2.0F;
		float h = this.bbHeight / 2.0F;
		this.bb = new AABB(x - w, y - h, z - w, x + w, y + h, z + w);
	}

	protected void set(float width, float height,float x, float y, float z) {
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
		float w = this.bbWidth / 2.0F;
		float h = this.bbHeight / 2.0F;
		this.bb = new AABB(x - w, y - h, z - w, x + w, y + h, z + w);
	}
	
	public List<AABB> getSurroundingAABBsPhys() {
		
		List<AABB> surroundingAABBs = new ArrayList<>();
		
		float aabbOffset = 2.0F;
		
		int x0 = Maths.roundFloat(bb.x0 - aabbOffset);
		int x1 = Maths.roundFloat(bb.x1 + aabbOffset);
		int y0 = Maths.roundFloat(bb.y0 - aabbOffset);
		int y1 = Maths.roundFloat(bb.y1 + aabbOffset);
		int z0 = Maths.roundFloat(bb.z0 - aabbOffset);
		int z1 = Maths.roundFloat(bb.z1 + aabbOffset);
		
		for(int x = x0; x < x1; ++x) {
			for(int y = y0; y < y1; ++y) {
				for(int z = z0; z < z1; ++z) {
					if(Main.theWorld.getBlock(new Vector3f(x,y,z)) != null) {
						AABB other = new AABB(x-0.5f, y-0.5f, z-0.5f, x+0.5f, y+0.5f, z+0.5f);
						//System.out.println(x + " " + y + " " + z);
						surroundingAABBs.add(other);
					}
					
				}
			}
		}
	
		
		return surroundingAABBs;
	}
	
	/**
	 * handles aabb collision
	 * 
	 * @param xa - ({@link Float})
	 * @param ya - ({@link Float})
	 * @param za - ({@link Float})
	 */
	public void move(float xa, float ya, float za) {
		float xaOrg = xa;
		float yaOrg = ya;
		float zaOrg = za;
		List<AABB> aABBs = this.getSurroundingAABBsPhys();

		int i;
		for(i = 0; i < aABBs.size(); ++i) {
			ya = ((AABB)aABBs.get(i)).clipYCollide(this.bb, ya);
		}

		this.bb.move(0.0F, ya, 0.0F);

		for(i = 0; i < aABBs.size(); ++i) {
			xa = ((AABB)aABBs.get(i)).clipXCollide(this.bb, xa);
		}

		this.bb.move(xa, 0.0F, 0.0F);

		for(i = 0; i < aABBs.size(); ++i) {
			za = ((AABB)aABBs.get(i)).clipZCollide(this.bb, za);
		}

		this.bb.move(0.0F, 0.0F, za);
		this.onGround = yaOrg != ya && yaOrg < 0.0F;
		if(xaOrg != xa) {
			this.motion.x = 0.0F;
		}

		if(yaOrg != ya) {
			this.motion.y = 0.0F;
		}

		if(zaOrg != za) {
			this.motion.z = 0.0F;
		}
		
		this.position.x = (this.bb.x0 + this.bb.x1) / 2.0F;
		this.position.y = this.bb.y0 + this.heightOffset;
		this.position.z = (this.bb.z0 + this.bb.z1) / 2.0F;
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
	
	public MasterChunk getCurrentChunk() {
		if(chunkPos == null) { chunkPos = new Vector3f(); }
		Maths.calculateChunkPosition(getPosition(), chunkPos);
		synchronized(MasterChunk.chunkMap) {
			synchronized(MasterChunk.usedPositions) {
				return MasterChunk.getChunkFromPosition(chunkPos);
			}

		}
	}
	
	public AABB getAABB() {
		return bb;
	}

	public float getHeightOffset() {
		return heightOffset;
	}
	
	public void tick() {}
}