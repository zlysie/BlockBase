package net.oikmo.engine.gui.component.slider;

import org.lwjgl.util.vector.Vector2f;

import net.oikmo.engine.gui.component.button.IButton;

public class GuiSlider  extends AbstractSlider  {
	public int controlID;
	private Vector2f scale;
    private Vector2f position;
    public String displayString;
	
	public GuiSlider(int controlID, float defaultValue, Vector2f position, Vector2f scale, String text) {
		
		super(defaultValue, position, scale);
		this.controlID = controlID;
		this.position = position;
		this.scale = scale;
		//float x = Math.abs((position.x+1)/2);
		//float y = Math.abs((position.y-1)/2);
		show();
	}
	
	@Override
	public void update2() {
		if(this.isLocked() || this.isHovering()) {
			//this.text.setColour(0.75f, 0.75f, 0);
		} else {
			//this.text.setColour(1, 1, 1);
		}
	}
	
	public Vector2f getScale() {
		return scale;
	}

	public void setScale(Vector2f scale) {
		this.scale = scale;
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(Vector2f position) {
		this.position = position;
	}
	
	public int getControlID() {
		return controlID;
	}
	
	public String getDisplayString() {
		return displayString;
	}
	
	@Override
	public void onStartHover(IButton button) {}
	
	@Override
	public void onStopHover(IButton button) {}
	
	@Override
	public void whileHovering(IButton button) {}
	
	@Override
	public void cleanUp() {}
	
	@Override
	public void restore() {}

}
