package net.oikmo.engine.entity;

import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import com.mojang.minecraft.phys.AABB;

import net.oikmo.engine.DisplayManager;
import net.oikmo.engine.inventory.Item;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.main.GameSettings;
import net.oikmo.main.Main;
import net.oikmo.toolbox.FastMath;
import net.oikmo.toolbox.Maths;

/**
 * Camera class. Allows the player to see the world.
 * 
 * @author Oikmo
 */
public class Camera {
	/** The limit of looking up or down */
	private int maxVerticalTurn = 90;
	/** Sound listener reasons (stores camera's previous position) */
	public Vector3f prevPosition;
	/** Camera's current position */
	private Vector3f position;
	
	/** Pitch of the camera */
	public float pitch = 0;
	/** Yaw of the camera */
	public float yaw = 0;
	/** Roll of the camera */
	public float roll = 0;
	
	/** Previous yaw of the camera */
	public float prevYaw;
	
	/** Dictates whether or not the mouse should be locked */
	private boolean mouseLocked = true;
	/** Action locker to prevent the action being done multiple times each frame */
	private boolean lockInCam;
	
	/** For currently selected blocks */
	private TargetedAABB aabb;
	
	/** For block picking */
	public Vector3f forward;
	
	/** Third person or not */
	private boolean perspective = false;
	
	/** How far should the camera be from the player if in third person */
	public float distanceFromPlayer = 5;
	/** X rotation around the player */
	public float angleAroundPlayer = 0;
	
	/** Action locker to prevent the action being done multiple times each frame */
	private boolean mouseClickLeft = false;
	/** Action locker to prevent the action being done multiple times each frame */
	private boolean mouseClickRight = false;
	/** Action locker to prevent the action being done multiple times each frame */
	private boolean perspectiveLock = false;
	/** Only true if the player is in first person and has a line of sight at a block */
	private boolean shouldRenderAABB = false;	
	
	/**
	 * Camera constructor. Sets position and rotation.
	 * @param position Where camera should be at
	 * @param rotation Where camera should look at
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
	
	/**
	 * Instantiates a camera at [0,0,0] without locking the mouse
	 */
	public Camera() {
		this.position = new Vector3f();
		mouseLocked = false;
	}
	
