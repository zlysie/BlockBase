package net.oikmo.engine.entity;

import java.util.List;

import net.oikmo.engine.AABB;
import net.oikmo.engine.DisplayManager;
import net.oikmo.engine.models.RawModel;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.world.chunk.MasterChunk;
import net.oikmo.toolbox.Maths;

import org.lwjgl.util.vector.Vector3f;

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
		setPos(position.x,position.y,position.z);
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
	
	public void move(float xa, float ya, float za) {
		float xaOrg = xa;
		float yaOrg = ya;
		float zaOrg = za;
		List<AABB> aABBs = null;
		if(getCurrentChunk() != null) {
			aABBs = getCurrentChunk().getChunk().getCubes(this.bb.expand(xa, ya, za));
		}
		if(aABBs == null) { return; }
		
		for(int i = 0; i < aABBs.size(); i++) {
			ya = aABBs.get(i).clipYCollide(this.bb, ya);
			
			xa = aABBs.get(i).clipXCollide(this.bb, xa);
			
			za = aABBs.get(i).clipZCollide(this.bb, za);
			
		}
		this.bb.move(0.0F, ya, 0.0F);
		this.bb.move(xa, 0.0F, 0.0F);
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

		this.position.x = (this.bb.x0 + this.bb.x1) / 2.0F;
		this.position.y = this.bb.y0 + this.heightOffset;
		this.position.z = (this.bb.z0 + this.bb.z1) / 2.0F;
	}

	public void moveRelative(float xa, float za, float speed) {
		float dist = xa * xa + za * za;
		if(dist >= 0.01F) {
			dist = speed / (float)Math.sqrt((double)dist);
			xa *= dist;
			za *= dist;
			float sin = (float)Math.sin((double)this.rotation.y * Math.PI / 180.0D);
			float cos = (float)Math.cos((double)this.rotation.y * Math.PI / 180.0D);
			this.distance.x += (xa * cos - za * sin) * DisplayManager.getFrameTimeSeconds();
			this.distance.z += (za * cos + xa * sin) * DisplayManager.getFrameTimeSeconds();
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
	
	public Vector3f getRotation() {
		return rotation;
	}
	
	public float getScale() {
		return scale;
	}
	
	Vector3f chunkPos;
	public MasterChunk getCurrentChunk() {
		if(chunkPos == null) { chunkPos = new Vector3f(); }
		Maths.calculateChunkPosition(getPosition(), chunkPos);
		return MasterChunk.getChunkFromPosition(chunkPos);
	}
}