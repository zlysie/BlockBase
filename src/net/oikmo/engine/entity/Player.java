package net.oikmo.engine.entity;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import com.github.matthewdawsey.collisionres.AABB;

import net.oikmo.engine.models.CubeModel;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.textures.ModelTexture;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.engine.world.chunk.MasterChunk;

public class Player extends Entity {
	
	private Camera camera;
	
	public Player(Vector3f position, Vector3f rotation) {
		super(new TexturedModel(CubeModel.getRawModel(Block.obsidian), ModelTexture.create("textures/transparent")), position, rotation,1f);
		resetPos();
		this.heightOffset = 0.81f; //1.62f
		this.camera = new Camera(position, rotation);
	}
	
	public void tick() {
		float xa = 0.0F;
		float ya = 0.0F;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_E)) {
			resetPos();
		}
  		
		if(Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W)) {
			--ya;
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_S)) {
			++ya;
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A)) {
			--xa;
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D)) {
			++xa;
		}
		
		if((Keyboard.isKeyDown(Keyboard.KEY_SPACE) || Keyboard.isKeyDown(Keyboard.KEY_LMETA)) && this.onGround) {
			this.motion.y = 0.12F;
		}
		
		this.setRotation(0.0f, camera.yaw, 0.0f);
		this.moveRelative(xa, ya, this.onGround ? 0.02F : 0.005F);
		this.motion.y = (float)((double)this.motion.y - 0.005D);
		this.move();
		if(this.getPosition().y < 0) {
			resetPos();
		}
		
		this.motion.x *= 0.91F;
		this.motion.y *= 0.98F;
		this.motion.z *= 0.91F;
		if(this.onGround) {
			this.motion.x *= 0.8F;
			this.motion.z *= 0.8F;
		}
	}
	
	public void updateCamera() {
		camera.update(new Vector3f(getPosition()), heightOffset);
	}
	
	private void resetPos() {
		MasterChunk currentChunk = getCurrentChunk();
		if(currentChunk != null) {
			this.setPos(getPosition().x, currentChunk.getChunk().getHeightFromPosition(currentChunk.getOrigin(), getPosition()), getPosition().z);
		}
		
	}

	public Camera getCamera() {
		return camera;
	}

	public AABB getAABB() {
		return aabb;
	}

	public float getHeightOffset() {
		return heightOffset;
	}

}