	/**
	 * Attaches to the player
	 * @param player Player to attach to
	 */
	public void update(Player player) {
		if(Keyboard.isKeyDown(Keyboard.KEY_F5)) {
			if(!perspectiveLock) {
				perspective = !perspective;
				perspectiveLock = true;
			}
		} else {
			perspectiveLock = false;
		}
		
		if(!perspective) {
			Vector3f position = new Vector3f(player.getPosition());
			this.position = position;
			this.prevPosition = new Vector3f(position);
			this.position.x = Maths.lerp(this.position.x, position.x, 0.1f);
			this.position.z = Maths.lerp(this.position.z, position.z, 0.1f);
			this.position.y = position.y + player.heightOffset;
			int reachDistance = player.reachDistance;
			this.prevYaw = yaw;
			if(mouseLocked && Main.theWorld != null && Main.thePlayer.tick) {
				this.updateForwardVector();
				
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
								if(block.getByteType() == Block.tnt.getType() && Main.theNetwork == null) {
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
		}
			
		this.move(player);
	}
	
	/**
	 * Updates the {@link #forward} vector for picking blocks
	 */
	private void updateForwardVector() {
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
	
	/**
	 * Returns the selected block's bounding box
	 * @return {@link TargetedAABB}
	 */
	public TargetedAABB getAABB() {
		return aabb;
	}
	
	/**
	 * Is the player in third person or not
	 * @return {@link Boolean}
	 */
	public boolean isPerspective() {
		return perspective;
	}
	
	/**
	 * Toggles the locking of the mouse
	 */
	public void toggleMouseLock() {
		if(!lockInCam) {
			mouseLocked = !mouseLocked;
			lockInCam = true;
		}
	}
	
	/**
	 * Sets the {@link #mouseLocked} variable directly (and sets the lock state of mouse)
	 * @param mouseLocked Should the mouse be locked?
	 */
	public void setMouseLock(boolean mouseLocked) {
		this.mouseLocked = mouseLocked;
		if(Mouse.isGrabbed() != mouseLocked) {
			Mouse.setGrabbed(mouseLocked);
		}
	}
	
	/**
	 * Despite the name, it doesn't move, rather it calculates limits and the perspective positions
	 * @param player Player to check against.
	 */
	private void move(Player player) {
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
			
			if(perspective) {
				
				calculateAngleAroundPlayer();
				float horizontalDistance = calculateHorizontalDistance();
				float verticalDistance = calculateVerticalDistance();
				calculateCameraPosition(player, horizontalDistance, verticalDistance);
				shouldRenderAABB = false;
			}
			
			yaw += Mouse.getDX() * GameSettings.sensitivity;
		} else {
			shouldRenderAABB = false;
		}
	}

	/**
	 * Moves camera based on given values.
	 * @param dx Distance X to move by
	 * @param dy Distance Y to move by
	 * @param dz Distance Z to move by
	 */
	public void increasePosition(float dx, float dy, float dz) {
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
	}
	/**
	 * Rotates the camera based on given values.
	 * @param dx Rotational X to move by
	 * @param dy Rotational Y to move by
	 * @param dz Rotational Z to move by
	 */
	public void increaseRotation(float dx, float dy, float dz) {
		this.pitch += dx;
		this.yaw += dy; 
		this.roll += dz;
	}
	/**
	 * Sets the rotation of the camera based on given values.
	 * @param dx Rotational X to set to
	 * @param dy Rotational Y to set to
	 * @param dz Rotational Z to set to
	 */
	public void setRotation(float dx, float dy, float dz) {
		this.pitch = dx;
		this.yaw = dy;
		this.roll = dz;
	}
	
	/** 
	 * If camera is looking at a valid block, this returns true (allows rendering of the outline)
	 * @return {@link Boolean}
	 */
	public boolean shouldRenderAABB() {
		return shouldRenderAABB;
	}

	/**
	 * Sets position to given 3D Vector
	 * @param v Vector to set position to
	 */
	public void setPosition(Vector3f v) {
		this.position = v;
	}
	
	/**
	 * Returns camera position
	 * @return {@link Vector3f}
	 */
	public Vector3f getPosition() {
		return position;
	}
	
	/**
	 * Returns camera's look rotation
	 * @return {@link Vector3f}
	 */
	public Vector3f getRotation() {
		return new Vector3f(pitch,yaw,roll);
	}

	/**
	 * Returns the {@link #pitch} of the camera
	 * @return {@link Float}
	 */
	public float getPitch() {
		return pitch;
	}
	/**
	 * Returns the {@link #yaw} of the camera
	 * @return {@link Float}
	 */
	public float getYaw() {
		return yaw;
	}
	/**
	 * Returns the {@link #roll} of the camera
	 * @return {@link Float}
	 */
	public float getRoll() {
		return roll;
	}
	
	/**
	 * Locked in window or not
	 * @return {@link Boolean}
	 */
	public boolean isLocked() {
		return mouseLocked;
	}
	
	/**
	 * Calculates the third person position of the camera from the player
	 * @param player Player to rotate around
	 * @param horizontalDistance Given from {@link #calculateHorizontalDistance()}
	 * @param verticalDistance Given from {@link #calculateVerticalDistance()}
	 */
	private void calculateCameraPosition(Player player, float horizontalDistance, float verticalDistance){
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(angleAroundPlayer)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(angleAroundPlayer)));
		position.x = Maths.lerp(position.x, player.getPosition().x - offsetX, 20f * DisplayManager.getFrameTimeSeconds());
		position.z = Maths.lerp(position.z, player.getPosition().z - offsetZ, 20f * DisplayManager.getFrameTimeSeconds());
		position.y = Maths.lerp(position.y, player.getPosition().y + verticalDistance + player.heightOffset, 20f * DisplayManager.getFrameTimeSeconds());
		this.yaw = 180 - angleAroundPlayer;
	}
	
	/**
	 * Returns the vertical {@link #distanceFromPlayer} using trigonometry (AAAH)
	 * @return {@link Float}
	 */
	private float calculateVerticalDistance(){
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch+2)));
	}
	
	/**
	 * Returns the horizontal {@link #distanceFromPlayer} using trigonometry (AAAH)
	 * @return {@link Float}
	 */
	private float calculateHorizontalDistance(){
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch+2)));
	}
	
	/**
	 * Calculates X Rotation around the player
	 */
	private void calculateAngleAroundPlayer() {
		float angleChange = Mouse.getDX() * GameSettings.sensitivity;
		angleAroundPlayer -= angleChange;
	}
	
	/**
	 * Easy bounding box class to render the block outline 
	 * @author Oikmo
	 */
	public static class TargetedAABB {
		/** Actual bounding box */
		private AABB aabb;
		/** Position of bounding box */
		private Vector3f position;
		
		/**
		 * Calculates the bounding box from given position (has a size of 1)
		 * @param position Position to calculate from
		 */
		public TargetedAABB(Vector3f position) {
			this.position = position;
			float x = this.position.x;
			float y = this.position.y;
			float z = this.position.z;
			this.aabb = new AABB(x, y, z, x + 1f, y + 1f, z + 1f);
		}
		
		/**
		 * Sets the position and calculates the bounding box from given position
		 * @param position Position to set to and calculate from
		 */
		public void setPosition(Vector3f position) {
			this.position = position;
			float x = this.position.x;
			float y = this.position.y;
			float z = this.position.z;
			this.aabb = new AABB(x, y, z, x + 1f, y + 1f, z + 1f);
		}
		
		/**
		 * Returns the bounding box
		 * @return {@link AABB}
		 */
		public AABB getAABB() {
			return aabb;
		}
		
		/**
		 * Returns the position
		 * @return {@link Vector3f}
		 */
		public Vector3f getPosition() {
			return position;
		}
	}
}