package net.oikmo.engine.entity;

import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.inventory.Item;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.main.Main;

public class ItemEntity extends Entity {
	
	private Item item;
	
	public boolean dontTick = false;
	private boolean allowDisabling;
	
	private float lastY = getPosition().y;
	private static float bobOscillate = (float) Math.abs(Math.sin(Main.elapsedTime * 0.045f * (2 * Math.PI))/10);
	
	public ItemEntity(Item item, TexturedModel model, Vector3f position) {
		super(model, position, new Vector3f(), 0.25f);
		this.item = item;
		this.setWhiteOffset(2f);
	}
	
	public static void updateOscillation() {
		bobOscillate = (float) Math.abs(Math.sin(Main.elapsedTime * 0.045f * (2 * Math.PI))/10);
	}
	
	public void tick() {
		this.increaseRotation(0, 0.5f, 0);
		
		if(isOnGround()) {
			this.setPosition(getPosition().x, (lastY+0.2f)+bobOscillate, getPosition().z);
			
			if(!Main.theWorld.anyBlockInSpecificLocation((int)getRoundedPosition().x, (int)getRoundedPosition().y-1, (int)getRoundedPosition().z)) {
				this.motion.y = (float)((double)this.motion.y - 0.005D);
				this.move(0, this.motion.y, 0,1);
			}
		} else {
			lastY = getPosition().y;
			this.motion.y = (float)((double)this.motion.y - 0.005D);
			this.move(this.motion.x, this.motion.y, this.motion.z,1);
		}
		
		if(aabb.intersects(Main.thePlayer.getAABB())) {
			if(allowDisabling) {
				dontTick = true;
			}
		} else {
			allowDisabling  = true;
		}
		
		
	}

	public Item getItem() {
		return item;
	}
}
