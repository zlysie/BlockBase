package net.oikmo.engine.entity;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import com.mojang.minecraft.phys.AABB;

import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.models.CubeModel;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.engine.textures.ModelTexture;
import net.oikmo.engine.world.blocks.Block;
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

	public float pitch = 0;
	public float yaw = 0;
	public float roll = 0;

	private boolean mouseLocked = true;
	private boolean lockInCam;
	
	private MousePicker picker;
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
		this.pitch = rotation.x;
		this.roll = rotation.z;
		this.picker = new MousePicker(this, MasterRenderer.getInstance().getProjectionMatrix());
		this.invisibleTexture = ResourceLoader.loadTexture("textures/transparent");
		this.block = new Entity(new TexturedModel(CubeModel.getRawModel(selectedBlock), new ModelTexture(MasterRenderer.currentTexturePack.getTextureID())), position, new Vector3f(0,0,0), 1.002f);
		mouseLocked = true;
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
		this.position.x = Maths.lerp(this.position.x, position.x, 0.1f);
		this.position.z = Maths.lerp(this.position.z, position.z, 0.1f);
		this.position.y = position.y + heightOffset;
		if(mouseLocked) {
			picker.update();

			picker.distance = picker.BASE_DISTANCE;
			for(int i = 0; i < picker.BASE_DISTANCE; i++) {
				Block block = Main.theWorld.getBlock(picker.getPointRounded(i));
				if(block != null) {
					picker.distance = i;
					break;
				}			
			}

			Vector3f pos = picker.getPoint();
			Maths.roundVector(pos);

			block.setPosition(pos);
			Block thatBlock = Main.theWorld.getBlock(pos);
			if(thatBlock != null) {
				block.setWhiteOffset(2f);
				block.setRawModel(CubeModel.getRawModel(thatBlock));
			} else {
				block.setWhiteOffset(0.5f);
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
			
			if(Main.theWorld.getBlock(block.getRoundedPosition()) == null) {
				block.setTextureID(MasterRenderer.currentTexturePack.getTextureID());
			}
			if(!Main.theWorld.blockHasNeighbours(block.getRoundedPosition())) {
				block.setTextureID(invisibleTexture);
			}
			
			if(Mouse.isButtonDown(1)) {
				if(!mouseClickRight) {
					Vector3f v = new Vector3f(picker.getPointRounded(picker.distance));
					v.y += 1;
					AABB toCheck = new AABB(v.x-0.5f, v.y, v.z-0.5f, v.x+0.5f, v.y+1f, v.z+0.5f);
					if(!Main.thePlayer.getAABB().intersects(toCheck)) {
						Block block1 = Main.theWorld.getBlock(picker.getPointRounded(picker.distance));
						if(block1 == null) {
							if(Main.theWorld.blockHasNeighbours(picker.getPointRounded(picker.distance))) {
								Main.theWorld.setBlock(picker.getPointRounded(picker.distance), selectedBlock);
							}

						} else {
							if(Main.theWorld.blockHasNeighbours(picker.getPointRounded(picker.distance-1))) { 
								Main.theWorld.setBlock(picker.getPointRounded(picker.distance-1), selectedBlock);
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
					Block block = Main.theWorld.getBlock(picker.getPointRounded());
					if(block != null) {
						Vector3f v = new Vector3f(picker.getPointRounded());
						v.y += 1f;
						ItemBlock item = new ItemBlock(block, v);
						Main.theWorld.entities.add(item);
						Main.theWorld.setBlock(picker.getPointRounded(), null);
					}
					mouseClickLeft = true;
				}
			} else {
				mouseClickLeft = false;
			}
			
			if(Mouse.isButtonDown(2)) {
				Block toBeSelected = Main.theWorld.getBlock(block.getRoundedPosition());
				if(toBeSelected != null) {
					index = toBeSelected.getType();
				}
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