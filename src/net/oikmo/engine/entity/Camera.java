package net.oikmo.engine.entity;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.Loader;
import net.oikmo.engine.models.CubeModel;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.engine.textures.ModelTexture;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.engine.world.chunk.Chunk;
import net.oikmo.engine.world.chunk.ChunkManager;
import net.oikmo.engine.world.chunk.MasterChunk;
import net.oikmo.main.GameSettings;
import net.oikmo.main.Main;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Maths;
import net.oikmo.toolbox.MousePicker;
import net.oikmo.toolbox.Logger.LogLevel;

/**
 * Camera class. Allows the player to see the world.
 * 
 * @author <i>Oikmo</i>
 */
public class Camera {


	private int maxVerticalTurn = 90;
	private Vector3f position;
	private Vector3f chunkPosition;
	private Vector3f roundedPosition;
	
	public float pitch = 0;
	public float yaw = 0;
	public float roll = 0;
	
	private MasterChunk currentChunk;
	private MousePicker picker;
	private Entity block;
	
	private float speeds = 0f;
	private float speed = 0.1f;
	private float moveAt;
	private boolean flyCam = true;
	private boolean lockInCam;
	private Block selectedBlock = Block.cobble;
	
	/**
	 * Camera constructor. Sets position and rotation.
	 * 
	 * @param position
	 * @param rotation
	 * @param scale
	 */
	public Camera(Vector3f position, Vector3f rotation) {
		this.position = position;
		this.chunkPosition = new Vector3f();
		this.roundedPosition = new Vector3f();
		this.pitch = rotation.x;
		this.roll = rotation.z;
		this.picker = new MousePicker(this, MasterRenderer.getInstance().getProjectionMatrix());
		this.block = new Entity(new TexturedModel(Loader.getInstance().loadToVAO(CubeModel.vertices, CubeModel.convert(selectedBlock.getType())), new ModelTexture(Loader.getInstance().loadTexture("defaultPack"))), position, rotation, 1);
		flyCam = true;
		startThread1();
	}

	private void startThread1() {

		Thread chunkGetter = new Thread(new Runnable() {
			public void run() {
				while(!Main.displayRequest) {
					if(position.x >= 0) {
						chunkPosition.x = (int) (position.x / Chunk.CHUNK_SIZE)*16;
					} else {
						if(position.x > -16) {
							chunkPosition.x = (int)-1*16;
						} else {
							chunkPosition.x = (int) ((position.x / Chunk.CHUNK_SIZE)-1)*16;
						}
					}

					if(position.z >= 0) {
						chunkPosition.z = (int) (position.z / Chunk.CHUNK_SIZE) * 16;
					} else {
						if(position.z > -16) {
							chunkPosition.z = (int)-1 * 16;
						} else {
							chunkPosition.z = (int) ((position.z / Chunk.CHUNK_SIZE)-1)*16;
						}
					}
					
					MasterChunk master = MasterChunk.getChunkFromPosition(chunkPosition);
					if(master != null) {
						currentChunk = master;
					}
				}
			}
		});

		chunkGetter.setName("Chunk Grabber");
		chunkGetter.start();
	}
	
	/**
	 * Fly cam
	 */
	public void update() {
		roundedPosition.x = Maths.roundFloat(position.x);
		roundedPosition.y = Maths.roundFloat(position.y);
		roundedPosition.z = Maths.roundFloat(position.z);
		
		picker.update();
		Vector3f pos = picker.getPoint();
		pos.x = Maths.roundFloat(pos.x);
		pos.y = Maths.roundFloat(pos.y);
		pos.z = Maths.roundFloat(pos.z);
		
		block.setPosition(pos);
		
		MasterRenderer.getInstance().addEntity(block);
		
		try {
			int index = Integer.parseInt(Keyboard.getKeyName(Keyboard.getEventKey()))-1 != -1 ? Integer.parseInt(Keyboard.getKeyName(Keyboard.getEventKey()))-1 : 0;
			
			System.out.println(index);
			if(Block.blocks[index] != null) {
				if(selectedBlock != Block.blocks[index]) {
					selectedBlock = Block.blocks[index];
					block.getModel().setRawModel(Loader.getInstance().loadToVAO(CubeModel.vertices, CubeModel.convert(selectedBlock.getType())));
				}
			} else {
				
			}
		} catch(NumberFormatException e) {}
		
		if(currentChunk != null) {
			if(Mouse.isButtonDown(1)) {
				ChunkManager.setBlock(pos, selectedBlock, currentChunk);
			} else if(Mouse.isButtonDown(0)) {
				ChunkManager.setBlock(pos, null, currentChunk);
			}
			
			if(Keyboard.isKeyDown(Keyboard.KEY_E)) {
				ChunkManager.setBlockFromTopLayer((int)roundedPosition.x, (int)roundedPosition.z, selectedBlock, currentChunk);
				
			}
		} else {
			Logger.log(LogLevel.WARN, "No chunk loaded!");
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			if(!lockInCam) {
				flyCam = !flyCam;
				lockInCam = true;
			}
		} else {
			lockInCam = false;
		}

		if(Mouse.isGrabbed() != flyCam) {
			Mouse.setGrabbed(flyCam);
		}

		if(flyCam) {
			
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				speeds = 6;
			} else {
				speeds = 2;
			}

			if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
				position.y += speed * speeds;
			} else if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
				position.y -= speed * speeds;
			}

			if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
				moveAt = -speed * speeds;
			} else if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
				moveAt = speed * speeds;
			} else {
				moveAt = 0;
			}

			if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
				position.x += (float) -((speed * speeds) * Math.cos(Math.toRadians(yaw)));
				position.z -= (float) ((speed * speeds) * Math.sin(Math.toRadians(yaw)));
			} else if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
				position.x -= (float) -((speed * speeds) * Math.cos(Math.toRadians(yaw)));
				position.z += (float) ((speed * speeds) * Math.sin(Math.toRadians(yaw)));
			}

			pitch -= Mouse.getDY() * GameSettings.sensitivity*2;
			if(pitch < -maxVerticalTurn){
				pitch = -maxVerticalTurn;
			}else if(pitch > maxVerticalTurn){
				pitch = maxVerticalTurn;
			}
			yaw += Mouse.getDX() * GameSettings.sensitivity;

			position.x += (float) -(moveAt * Math.sin(Math.toRadians(yaw)));
			position.y += (float) (moveAt * Math.sin(Math.toRadians(pitch)));
			position.z += (float) (moveAt * Math.cos(Math.toRadians(yaw)));
		} else {
			if(Mouse.isGrabbed()) {
				Mouse.setGrabbed(false);
			}
		}
	}
	
	/**
	 * Moves camera based on given values.
	 * @param dx
	 * @param dy
	 * @param dz
	 */
	public void increasePosition(float dx, float dy, float dz) {
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
	}
	/**
	 * Rotates the camera based on given values.
	 * @param dx
	 * @param dy
	 * @param dz
	 */
	public void increaseRotation(float dx, float dy, float dz) {
		this.pitch += dx;
		this.yaw += dy;
		this.roll += dz;
	}
	/**
	 * Sets the rotation of the camera based on given values.
	 * @param dx
	 * @param dy
	 * @param dz
	 */
	public void setRotation(float dx, float dy, float dz) {
		this.pitch = dx;
		this.yaw = dy;
		this.roll = dz;
	}

	/**
	 * Sets position to given 3D Vector
	 * @param vector
	 */
	public void setPosition(Vector3f v) {
		this.position = v;
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}
	public float getYaw() {
		return yaw;
	}
	public float getRoll() {
		return roll;
	}
}