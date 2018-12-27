package kd.equilinox.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Contains various methods for File manipulation.
 * 
 * @author Krzysztof Dobrzynski - k.dobrzynski94@gmail.com
 */
public final class FileUtils {
	private static final String EMF_DIR = "EMF";

	public static final String LOGS_DIR = EMF_DIR + "/logs";
	public static final String MODS_DIR = EMF_DIR + "/mods";

	public static final File LOG_FILE;
	public static final File ERROR_FILE;

	private FileUtils() {
	}

	static {
		// Create basic directories
		createDirectoryIfNotExists(LOGS_DIR);
		createDirectoryIfNotExists(MODS_DIR);

		// Create basic files.
		String date = DateUtils.getNow();
		LOG_FILE = new File(LOGS_DIR + "/EMF-log-" + date + ".txt");
		ERROR_FILE = new File(LOGS_DIR + "/EMF-error-" + date + ".txt");

		try {
			if (!LOG_FILE.exists()) {
				LOG_FILE.createNewFile();
			}
			if (!ERROR_FILE.exists()) {
				ERROR_FILE.createNewFile();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void prepareFiles() {
		try {
			System.setOut(new PrintStream(new FileOutputStream(LOG_FILE)));
			System.setErr(new PrintStream(new FileOutputStream(ERROR_FILE)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static File prepareFile(String path) throws IOException {
		File file = new File(path);

		if (!file.exists()) {
			file.createNewFile();
		}

		return file;
	}

	public static void createDirectoryIfNotExists(String path) {
		File file = new File(path);

		if (!file.exists()) {
			file.mkdirs();
		}
	}

	public static File getFrameworkFile() {
		return new File(FileUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath());
	}

	public static File getGameFile() {
		Path currentDir = Paths.get(System.getProperty("user.dir"));
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(currentDir)) {
			File gameFile = null;
			for (Path file : stream) {
				gameFile = file.toFile();
				if (isGameFile(gameFile)) {
					return gameFile;
				}
			}
		} catch (Exception x) {
			System.err.println(x);
		}
		throw new IllegalArgumentException("Cannot find Equilinox game JAR file.");
	}

	public static boolean isGameFile(File file) {
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

	public static boolean backupFile(File file) {
		try {
			// Path for game backup file.
			String path = file.getParent() + "/" + file.getName() + ".emf.backup";

			// Check if backup was created previously
			File backupFile = new File(path);
			if (backupFile.exists()) {
				Logger.info("Backup of the game file already exists.");
				return true;
			}

			// If there is no backup, try to make one.
			Path backupPath = Paths.get(path);
			Files.copy(file.toPath(), backupPath);
			Logger.info("Backup file created at: " + backupPath);
			return true;
		} catch (IOException e) {
			Logger.error("Error when creating backup file.");
			Logger.error(e);
		}
		return false;
	}
}
