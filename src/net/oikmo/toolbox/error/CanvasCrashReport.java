package net.oikmo.toolbox.error;

import java.awt.Canvas;
import java.awt.Dimension;

/**
 * Spacing for {@link PanelCrashReport}
 * @author Oikmo
 */
public class CanvasCrashReport extends Canvas {
	/** Default serial */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Sets the preferred size and minimum size to the given size
	 * @param size Size to be set to
	 */
	public CanvasCrashReport(int size) {
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));
    }
}
