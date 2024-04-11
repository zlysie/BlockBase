package net.oikmo.engine.gui.component.slick;

public class GuiCommand {
	
	protected float x, y;
	
	public void invoke() { return; }
	public void invoke(float sliderValue) { return; }
	public void update() { return; }
	
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
}

