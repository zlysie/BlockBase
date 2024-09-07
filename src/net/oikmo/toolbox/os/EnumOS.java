package net.oikmo.toolbox.os;

/**
 * Contains all available os' compatible with java.
 * @author Oikmo
 *
 */
public enum EnumOS
{
	/** Linux */
    linux,
    /** Solaris */
    solaris,
    /** Windows */
    windows,
    /** MacOS */
    macos,
    /** No OS */
    unknown;
	
	
	/**
	 * Gets the os type of the user using the java property "os.name".
	 * <br>
	 * OS' it tries to find.
	 * <ul>
	 * <li>Windows (win, {@link EnumOS#windows})</li>
	 * <li>Linux (linux, {@link EnumOS#linux})</li>
	 * <li>Unix (unix, {@link EnumOS#linux})</li>
	 * <li>Mac (mac, {@link EnumOS#macos})</li>
	 * <li>Solaris (solaris, {@link EnumOS#solaris})</li>
	 * <li>Sunos (sunos, {@link EnumOS#unknown})</li>
	 * <li>If it can find one ({@link EnumOS#unknown})</li>
	 * </ul>
	 * @return {@link EnumOS}
	 */
	public static EnumOS getOS() {
		String rawOS = System.getProperty("os.name").toLowerCase();
		return rawOS.contains("win") ? EnumOS.windows : (rawOS.contains("mac") ? EnumOS.linux : (rawOS.contains("solaris") ? EnumOS.solaris : (rawOS.contains("sunos") ? EnumOS.unknown : (rawOS.contains("linux") ? EnumOS.linux : (rawOS.contains("unix") ? EnumOS.linux : EnumOS.unknown)))));
	}
}
