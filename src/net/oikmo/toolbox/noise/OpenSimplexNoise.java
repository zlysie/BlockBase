package net.oikmo.toolbox.noise;



/**
 * This is a template class and can be used to start a new processing Library.
 * Make sure you rename this class as well as the name of the example package 'template' 
 * to your own Library naming convention.
 * 
 * (the tag example followed by the name of an example included in folder 'examples' will
 * automatically include the example in the javadoc.)
 *
 * @example Hello 
 */
public class OpenSimplexNoise {

	private OpenSimplexNoiseKS generator;

	private long seed;
	
	/**
	 * Constructs a new OpenSimplexNoise object,
	 * using the system's current time as the noise seed.
	 */
	public OpenSimplexNoise() {
		this(System.currentTimeMillis());
	}

	/**
	 * Constructs a new OpenSimplexNoise object,
	 * using the provided value as the noise seed.
	 */
	public OpenSimplexNoise(long seed) {
		generator = new OpenSimplexNoiseKS(seed);
		this.seed = seed;
	}

	private double remap(double val) {
		return (val + 1) / 2;
	}

	public float noise (float xoff) {
		return this.noise(xoff, 0);
	}

	public float noise (float xoff, float yoff) {
		return (float) remap(generator.eval(xoff, yoff));
	}

	public float noise (float xoff, float yoff, float zoff) {
		return (float) remap(generator.eval(xoff, yoff, zoff));
	}

	public float noise (float xoff, float yoff, float zoff, float uoff) {
		return (float) remap(generator.eval(xoff, yoff, zoff, uoff));
	}
	
	public long getSeed() {
		return seed;
	}
}