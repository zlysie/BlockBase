package net.oikmo.engine.entity;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.github.matthewdawsey.collisionres.AABB;

import net.oikmo.engine.models.RawModel;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.world.chunk.Chunk;
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
	
	protected AABB aabb;
	protected boolean onGround;
	protected float heightOffset = 0.0F;
	protected float bbWidth = 0.6F;
	protected float bbHeight = 1.8F;
	AABB floorCheckAABB;
	AABB ceilingCheckAABB;
	private static ArrayList<AABB> aabbsToUseAroundPlayer = new ArrayList<>();
	
	public Entity(TexturedModel model, Vector3f position, Vector3f rotation, float scale) {
		this.model = model;
		this.position = position;
		this.aabb = new AABB(
				new Vector3f(this.bbWidth / -2, this.bbHeight / -2, this.bbWidth / -2),
				new Vector3f(this.bbWidth / 2, this.bbHeight / 2, this.bbWidth / 2));
		ceilingCheckAABB = new AABB(new Vector3f(this.bbWidth / -2, 0, this.bbWidth / -2), new Vector3f(this.bbWidth / 2, 0.01f, this.bbWidth / 2));
		floorCheckAABB = new AABB(new Vector3f(this.bbWidth / -2, -0.01f, this.bbWidth / -2), new Vector3f(this.bbWidth / 2, 0, this.bbWidth / 2));
		this.motion = new Vector3f();
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
		this.aabb = new AABB(
				new Vector3f(this.bbWidth / -2, this.bbHeight / -2, this.bbWidth / -2),
				new Vector3f(this.bbWidth / 2, this.bbHeight / 2, this.bbWidth / 2));
		ceilingCheckAABB = new AABB(new Vector3f(this.bbWidth / -2, 0, this.bbWidth / -2), new Vector3f(this.bbWidth / 2, 0.01f, this.bbWidth / 2));
		floorCheckAABB = new AABB(new Vector3f(this.bbWidth / -2, -0.01f, this.bbWidth / -2), new Vector3f(this.bbWidth / 2, 0, this.bbWidth / 2));
		
	}

	protected void set(float width, float height,float x, float y, float z) {
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
		this.aabb = new AABB(
				new Vector3f(this.bbWidth / -2, this.bbHeight / -2, this.bbWidth / -2),
				new Vector3f(this.bbWidth / 2, this.bbHeight / 2, this.bbWidth / 2));
		ceilingCheckAABB = new AABB(new Vector3f(this.bbWidth / -2, 0, this.bbWidth / -2), new Vector3f(this.bbWidth / 2, 0.01f, this.bbWidth / 2));
		floorCheckAABB = new AABB(new Vector3f(this.bbWidth / -2, -0.01f, this.bbWidth / -2), new Vector3f(this.bbWidth / 2, 0, this.bbWidth / 2));
		
	}

	/**
	 * Checks neighbouring chunks and collects the AABBs from each. one.
	 * @param za 
	 * @param ya 
	 * @param xa 
	 * @return
	 */
	public List<AABB> getSurroundingAABBs() {
		aabbsToUseAroundPlayer.clear();
		List<AABB> surroundingAABBs = aabbsToUseAroundPlayer;
		MasterChunk currentChunk = getCurrentChunk();

		if(currentChunk != null) {
			for (int xOffset = -1; xOffset <= 1; xOffset++) {
				for (int zOffset = -1; zOffset <= 1; zOffset++) {

					float chunkX = (int) (currentChunk.getOrigin().x + xOffset * Chunk.CHUNK_SIZE);
					float chunkZ = (int) (currentChunk.getOrigin().z + zOffset * Chunk.CHUNK_SIZE);

					Vector3f chunkPos = new Vector3f(chunkX, 0, chunkZ);
					MasterChunk neighborChunk = MasterChunk.getChunkFromPosition(chunkPos);

					if (neighborChunk != null && neighborChunk.getEntity() != null) {
						for (AABB aabb : neighborChunk.getChunk().getAABBs(neighborChunk.getOrigin(), aabb)) {
							surroundingAABBs.add(aabb);
						}
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
	public void move() {
		this.position.y += this.motion.y;
		this.aabb.updatePosition(this.position);
		
		// separate colliders with a height value of 0.01
		// on the top and bottom to detect Y axis collisions
		floorCheckAABB.updatePosition(this.position);
		floorCheckAABB.offset(0, bbHeight / -2, 0);
		ceilingCheckAABB.updatePosition(this.position);
		ceilingCheckAABB.offset(0, this.bbHeight / 2, 0);
		//
		// assume falling if not standing on a collider
		this.onGround = false;
		List<AABB> aabbList = new ArrayList<>();
		// get the list of colliders
		if(this instanceof Player) {
			aabbList = getSurroundingAABBs();
		} else {
			aabbList = aabbsToUseAroundPlayer;
		}
		
		// check for Y axis collisions
		for (AABB aabb : aabbList) {
			if (floorCheckAABB.intersects(aabb)) {
				float dy = this.aabb.center.y - aabb.center.y;
				if (dy > 0) { // colliding with +Y face (standing on floor)
					this.onGround = true;
					this.position.y = aabb.end.y + this.bbHeight / 2;
					this.motion.y = 0;
				}
			}
			
			if (ceilingCheckAABB.intersects(aabb)) {
				float dy = this.aabb.center.y - aabb.center.y;
				if (dy < 0) { // colliding with -Y face (bump head on ceiling)
					this.onGround = false;
					this.position.y = aabb.start.y - this.bbHeight / 2;
					this.motion.y = 0;
				}
			}
		}

		// apply X axis velocity
		this.position.x += this.motion.x;
		this.aabb.updatePosition(this.position);

		// check for X axis collisions
		for (AABB aabb : aabbList) {
			if (this.aabb.intersects(aabb)) {
				float dx = this.aabb.center.x - aabb.center.x;
				if (dx > 0) { // colliding with +X face
					this.position.x = aabb.end.x + this.bbWidth / 2;
				} else if (dx < 0) { // colliding with -X face
					this.position.x = aabb.start.x - this.bbWidth / 2;
				}

				this.motion.x = 0;
			}
		}

		// apply Z axis velocity
		this.position.z += this.motion.z;
		this.aabb.updatePosition(this.position);

		// check for Z axis collisions
		for (AABB aabb : aabbList) {
			if (this.aabb.intersects(aabb)) {
				float dz = this.aabb.center.z - aabb.center.z;
				if (dz > 0) { // colliding with +Z face
					this.position.z = aabb.end.z + this.bbWidth / 2;
				} else if (dz < 0) { // colliding with -Z face
					this.position.z = aabb.start.z - this.bbWidth / 2;
				}

				this.motion.z = 0;
			}
		}

		// one final realignment so collider doesnt lag behind when being drawn
		//this.aabb.updatePosition(this.position);
	}
	
	public void moveY() {
		
		this.position.y += motion.y;
		this.aabb.updatePosition(this.position);
		
		floorCheckAABB.updatePosition(this.position);
		floorCheckAABB.offset(0, bbHeight / -2, 0);
		ceilingCheckAABB.updatePosition(this.position);
		ceilingCheckAABB.offset(0, this.bbHeight / 2, 0);
		// assume falling if not standing on a collider
		this.onGround = false;
		// get the list of colliders
		Vector3f v = new Vector3f(this.getRoundedPosition());
		v.y = getRoundedPosition().y;
		if(Main.theWorld.getBlock(v) != null) {
			AABB aabb = new AABB(new Vector3f(-0.5f, -0.5f, -0.5f), new Vector3f(0.5f, 0.5f, 0.5f));
			aabb.updatePosition(new Vector3f(v));
			
			if (floorCheckAABB.intersects(aabb)) {
				float dy = this.aabb.center.y - aabb.center.y;
				if (dy > 0) { // colliding with +Y face (standing on floor)
					this.onGround = true;
					this.position.y = aabb.end.y + this.bbHeight / 2;
					this.motion.y = 0;
				}
			}
			
			if (ceilingCheckAABB.intersects(aabb)) {
				float dy = this.aabb.center.y - aabb.center.y;
				if (dy < 0) { // colliding with -Y face (bump head on ceiling)
					this.onGround = false;
					this.position.y = aabb.start.y - this.bbHeight / 2;
					this.motion.y = 0;
				}
			}
		}
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
	
	public void tick() {}
}