package net.oikmo.engine.entity;

import java.util.Random;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import com.mojang.minecraft.phys.AABB;

import net.oikmo.engine.DisplayManager;
import net.oikmo.engine.inventory.Item;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.engine.world.World;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.main.GameSettings;
import net.oikmo.main.Main;
import net.oikmo.toolbox.FastMath;
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
	
	private TargetedAABB aabb;
	private World world;
	
	public Vector3f forward;
	public Vector3f right;
	public Vector3f up;
	
	Thread blockPicker;
	
	Camera camera;
	
	boolean yeah;
	
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
		this.right = new Vector3f();
		this.up = new Vector3f();
		mouseLocked = true;
		world = Main.theWorld;
		camera = this;
		blockPicker = new Thread(new Runnable() {
			public void run() {
				while(DisplayManager.activeDisplay) {
					//System.out.println(picker.distance);
					yeah = Maths.raycast(camera);
					
				}
			}
		});
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
	
	
	private Vector3f currentPoint = null;
	
	
	
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
			this.updateVectors();
			if(world == null) {
				world = Main.theWorld;
				blockPicker.start();
			}
			
			
			
			currentPoint = Maths.getCurrentVoxelPosition();
			
			int blockX = (int)(currentPoint.x);
			int blockY = (int)(currentPoint.y);
			int blockZ = (int)(currentPoint.z);
			
			System.out.println("blockPos[X="+blockX+",Y="+blockY+",Z="+blockZ+"] " + yeah);
			
			Vector3f blockPos = new Vector3f(blockX,blockY,blockZ);
			
			if(Mouse.isButtonDown(1)) {
				if(!mouseClickRight) {
					if(Main.inGameGUI.getSelectedItem() != null) {
						if(Main.theWorld.blockHasNeighbours(blockPos)) {
							Main.theWorld.setBlock(blockPos, Main.inGameGUI.getSelectedItem());
						}
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
						int x = blockX;
						int y = blockY;
						int z = blockZ;
						
						Main.theWorld.setBlock(blockPos, null);
						if(block.getByteType() == Block.tnt.getType()) {
							Main.theWorld.addEntity(new PrimedTNT(new Vector3f(x,y,z), new Random().nextInt(10)/10f, 0.1f, new Random().nextInt(10)/10f, true));
						}
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
			
		}

		this.move();
	}
	
	private void updateVectors() {
		this.forward.x = FastMath.cos(yaw) * FastMath.cos(pitch);
		this.forward.y = FastMath.sin(pitch);
		this.forward.z = FastMath.sin(yaw) * FastMath.cos(pitch);
		this.forward.normalise();
		
		Vector3f crossForward = new Vector3f();
		Vector3f.cross(forward, new Vector3f(0,1,0), crossForward);
		crossForward.normalise(right);
		Vector3f crossRF = new Vector3f();
		Vector3f.cross(right, forward, crossRF);
		crossRF.normalise(this.up);
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
	
	/**
	 * from settings import *
from meshes.chunk_mesh_builder import get_chunk_index


class VoxelHandler:
    def __init__(self, world):
        self.app = world.app
        self.chunks = world.chunks

        # ray casting result
        self.chunk = None
        self.voxel_id = None
        self.voxel_index = None
        self.voxel_local_pos = None
        self.voxel_world_pos = None
        self.voxel_normal = None

        self.interaction_mode = 0  # 0: remove voxel   1: add voxel
        self.new_voxel_id = 1

    def add_voxel(self):
        if self.voxel_id:
            # check voxel id along normal
            result = self.get_voxel_id(self.voxel_world_pos + self.voxel_normal)

            # is the new place empty?
            if not result[0]:
                _, voxel_index, _, chunk = result
                chunk.voxels[voxel_index] = self.new_voxel_id
                chunk.mesh.rebuild()

                # was it an empty chunk
                if chunk.is_empty:
                    chunk.is_empty = False

    def rebuild_adj_chunk(self, adj_voxel_pos):
        index = get_chunk_index(adj_voxel_pos)
        if index != -1:
            self.chunks[index].mesh.rebuild()

    def rebuild_adjacent_chunks(self):
        lx, ly, lz = self.voxel_local_pos
        wx, wy, wz = self.voxel_world_pos

        if lx == 0:
            self.rebuild_adj_chunk((wx - 1, wy, wz))
        elif lx == CHUNK_SIZE - 1:
            self.rebuild_adj_chunk((wx + 1, wy, wz))

        if ly == 0:
            self.rebuild_adj_chunk((wx, wy - 1, wz))
        elif ly == CHUNK_SIZE - 1:
            self.rebuild_adj_chunk((wx, wy + 1, wz))

        if lz == 0:
            self.rebuild_adj_chunk((wx, wy, wz - 1))
        elif lz == CHUNK_SIZE - 1:
            self.rebuild_adj_chunk((wx, wy, wz + 1))

    def remove_voxel(self):
        if self.voxel_id:
            self.chunk.voxels[self.voxel_index] = 0

            self.chunk.mesh.rebuild()
            self.rebuild_adjacent_chunks()

    def set_voxel(self):
        if self.interaction_mode:
            self.add_voxel()
        else:
            self.remove_voxel()

    def switch_mode(self):
        self.interaction_mode = not self.interaction_mode

    def update(self):
        self.ray_cast()

    def ray_cast(self):
        # start point
        x1, y1, z1 = self.app.player.position
        # end point
        x2, y2, z2 = self.app.player.position + self.app.player.forward * MAX_RAY_DIST

        current_voxel_pos = glm.ivec3(x1, y1, z1)
        self.voxel_id = 0
        self.voxel_normal = glm.ivec3(0)
        step_dir = -1

        dx = glm.sign(x2 - x1)
        delta_x = min(dx / (x2 - x1), 10000000.0) if dx != 0 else 10000000.0
        max_x = delta_x * (1.0 - glm.fract(x1)) if dx > 0 else delta_x * glm.fract(x1)

        dy = glm.sign(y2 - y1)
        delta_y = min(dy / (y2 - y1), 10000000.0) if dy != 0 else 10000000.0
        max_y = delta_y * (1.0 - glm.fract(y1)) if dy > 0 else delta_y * glm.fract(y1)

        dz = glm.sign(z2 - z1)
        delta_z = min(dz / (z2 - z1), 10000000.0) if dz != 0 else 10000000.0
        max_z = delta_z * (1.0 - glm.fract(z1)) if dz > 0 else delta_z * glm.fract(z1)

        while not (max_x > 1.0 and max_y > 1.0 and max_z > 1.0):

            result = self.get_voxel_id(voxel_world_pos=current_voxel_pos)
            if result[0]:
                self.voxel_id, self.voxel_index, self.voxel_local_pos, self.chunk = result
                self.voxel_world_pos = current_voxel_pos

                if step_dir == 0:
                    self.voxel_normal.x = -dx
                elif step_dir == 1:
                    self.voxel_normal.y = -dy
                else:
                    self.voxel_normal.z = -dz
                return True

            if max_x < max_y:
                if max_x < max_z:
                    current_voxel_pos.x += dx
                    max_x += delta_x
                    step_dir = 0
                else:
                    current_voxel_pos.z += dz
                    max_z += delta_z
                    step_dir = 2
            else:
                if max_y < max_z:
                    current_voxel_pos.y += dy
                    max_y += delta_y
                    step_dir = 1
                else:
                    current_voxel_pos.z += dz
                    max_z += delta_z
                    step_dir = 2
        return False

    def get_voxel_id(self, voxel_world_pos):
        cx, cy, cz = chunk_pos = voxel_world_pos / CHUNK_SIZE

        if 0 <= cx < WORLD_W and 0 <= cy < WORLD_H and 0 <= cz < WORLD_D:
            chunk_index = cx + WORLD_W * cz + WORLD_AREA * cy
            chunk = self.chunks[chunk_index]

            lx, ly, lz = voxel_local_pos = voxel_world_pos - chunk_pos * CHUNK_SIZE

            voxel_index = lx + CHUNK_SIZE * lz + CHUNK_AREA * ly
            voxel_id = chunk.voxels[voxel_index]

            return voxel_id, voxel_index, voxel_local_pos, chunk
        return 0, 0, 0, 0

	 */
}