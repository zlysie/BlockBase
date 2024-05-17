package net.oikmo.engine.entity;

import java.util.Random;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import com.mojang.minecraft.phys.AABB;

import net.oikmo.engine.inventory.Item;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.main.GameSettings;
import net.oikmo.main.Main;
import net.oikmo.toolbox.FastMath;
import net.oikmo.toolbox.Maths;

/**
 * Camera class. Allows the player to see the world.
 * 
 * @author <i>Oikmo</i>
 */
public class Camera {
	private int maxVerticalTurn = 90;
	public Vector3f prevPosition;
	private Vector3f position;

	public float pitch = 0;
	public float prevYaw;
	public float yaw = 0;
	public float roll = 0;

	private boolean mouseLocked = true;
	private boolean lockInCam;
	
	private TargetedAABB aabb;
	
	public Vector3f forward;
	
	/**
	 * Camera constructor. Sets position and rotation.
	 * 
	 * @param position
	 * @param rotation
	 * @param scale
	 */
	public Camera(Vector3f position, Vector3f rotation) {
		this.position = position;
		this.prevPosition = position;
		this.pitch = rotation.x;
		this.roll = rotation.z;
		this.aabb = new TargetedAABB(new Vector3f());
		this.forward = new Vector3f();
		mouseLocked = true;
	}
	
	public Camera() {
		this.position = new Vector3f();
		mouseLocked = false;
	}
	
	float multiplier = 100;
	boolean inventory = false;
	boolean mouseClickLeft = false;
	boolean mouseClickRight = false;
	boolean shouldRenderAABB = false;
	
	/**
	 * Fly cam
	 * @param heightOffset 
	 */
	public void update(Player player) {
		Vector3f position = new Vector3f(player.getPosition());
		this.position = position;
		this.prevPosition = new Vector3f(position);
		this.position.x = Maths.lerp(this.position.x, position.x, 0.1f);
		this.position.z = Maths.lerp(this.position.z, position.z, 0.1f);
		this.position.y = position.y + player.heightOffset;
		int reachDistance = player.reachDistance;
		this.prevYaw = yaw;
		if(mouseLocked && Main.theWorld != null && Main.thePlayer.tick) {
			this.updateVectors();
			
			Vector3f currentPoint = Main.theWorld.raycast(getPosition(), forward, reachDistance, false);  
			
			if(currentPoint != null) {
				int blockX = (int)(currentPoint.x);
				int blockY = (int)(currentPoint.y);
				int blockZ = (int)(currentPoint.z);
				Vector3f blockPos = new Vector3f(blockX,blockY,blockZ);
				
				if(Mouse.isButtonDown(1)) {
					if(!mouseClickRight) {
						if(Main.inGameGUI.getSelectedItem() != null) {
							Vector3f point = Main.theWorld.raycast(getPosition(), forward, reachDistance, true);
							int bx = (int)(point.x);
							int by = (int)(point.y);
							int bz = (int)(point.z);
							Main.theWorld.setBlock(new Vector3f(bx,by,bz), Main.inGameGUI.getSelectedItem());
						}
						mouseClickRight = true;
					}
				} else {
					mouseClickRight = false;
				}

				if(Mouse.isButtonDown(0)) {
					if(!mouseClickLeft) {
						Block block = Main.theWorld.getBlock(blockPos);
						if(block != null) {
							/*Vector3f v = new Vector3f(picker.getPointRounded());
							v.y += 1f;
							ItemBlock item = new ItemBlock(block, v);
							Main.theWorld.addEntity(item);*/
							Vector3f pos = new Vector3f(currentPoint);
							if(block.getByteType() == Block.tnt.getType()) {
								Main.theWorld.addEntity(new PrimedTNT(pos, new Random().nextInt(10)/10f, 0.1f, new Random().nextInt(10)/10f, true));
							}
							Main.theWorld.setBlock(blockPos, null);
							
						}
						mouseClickLeft = true;
					}
				} else {
					mouseClickLeft = false;
				}
				
				if(Mouse.isButtonDown(2)) {
					Block toBeSelected = Main.theWorld.getBlock(blockPos);
					if(toBeSelected != null) {
						Main.inGameGUI.setSelectedItem(Item.blockToItem(toBeSelected));
					}
				}
				
				Block thatBlock = Main.theWorld.getBlock(blockPos);
				if(thatBlock != null) {
					aabb.setPosition(blockPos);
					shouldRenderAABB = true;
				} else {
					shouldRenderAABB = false;
				}
				
			} else {
				shouldRenderAABB = false;
			}

		}
			
		this.move();
	}
	
	private void updateVectors() {
		float yaw = (float) Math.toRadians(this.yaw + 90);
		float pitch = (float) Math.toRadians(this.pitch);
		this.forward.x = FastMath.cos(yaw) * FastMath.cos(pitch);
		this.forward.y = FastMath.sin(pitch);
		this.forward.z = FastMath.sin(yaw) * FastMath.cos(pitch);
		forward = (Vector3f)forward.normalise();
		forward.x *= -1;
		forward.y *= -1;
		forward.z *= -1;
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
			this.aabb = new AABB(x, y, z, x + 1f, y + 1f, z + 1f);
		}
		
		public void setPosition(Vector3f position) {
			this.position = position;
			float x = this.position.x;
			float y = this.position.y;
			float z = this.position.z;
			this.aabb = new AABB(x, y, z, x + 1f, y + 1f, z + 1f);
		}

		public AABB getAABB() {
			return aabb;
		}
		
		public Vector3f getPosition() {
			return position;
		}
	}
}