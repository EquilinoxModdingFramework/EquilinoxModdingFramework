package kd.equilinox.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import kd.equilinox.utils.FileUtils;
import kd.equilinox.utils.Logger;

/**
 * Start point of the framework which is responsible for running game with
 * custom agent.
 * 
 * @author Krzysztof Dobrzynski - k.dobrzynski94@gmail.com
 */
public class Main {
	public static void main(String[] args) {
		File frameworkFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String fileName = frameworkFile.getName();
		String gameFileName = getGameFileName();
		String command = "java -javaagent:" + fileName + " -jar " + gameFileName;

		Logger.info(command);

		try {
			Logger.info("Starting game...");
			Runtime.getRuntime().exec(command, null, null);
			Logger.info("Game started.");
		} catch (IOException exception) {
			try {
				FileUtils.handleCrash(exception);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static String getGameFileName() {
		Path currentDir = Paths.get(System.getProperty("user.dir"));
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(currentDir)) {
			File gameFile = null;
			for (Path file : stream) {
				gameFile = file.toFile();
				if (isGameFile(gameFile)) {
					return gameFile.getName();
				}
			}
		} catch (Exception x) {
			System.err.println(x);
		}
		throw new IllegalArgumentException("Cannot find Equilinox game JAR file.");
	}

	private static boolean isGameFile(File file) {
		if (!file.isFile()) {
			return false;
		}

		String fileName = file.getName();

		// EMF will contains "-" in it's name and will also end with ".jar".
		if (fileName.endsWith(".jar") && !fileName.contains("-") && fileName.contains("Equilinox")) {
			return true;
		}

		return false;
	}
}
