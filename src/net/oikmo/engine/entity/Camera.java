package net.oikmo.engine.entity;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.DisplayManager;
import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.gui.component.slider.GuiText;
import net.oikmo.engine.models.CubeModel;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.engine.textures.ModelTexture;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.engine.world.chunk.Chunk;
import net.oikmo.engine.world.chunk.MasterChunk;
import net.oikmo.main.GameSettings;
import net.oikmo.main.Main;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Logger.LogLevel;
import net.oikmo.toolbox.Maths;
import net.oikmo.toolbox.MousePicker;

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
	
	private float speeds = 0f;
	private float speed = 0.1f;
	private float moveAt;
	
	private boolean flyCam = true;
	private boolean lockInCam;
	
	private MasterChunk currentChunk;
	private MousePicker picker;
	
	private int texturePackTexture;
	private int invisibleTexture;
	private Block selectedBlock = Block.cobble;
	private Entity block;
	
	private GuiText blockType;
	
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
		this.blockType = new GuiText("selectedBlock: "+selectedBlock.getEnumType().name(), 1.1f, MasterRenderer.font, new Vector2f(0,0), 1, false, false);
		this.blockType.setColour(1f, 1f, 1f);
		this.blockType.setEdge(0.2f);
		this.picker = new MousePicker(this, MasterRenderer.getInstance().getProjectionMatrix());
		this.texturePackTexture = ResourceLoader.loadTexture("textures/defaultPack");
		this.invisibleTexture = ResourceLoader.loadTexture("textures/transparent");
		this.block = new Entity(new TexturedModel(CubeModel.getRawModel(selectedBlock), new ModelTexture(texturePackTexture)), position, rotation, 1.002f);
		flyCam = true;
		startThread1();
	}

	private void startThread1() {

		Thread chunkGetter = new Thread(new Runnable() {
			public void run() {
				while(!Main.displayRequest) {
					if(block.getPosition().x >= 0) {
						chunkPosition.x = (int) (block.getPosition().x / Chunk.CHUNK_SIZE)*16;
					} else {
						if(block.getPosition().x > -16) {
							chunkPosition.x = (int)-1*16;
						} else {
							chunkPosition.x = (int) ((block.getPosition().x / Chunk.CHUNK_SIZE)-1)*16;
						}
					}

					if(block.getPosition().z >= 0) {
						chunkPosition.z = (int) (block.getPosition().z / Chunk.CHUNK_SIZE) * 16;
					} else {
						if(block.getPosition().z > -16) {
							chunkPosition.z = (int)-1 * 16;
						} else {
							chunkPosition.z = (int) ((block.getPosition().z / Chunk.CHUNK_SIZE)-1)*16;
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
	
	float multiplier = 100;
	boolean mouseClickLeft = false;
	boolean mouseClickRight = false;
	/**
	 * Fly cam
	 */
	public void update() {
		if(currentChunk != null) {
			Maths.roundVector(position, roundedPosition);
			
			picker.update();
			
			picker.distance = picker.BASE_DISTANCE;
			for(int i = 0; i < picker.BASE_DISTANCE; i++) {
				Block block = currentChunk.getBlock(picker.getPointRounded(i));
				if(block != null) {
					picker.distance = i;
					break;
				}			
			}
			
			Vector3f pos = picker.getPoint();
			Maths.roundVector(pos);
			
			block.setPosition(pos);
			Block thatBlock = currentChunk.getBlock(pos);
			if(thatBlock != null) {
				block.setWhiteOffset(2f);
				block.setRawModel(CubeModel.getRawModel(thatBlock));
			} else {
				block.setWhiteOffset(0.2f);
				block.setRawModel(CubeModel.getRawModel(selectedBlock));
			}
			
			MasterRenderer.getInstance().addEntity(block);
			
			try {
				int index = Integer.parseInt(Keyboard.getKeyName(Keyboard.getEventKey()))-1 != -1 ? Integer.parseInt(Keyboard.getKeyName(Keyboard.getEventKey()))-1 : 0;
				
				if(Block.blocks[index] != null) {
					if(selectedBlock != Block.blocks[index]) {
						selectedBlock = Block.blocks[index];
						blockType.setTextString("selectedBlock: " + selectedBlock.getEnumType().name());
					}
				}
			} catch(NumberFormatException e) {}
			
			if(currentChunk.getBlock(roundedPosition) == null) {
				block.setTextureID(texturePackTexture);
			} else {
				block.setTextureID(invisibleTexture);
			}
			
			if(Mouse.isButtonDown(1)) {
				if(!mouseClickRight) {
					Block block1 = currentChunk.getBlock(picker.getPointRounded(picker.distance));
					if(block1 == null) {
						currentChunk.setBlock(picker.getPointRounded(picker.distance), selectedBlock);
					} else {
						currentChunk.setBlock(picker.getPointRounded(picker.distance-1), selectedBlock);
					}
					mouseClickRight = true;
				}
			} else {
				mouseClickRight = false;
			}
			
			if(Mouse.isButtonDown(0)) {
				if(!mouseClickLeft) {
					Block block = currentChunk.getBlock(picker.getPointRounded());
					if(block != null) {
						currentChunk.setBlock(picker.getPointRounded(), null);
					}
					mouseClickLeft = true;
				}
			} else {
				mouseClickLeft = false;
			}
			
			if(Keyboard.isKeyDown(Keyboard.KEY_E)) {
				currentChunk.setBlockFromTopLayer((int)roundedPosition.x, (int)roundedPosition.z, selectedBlock);
			}
		} else {
			Logger.log(LogLevel.WARN, "No chunk detected!");
		}
		
		this.move();
	}
	
	private void move() {
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
				position.y += (speed * speeds) * DisplayManager.getFrameTimeSeconds() * multiplier;
			} else if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
				position.y -= (speed * speeds)  * DisplayManager.getFrameTimeSeconds() * multiplier;
			}

			if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
				moveAt = (-speed * speeds)* DisplayManager.getFrameTimeSeconds() * multiplier;
			} else if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
				moveAt = (speed * speeds)* DisplayManager.getFrameTimeSeconds() * multiplier;
			} else {
				moveAt = 0;
			}

			if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
				position.x += (float) -((speed * speeds) * Math.cos(Math.toRadians(yaw))) * DisplayManager.getFrameTimeSeconds() * multiplier;
				position.z -= (float) ((speed * speeds) * Math.sin(Math.toRadians(yaw))) * DisplayManager.getFrameTimeSeconds() * multiplier;
			} else if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
				position.x -= (float) -((speed * speeds) * Math.cos(Math.toRadians(yaw))) * DisplayManager.getFrameTimeSeconds() * multiplier;
				position.z += (float) ((speed * speeds) * Math.sin(Math.toRadians(yaw))) * DisplayManager.getFrameTimeSeconds() * multiplier;
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