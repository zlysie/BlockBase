package net.oikmo.engine.entity;

import java.util.Random;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import com.mojang.minecraft.particle.Particle;
import com.mojang.minecraft.phys.AABB;

import net.oikmo.engine.inventory.Item;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.engine.sound.SoundMaster;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.main.GameSettings;
import net.oikmo.main.Main;
import net.oikmo.network.shared.PacketPlaySoundAt;
import net.oikmo.toolbox.Maths;
import net.oikmo.toolbox.MousePicker;

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
	
	private MousePicker picker;
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
		this.prevPosition = position;
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
		this.prevPosition = new Vector3f(position);
		this.position.x = Maths.lerp(this.position.x, position.x, 0.1f);
		this.position.z = Maths.lerp(this.position.z, position.z, 0.1f);
		this.position.y = position.y + heightOffset;
		this.prevYaw = yaw;
		if(mouseLocked && Main.theWorld != null && Main.thePlayer.tick) {
			picker.update();
			
			picker.distance = picker.BASE_DISTANCE;
			for(int i = 0; i < picker.BASE_DISTANCE; i++) {
				Block block = Main.theWorld.getBlock(picker.getPointRounded(i));
				if(block != null) {
					picker.distance = i;
					break;
				}			
			}
			
			int blockX = (int) this.picker.getPointRounded().x;
			int blockY = (int) this.picker.getPointRounded().y;
			int blockZ = (int) this.picker.getPointRounded().z;
			/**/
			
			if(Mouse.isButtonDown(1)) {
				if(this.picker.distance == 0) {
					--blockY;
				}

				if(this.picker.distance == 1) {
					++blockY;
				}

				if(this.picker.distance == 2) {
					--blockZ;
				}

				if(this.picker.distance == 3) {
					++blockZ;
				}

				if(this.picker.distance == 4) {
					--blockX;
				}

				if(this.picker.distance == 5) {
					++blockX;
				}
				if(!mouseClickRight) {
					Block block1 = Main.theWorld.getBlock(picker.getPointRounded());
					if(Main.inGameGUI.getSelectedItem() != null) {
						if(block1 == null) {
							if(Main.theWorld.blockHasNeighbours(picker.getPointRounded())) {
								int x = (int) picker.getPointRounded().x;
								int y = (int) picker.getPointRounded().y;
								int z = (int) picker.getPointRounded().z;
								SoundMaster.playBlockPlaceSFX(Main.inGameGUI.getSelectedItem(), x, y, z);
								if(Main.network != null) {
									PacketPlaySoundAt packet = new PacketPlaySoundAt();
									packet.place = true;
									packet.blockID = Main.inGameGUI.getSelectedItem().getByteType();
									packet.x = x;
									packet.y = y;
									packet.z = z;
									Main.network.client.sendTCP(packet);
								}
								Main.theWorld.setBlock(picker.getPointRounded(), Main.inGameGUI.getSelectedItem());
							}
						} else {
							if(Main.theWorld.blockHasNeighbours(picker.getPointRounded(picker.distance-1))) {
								int x = (int) picker.getPointRounded().x;
								int y = (int) picker.getPointRounded().y;
								int z = (int) picker.getPointRounded().z;
								SoundMaster.playBlockPlaceSFX(Main.inGameGUI.getSelectedItem(), x, y, z);
								if(Main.network != null) {
									PacketPlaySoundAt packet = new PacketPlaySoundAt();
									packet.place = true;
									packet.blockID = Main.inGameGUI.getSelectedItem().getByteType();
									packet.x = x;
									packet.y = y;
									packet.z = z;
									Main.network.client.sendTCP(packet);
								}
								Main.theWorld.setBlock(picker.getPointRounded(picker.distance-1), Main.inGameGUI.getSelectedItem());
							}
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
						/*Vector3f v = new Vector3f(picker.getPointRounded());
						v.y += 1f;
						ItemBlock item = new ItemBlock(block, v);
						Main.theWorld.addEntity(item);*/
						int x = blockX;
						int y = blockY;
						int z = blockZ;
						SoundMaster.playBlockBreakSFX(block, blockX, blockY, blockZ);
						for(int px = 0; px < 4; ++px) {
							for(int py = 0; py < 4; ++py) {
								for(int pz = 0; pz < 4; ++pz) {
									float particleX = (float)x + ((float)px) / (float)4;
									float particleY = (float)y + ((float)py) / (float)4;
									float particleZ = (float)z + ((float)pz) / (float)4;
									Particle particle = new Particle(particleX-0.5f, particleY-0.5f, particleZ-0.5f, particleX - (float)x, particleY - (float)y, particleZ - (float)z, block);
									Main.theWorld.spawnParticle(particle);
								}
							}
						}
						if(Main.network != null) {
							PacketPlaySoundAt packet = new PacketPlaySoundAt();
							packet.blockID = block.getByteType();
							packet.x = x;
							packet.y = y;
							packet.z = z;
							Main.network.client.sendTCP(packet);
						}
						Main.theWorld.setBlock(new Vector3f(blockX,blockY,blockZ), null);
						if(block.getType() == Block.tnt.getType()) {
							Main.theWorld.addEntity(new PrimedTNT(new Vector3f(blockX,blockY,blockZ), new Random().nextInt(10)/10f, 0.1f, new Random().nextInt(10)/10f));
							System.out.println("AAAAAA");
						}
					}
					mouseClickLeft = true;
				}
			} else {
				mouseClickLeft = false;
			}
			
			if(Mouse.isButtonDown(2)) {
				Block toBeSelected = Main.theWorld.getBlock(picker.getPointRounded());
				if(toBeSelected != null) {
					Main.inGameGUI.setSelectedItem(Item.blockToItem(toBeSelected));
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