package net.oikmo.engine.entity;

import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.models.TexturedModel;
import net.oikmo.main.Main;

public class ItemEntity extends Entity {
	
	public boolean dontTick = false;
	private boolean allowDisabling;
	
	private float lastY = 0;
	
	public ItemEntity(TexturedModel model, Vector3f position) {
		super(model, position, new Vector3f(), 0.25f);
	}
	
	private boolean doThatOnce = false;
	
	public void tick() {
		this.increaseRotation(0, 0.5f, 0);
		
		float bobOscillate = (float) Math.abs(Math.sin(Main.elapsedTime * 0.045f * (2 * Math.PI))/10);
		if(onGround) {
			if(!doThatOnce) {
				this.aabb.updatePosition(getRoundedPosition());
				doThatOnce = true;
			}
			this.setPosition(getPosition().x, (lastY+0.2f)+bobOscillate, getPosition().z);
			
		} else {
			doThatOnce = false;
			lastY = getPosition().y;
			this.motion.y = (float)((double)this.motion.y - 0.005D);
			this.move();
		}
		
		if(aabb.intersects(Main.thePlayer.aabb)) {
			if(allowDisabling) {
				dontTick = true;
			}
		} else {
			allowDisabling  = true;
		}
		
	}

}
