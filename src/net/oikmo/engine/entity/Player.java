package net.oikmo.engine.entity;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.models.CubeModel;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.textures.ModelTexture;
import net.oikmo.engine.world.blocks.Block;

public class Player extends Entity {

	public Player(Vector3f position, Vector3f rotation) {
		super(new TexturedModel(CubeModel.getRawModel(Block.obsidian), ModelTexture.create("textures/defaultPack")), position, rotation,1f);
		this.heightOffset = 0; //1.62f
		set(1,1,0,60,0);
	}
	
	public void update() {
		float xa = 0.0F;
		float ya = 0.0F;
		
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
			this.distance.y = 0.5F;
		}
		if(this.getPosition().y <= 0 || getCurrentChunk() == null) {
			this.set(bbWidth, bbHeight, 1, 70, 1);
		}
		this.moveRelative(xa, ya, this.onGround ? 0.02F : 0.2F);
		this.distance.y = (float)((double)this.distance.y - 0.005D);
		this.move(this.distance.x, this.distance.y, this.distance.z);
		this.distance.x *= 0.91F;
		this.distance.y *= 0.98F;
		this.distance.z *= 0.91F;
		if(this.onGround) {
			this.distance.x *= 0.8F;
			this.distance.z *= 0.8F;
		}
	}
}
