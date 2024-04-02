package net.oikmo.engine.entity;

import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.textures.ModelTexture;
import net.oikmo.toolbox.obj.OBJFileLoader;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

public class Player extends Entity {

	public Player(Vector3f position, Vector3f rotation) {
		super(new TexturedModel(OBJFileLoader.loadOBJ("player"), ModelTexture.create("textures/player")), position, rotation,0.1f);
		this.heightOffset = 1.62F;
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

		this.moveRelative(xa, ya, this.onGround ? 0.1F : 0.02F);
		this.distance.y = (float)((double)this.distance.y - 0.08D);
		this.move(this.distance.x, this.distance.y, this.distance.z);
		this.distance.x *= 0.91F;
		this.distance.y *= 0.98F;
		this.distance.z *= 0.91F;
		if(this.onGround) {
			this.distance.x *= 0.7F;
			this.distance.z *= 0.7F;
		}
	}
}
