package net.oikmo.toolbox.noise;



/**
 * I DIDN'T TAKE THIS FROM GITHUB WTFDYM
 * 
 * @author i forgor ;-;
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