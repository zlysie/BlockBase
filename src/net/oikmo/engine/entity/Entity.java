package net.oikmo.engine.entity;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.AABB;
import net.oikmo.engine.DisplayManager;
import net.oikmo.engine.models.RawModel;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.world.chunk.Chunk;
import net.oikmo.engine.world.chunk.MasterChunk;
import net.oikmo.toolbox.Maths;

public class Entity {
	private TexturedModel model;
	private Vector3f position;
	protected Vector3f distance;
	private Vector3f rotation;
	private float scale;
	private AABB bb;
	protected boolean onGround;
	protected float heightOffset = 0.0F;
	protected float bbWidth = 0.6F;
	protected float bbHeight = 1.8F;


	public Entity(TexturedModel model, Vector3f position, Vector3f rotation, float scale) {
		this.model = model;
		this.position = position;
		this.bb = new AABB(position.x - bbWidth, position.y - bbHeight, position.z - bbWidth, position.x + bbWidth, position.y + bbHeight, position.z + bbWidth);
		this.distance = new Vector3f();
		this.rotation = rotation;
		this.scale = scale;
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
		float w = this.bbWidth = width;
		float h = this.bbHeight = height;
		this.bb = new AABB(x - w, y - h, z - w, x + w, y + h, z + w);
	}
	
	/**
	 * Checks neighbouring chunks and collects the AABBs from each. one.
	 * @param za 
	 * @param ya 
	 * @param xa 
	 * @return
	 */
	public List<AABB> getSurroundingAABBs(float xa, float ya, float za) {
		List<AABB> surroundingAABBs = new ArrayList<>();
		MasterChunk currentChunk = getCurrentChunk();
		
		if(currentChunk != null) {
			for (int xOffset = 0; xOffset <= 1; xOffset++) {
				for (int zOffset = 0; zOffset <= 1; zOffset++) {
					
					int chunkX = (int) (currentChunk.getOrigin().x + xOffset * Chunk.CHUNK_SIZE);
					int chunkZ = (int) (currentChunk.getOrigin().z + zOffset * Chunk.CHUNK_SIZE);
					
					Vector3f chunkPos = new Vector3f(chunkX, 0, chunkZ);
					MasterChunk neighborChunk = MasterChunk.getChunkFromPosition(chunkPos);

					if (neighborChunk != null) {
						for (AABB aabb : neighborChunk.getChunk().getAABBs()) {
							surroundingAABBs.add(aabb);
						}
					}
				}
			}
		}
		return surroundingAABBs;
	}
	
	/**
	 * This shit aint even mine. this is minecraft source. :sob:
	 * 
	 * @param xa - ({@link Float})
	 * @param ya - ({@link Float})
	 * @param za - ({@link Float})
	 */
	public void move(float xa, float ya, float za) {
		float xaOrg = xa;
		float yaOrg = ya;
		float zaOrg = za;
		List<AABB> aABBs = getSurroundingAABBs(xa, ya, za);
		
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
			this.distance.x = 0.0F;
		}

		if(yaOrg != ya) {
			this.distance.y = 0.0F;
		}

		if(zaOrg != za) {
			this.distance.z = 0.0F;
		}

		this.position.x = (this.bb.minX + this.bb.maxX) / 2.0F;
		this.position.y = this.bb.minY + this.heightOffset;
		this.position.z = (this.bb.minZ + this.bb.maxZ) / 2.0F;
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
			this.distance.x += ((xa * cos - za * sin) * DisplayManager.getFrameTimeSeconds() * 10);
			this.distance.z += ((za * cos + xa * sin) * DisplayManager.getFrameTimeSeconds() * 10);
		}
	}

	public void setRawModel(RawModel model) {
		this.model.setRawModel(model);
	}

	public void setTextureID(int textureID) {
		this.model.getTexture().setTextureID(textureID);
	}

	private float whiteOffset;
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

	protected void setRotation(float pitch, float yaw, float roll) {
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
		return chunkPos;
	}
	
	Vector3f chunkPos;
	public MasterChunk getCurrentChunk() {
		synchronized(MasterChunk.chunkMap) {
			if(chunkPos == null) { chunkPos = new Vector3f(); }
			Maths.calculateChunkPosition(getPosition(), chunkPos);
			return MasterChunk.getChunkFromPosition(chunkPos);
		}
		
	}
}