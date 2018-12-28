package kd.equilinox.utils;

/**
 * Used for logging various things.
 * 
 * @author Krzysztof Dobrzynski - k.dobrzynski94@gmail.com
 */
public final class Logger {
	private Logger() {
	}

	static {
		FileUtils.prepareFiles();
		printFrameworkDetails();
	}

	public static void info(String message) {
		System.out.println("[EMF] " + message);
	}

	public static void error(String message) {
		System.err.println("[EMF] " + message);
	}

	public static void error(Exception exception) {
		exception.printStackTrace(System.err);
	}

	private static void printFrameworkDetails() {
		info("|************************************************************************|");
		info("|                                                                        |");
		info("|               Equilinox Modding Framework  by  Sejoslaw                |");
		info("|                                                                        |");
		info("| https://github.com/EquilinoxModdingFramework/EquilinoxModdingFramework |");
		info("|                                                                        |");
		info("|************************************************************************|");
	}
}
