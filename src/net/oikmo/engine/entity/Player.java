package net.oikmo.engine.entity;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.inventory.Container;
import net.oikmo.engine.models.PlayerModel;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.engine.world.chunk.MasterChunk;
import net.oikmo.main.Main;
import net.oikmo.main.gui.GuiChat;

public class Player extends Entity {
	public boolean tick = true;
	
	private Vector3f modelPosition = new Vector3f();
	private Vector3f modelRotation = new Vector3f();
	private Camera camera;
	private Container inventory;
	
	public int reachDistance = 5;
	
	public Player(Vector3f position, Vector3f rotation) {
		super(new TexturedModel(PlayerModel.getRawModel(), MasterRenderer.invisibleTexture), position, rotation,1f);
		resetPos();
		this.heightOffset = 0.81f;
		this.camera = new Camera(position, rotation);
		this.inventory = new Container(9,4);
	}
	
	public void tick() {
		if(!tick) {return;}
		
		if(camera.isPerspective() && this.getModel().getTexture().getTextureID() != Main.playerSkin) { 
			this.getModel().getTexture().setTextureID(Main.playerSkin);
		} else if(!camera.isPerspective() && this.getModel().getTexture().getTextureID() != MasterRenderer.invisibleTexture) {
			this.getModel().getTexture().setTextureID(MasterRenderer.invisibleTexture);
		}
		
		float xa = 0.0F;
		float za = 0.0F;
  		
		if(!(Main.currentScreen instanceof GuiChat)) {
			if(Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W)) {
				--za;
			}

			if(Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_S)) {
				++za;
			}

			if(Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A)) {
				--xa;
			}

			if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D)) {
				++xa;
			}
			
			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && this.isOnGround()) {
				this.motion.y = 0.15F;
			}
		}
		
		
		this.setRotation(0f, camera.yaw, 0f);
		if(camera.isPerspective()) {
			this.setRotation(0f, camera.yaw-180, 0f);
			
			modelRotation.x = 0;
			modelRotation.y = -camera.getYaw()+90;
			modelRotation.z = -camera.getPitch();
			
			modelPosition.x = getPosition().x;
			modelPosition.y = getPosition().y+heightOffset;
			modelPosition.z = getPosition().z;
			xa = -xa;
		}
		this.moveRelative(xa, za, this.isOnGround() ? 0.015F : 0.005F);
		this.motion.y = (float)((double)this.motion.y - 0.008D);
		this.move(this.motion.x, this.motion.y, this.motion.z);
		if(this.getPosition().y < 0) {
			resetPos();
		}
		
		this.motion.x *= 0.91F;
		this.motion.y *= 0.98F;
		this.motion.z *= 0.91F;
		if(this.isOnGround()) {
			this.motion.x *= 0.8F;
			this.motion.z *= 0.8F;
		}
	}
	
	public Vector3f getModelPosition() {
		return modelPosition;
	}
	
	public Vector3f getModelRotation() {
		return modelRotation;
	}
	
	public Container getInventory() {
		return inventory;
	}

	public void updateCamera() {
		if(tick)
			camera.update(this);
	}
	
	public void resetPos() {
		MasterChunk currentChunk = getCurrentChunk();
		if(currentChunk != null) {
			this.setPosition(getPosition().x, currentChunk.getChunk().getHeightFromPosition(currentChunk.getOrigin(), getPosition()), getPosition().z);
		}
	}

	public Camera getCamera() {
		return camera;
	}

	public void setInventory(Container cont) {
		this.inventory = cont;
	}

	
}
