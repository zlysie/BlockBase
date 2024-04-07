package net.oikmo.engine.entity;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import com.github.matthewdawsey.collisionres.AABB;

import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.models.CubeModel;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.engine.textures.ModelTexture;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.engine.world.chunk.Chunk;
import net.oikmo.engine.world.chunk.MasterChunk;
import net.oikmo.main.GameSettings;
import net.oikmo.main.Main;
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

	private boolean mouseLocked = true;
	private boolean lockInCam;

	private MasterChunk currentChunk;
	private MousePicker picker;

	private int texturePackTexture;
	private int invisibleTexture;
	private Block selectedBlock = Block.cobble;
	private Entity block;

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
		this.texturePackTexture = MasterRenderer.currentTexturePack.getTextureID();
		this.invisibleTexture = ResourceLoader.loadTexture("textures/transparent");
		this.block = new Entity(new TexturedModel(CubeModel.getRawModel(selectedBlock), new ModelTexture(MasterRenderer.currentTexturePack.getTextureID())), position, new Vector3f(0,0,0), 1.002f);
		mouseLocked = true;
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

					try {
						MasterChunk master = MasterChunk.getChunkFromPosition(chunkPosition);
						if(master != null) {
							currentChunk = master;
						}
					} catch(IndexOutOfBoundsException e) {}

				}
			}
		});

		chunkGetter.setName("Chunk Grabber");
		chunkGetter.start();
	}
	int index = 0;
	float multiplier = 100;
	boolean inventory = false;
	boolean mouseClickLeft = false;
	boolean mouseClickRight = false;
	/**
	 * Fly cam
	 * @param heightOffset 
	 */
	public void update(Vector3f position, float heightOffset) {
		this.position = position;
		this.position.x = position.x;
		this.position.z = position.z;
		
		this.position.y = position.y + heightOffset;
		if(currentChunk != null && mouseLocked) {
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

			try {

				if(!inventory) {
					if(Keyboard.isKeyDown(Keyboard.KEY_EQUALS)) {
						index += 1;
						index = index > Block.blocks.length-1 ? 0 : index;
						inventory = true;
					} else if(Keyboard.isKeyDown(Keyboard.KEY_MINUS)) {
						index -= 1;
						index = index <= -1 ? Block.blocks.length-1 : index;
						inventory = true;
					}
				} else {
					if(!Keyboard.isKeyDown(Keyboard.KEY_EQUALS) && !Keyboard.isKeyDown(Keyboard.KEY_MINUS)) {
						inventory = false;
					}
				}
				if(Block.blocks[index] != null) {
					if(selectedBlock != Block.blocks[index]) {
						selectedBlock = Block.blocks[index];
					}
				}
			} 
			catch(NumberFormatException e) {}
			catch(ArithmeticException e) {}

			if(currentChunk.getBlock(roundedPosition) == null) {
				this.texturePackTexture = MasterRenderer.currentTexturePack.getTextureID();
				block.setTextureID(texturePackTexture);
			} else {
				block.setTextureID(invisibleTexture);
			}

			if(!currentChunk.blockHasNeighbours(block.getPosition())) {
				block.setTextureID(invisibleTexture);
			}
			
			if(Mouse.isButtonDown(1)) {
				if(!mouseClickRight) {
					Vector3f v = new Vector3f(block.getRoundedPosition());
					v.y += 1;
					AABB toCheck = new AABB(v,new Vector3f(v.x+1.f,v.y+.5f,v.z+1.f));
					if(!Main.thePlayer.getAABB().intersects(toCheck)) {
						Block block1 = currentChunk.getBlock(picker.getPointRounded(picker.distance));
						if(block1 == null) {
							if(currentChunk.blockHasNeighbours(picker.getPointRounded(picker.distance))) {
								currentChunk.setBlock(picker.getPointRounded(picker.distance-1), selectedBlock);
							}

						} else {
							if(currentChunk.blockHasNeighbours(picker.getPointRounded(picker.distance-1))) { 
								currentChunk.setBlock(picker.getPointRounded(picker.distance-1), selectedBlock);
							}
							
						}
						mouseClickRight = true;
					}
					
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
		}

		this.move();
	}

	public Block getCurrentlySelectedBlock() {
		return selectedBlock;
	}

	public Entity getSelectedBlock() {
		return block;
	}
	
	public void toggleMouseLock() {
		if(!lockInCam) {
			mouseLocked = !mouseLocked;
			lockInCam = true;
		}
	}
	
	public void setMouseLock(boolean mouseLocked) {
		this.mouseLocked = mouseLocked;
	}
	
	private void move() {
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			toggleMouseLock();
		} else {
			lockInCam = false;
		}

		if(Mouse.isGrabbed() != mouseLocked) {
			Mouse.setGrabbed(mouseLocked);
		}

		if(mouseLocked) {
			pitch -= Mouse.getDY() * GameSettings.sensitivity*2;
			if(pitch < -maxVerticalTurn){
				pitch = -maxVerticalTurn;
			}else if(pitch > maxVerticalTurn){
				pitch = maxVerticalTurn;
			}
			yaw += Mouse.getDX() * GameSettings.sensitivity;
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