package net.oikmo.engine.entity;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import com.mojang.minecraft.phys.AABB;

import net.oikmo.engine.renderers.MasterRenderer;
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
	private Block selectedBlock = Block.cobble;
	private TargetedAABB aabb;
	
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
		this.aabb = new TargetedAABB(new Vector3f());
		mouseLocked = true;
	}
	
	public Camera() {
		this.position = new Vector3f();
		mouseLocked = false;
	}
	
	int index = 0;
	float multiplier = 100;
	boolean inventory = false;
	boolean mouseClickLeft = false;
	boolean mouseClickRight = false;
	boolean shouldRenderAABB = false;
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
			
			
			if(Mouse.isButtonDown(1)) {
				if(!mouseClickRight) {
					Block block1 = Main.theWorld.getBlock(picker.getPointRounded());
					if(block1 == null) {
						if(Main.theWorld.blockHasNeighbours(picker.getPointRounded())) {
							Main.theWorld.setBlock(picker.getPointRounded(), selectedBlock);
						}
					} else {
						if(Main.theWorld.blockHasNeighbours(picker.getPointRounded(picker.distance-1))) {
							Main.theWorld.setBlock(picker.getPointRounded(picker.distance-1), selectedBlock);
						}
					}
					
					mouseClickRight = true;
				}
			} else {
				mouseClickRight = false;
			}

			if(Mouse.isButtonDown(0)) {
				if(!mouseClickLeft) {
					Block block = Main.theWorld.getBlock(picker.getPointRounded());
					if(block != null) {
						//Vector3f v = new Vector3f(picker.getPointRounded());
						//v.y += 1f;
						//ItemBlock item = new ItemBlock(block, v);
						//Main.theWorld.addEntity(item);
						Main.theWorld.setBlock(picker.getPointRounded(), null);
					}
					mouseClickLeft = true;
				}
			} else {
				mouseClickLeft = false;
			}
			
			if(Mouse.isButtonDown(2)) {
				Block toBeSelected = Main.theWorld.getBlock(picker.getPointRounded());
				if(toBeSelected != null) {
					index = toBeSelected.getType();
				}
			}
			
			Block thatBlock = Main.theWorld.getBlock(picker.getPointRounded());
			if(thatBlock != null) {
				aabb.setPosition(picker.getPointRounded());
				shouldRenderAABB = true;
			} else {
				shouldRenderAABB = false;
			}
			
		}

		this.move();
	}

	public Block getCurrentlySelectedBlock() {
		return selectedBlock;
	}
	
	public TargetedAABB getAABB() {
		return aabb;
	}
	
	public void toggleMouseLock() {
		if(!lockInCam) {
			mouseLocked = !mouseLocked;
			lockInCam = true;
		}
	}
	
	public void setMouseLock(boolean mouseLocked) {
		this.mouseLocked = mouseLocked;
		if(Mouse.isGrabbed() != mouseLocked) {
			Mouse.setGrabbed(mouseLocked);
		}
	}
	
	private void move() {
		/*if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			toggleMouseLock();
		} else {
			lockInCam = false;
		}*/

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
	
	public boolean shouldRenderAABB() {
		return shouldRenderAABB;
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
	
	public Vector3f getRotation() {
		return new Vector3f(pitch,yaw,roll);
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

	public boolean isLocked() {
		return mouseLocked;
	}
	
	public static class TargetedAABB {
		
		private AABB aabb;
		private Vector3f position;
		
		public TargetedAABB(Vector3f position) {
			this.position = position;
			float x = this.position.x;
			float y = this.position.y;
			float z = this.position.z;
			this.aabb = new AABB(x - 0.5f, y - 0.5f, z - 0.5f, x + 0.5f, y + 0.5f, z + 0.5f);
		}
		
		public void setPosition(float x, float y, float z) {
			this.position.x = x;
			this.position.y = y	;
			this.position.z = z;
			this.aabb = new AABB(x - 0.5f, y - 0.5f, z - 0.5f, x + 0.5f, y + 0.5f, z + 0.5f);
		}
		
		public void setPosition(Vector3f position) {
			this.position = position;
			float x = this.position.x;
			float y = this.position.y;
			float z = this.position.z;
			this.aabb = new AABB(x - 0.5f, y - 0.5f,z - 0.5f, x + 0.5f, y + 0.5f,z + 0.5f);
		}

		public AABB getAABB() {
			return aabb;
		}
		
		public Vector3f getPosition() {
			return position;
		}
	}
}