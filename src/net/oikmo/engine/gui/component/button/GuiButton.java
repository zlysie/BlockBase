package net.oikmo.engine.gui.component.button;

import org.lwjgl.util.vector.Vector2f;

public class GuiButton extends AbstractButton {

	public int controlID;
	private Vector2f scale;
    private Vector2f position;	
	
    /**
     * 
     * @param controlID
     * @param position
     * @param scale
     * @param text
     */
	public GuiButton(int controlID, Vector2f position, Vector2f scale, String text) {
		super(position, scale);
		this.controlID = controlID;
		this.position = position;
		this.scale = scale;
		//float x = Math.abs((1+position.x)/2);
		//float y = Math.abs((1-position.y)/2);
		show();
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
	
	@Override
	public void onStartHover(IButton button) {}
	
	@Override
	public void onStopHover(IButton button) {}
	
	@Override
	public void whileHovering(IButton button) {}
	
	@Override
	public void cleanUp() {
	}
	
	@Override
	public void restore() {
	}

}
