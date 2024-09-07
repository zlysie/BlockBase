package net.oikmo.toolbox.error;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Shows the logo of a specified image
 * @author Oikmo
 */
public class CanvasLogo extends Canvas {

	/** Default serial */
	private static final long serialVersionUID = 1L;
	
	/** Logo to render */
	private BufferedImage logo;
	
	/** The multiplier for the size of the image */
	private int scale = -1;
	/** Actual size of the image to be set to */
	private byte size = 100;

	/**
	 * Loads "iconx64" with all default parameters
	 */
	public CanvasLogo() {
		loadImage("iconx64");
	}

	/**
	 * Loads specified image with specified scale and size
	 * @param imagePath Image to load
	 * @param scale Scale to be set to
	 * @param size Size to be set to
	 */
	public CanvasLogo(String imagePath, int scale, byte size) {
		this.scale = scale;
		this.size = size;
		loadImage(imagePath);
	}
	
	/**
	 * Loads the specified image and sets the preferred size and minimum size to the set {@link #size}
	 * @param imagePath Path to image
	 */
	private void loadImage(String imagePath) {
		try {
			logo = ImageIO.read(this.getClass().getResourceAsStream("/assets/" + imagePath + ".png"));
		} catch(IOException e) { }
		
		setPreferredSize(new Dimension(size, size));
		setMinimumSize(new Dimension(size, size));
	}

	public void paint(Graphics g) {
		super.paint(g);
		if(scale == -1) {
			g.drawImage(logo, getWidth() / 2 - logo.getWidth() / 2, 32, null);
		} else {
			g.drawImage(logo, (getWidth() - logo.getWidth()*scale)/2, (getHeight() - logo.getHeight()*scale)/2, logo.getWidth()*scale, logo.getHeight()*scale, null);
		}
	}
}