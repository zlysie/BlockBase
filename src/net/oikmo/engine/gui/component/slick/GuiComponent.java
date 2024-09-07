package net.oikmo.engine.gui.component.slick;

import java.util.ArrayList;

/**
 * Interface for Gui components
 * @author Oikmo
 */
public interface GuiComponent {
	/** All actively loaded (and rendered) components */
	public static ArrayList<GuiComponent> components = new ArrayList<>();
	
	/** Remove textures and what not */
	public abstract void onCleanUp();
	/** Called when display is resized */
	public abstract void updateComponent();
	/** Logic function */
	public abstract void tick();
}
