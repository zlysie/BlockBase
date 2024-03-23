package net.oikmo.engine.entity;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.world.blocks.Block;
import net.oikmo.engine.world.chunk.Chunk;
import net.oikmo.engine.world.chunk.ChunkManager;
import net.oikmo.engine.world.chunk.MasterChunk;
import net.oikmo.main.GameSettings;
import net.oikmo.main.Main;

/**
 * Camera class. Allows the player to see the world.
 * 
 * @author <i>Oikmo</i>
 */
public class Camera {


	private int maxVerticalTurn = 80;
	private Vector3f position;
	private Vector3f chunkPosition;
	
	public float pitch = 0;
	public float yaw = 0;
	public float roll = 0;
	
	private MasterChunk currentChunk;

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
		this.pitch = rotation.x;
		this.roll = rotation.z;
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
					
					
					
					/*for(int i = 0; i < Main.theWorld.chunks.size(); i++) {
						Chunk chunk = Main.theWorld.chunks.get(i);
						if(chunk != null) {
							if(chunk.origin.x/16 == chunkX && chunk.origin.z/16 == chunkZ) {
								currentChunk = chunk;
							}

						} else {
							System.out.println("Null!");
						}
					}*/
				}
			}
		});

		chunkGetter.setName("Chunk Grabber");
		chunkGetter.start();
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

	private float speeds = 0f;
	private float speed = 0.1f;
	private float moveAt;
	private boolean flyCam = true;
	private boolean lockInCam;
	private Block selectedBlock;
	
	/**
	 * Fly cam
	 */
	public void update() {
		try {
			int index = Integer.parseInt(Keyboard.getKeyName(Keyboard.getEventKey()))-1 != -1 ? Integer.parseInt(Keyboard.getKeyName(Keyboard.getEventKey()))-1 : 0;
			selectedBlock = Block.blocks[index] != null ? Block.blocks[index] : Block.bedrock;
		} catch(NumberFormatException e) {}
		
		if(currentChunk != null) {
			if(Mouse.isButtonDown(1)) {
				ChunkManager.setBlock(new Vector3f(position), selectedBlock, currentChunk);
			} else if(Mouse.isButtonDown(0)) {
				ChunkManager.setBlock(new Vector3f(position), null, currentChunk);
			}
			
			if(Keyboard.isKeyDown(Keyboard.KEY_E)) {
				currentChunk.getChunk().setBlockFromTopLayer((int)position.x, (int)position.z, selectedBlock);
				
			}
		}

		if(!lockInCam) {
			if(Keyboard.isKeyDown(Keyboard.KEY_Q)) {
				flyCam = !flyCam;
				lockInCam = true;
			}
		} else {
			if(!Keyboard.isKeyDown(Keyboard.KEY_Q)) {
				lockInCam = false;
			}
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