package kd.equilinox.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Contains various methods for File manipulation.
 * 
 * @author Krzysztof Dobrzynski - k.dobrzynski94@gmail.com
 */
public final class FileUtils {
	private static final String EMF_DIR = "EMF";

	public static final String LOGS_DIR = EMF_DIR + "/logs";
	public static final String CRASH_DIR = EMF_DIR + "/crashes";
	public static final String MODS_DIR = EMF_DIR + "/mods";

	public static final File LOG_FILE;
	public static final File ERROR_FILE;

	private FileUtils() {
	}

	static {
		// Create basic directories
		createDirectoryIfNotExists(LOGS_DIR);
		createDirectoryIfNotExists(CRASH_DIR);
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

	public static File handleCrash(Exception exception) throws IOException {
		String date = DateUtils.getNow();
		File errorFile = prepareFile(LOGS_DIR + "/EMF-crash-" + date + ".txt");

		PrintStream ps = new PrintStream(errorFile);
		exception.printStackTrace(ps);

		return errorFile;
	}

	public static void prepareFiles() {
		try {
			System.setOut(new PrintStream(new FileOutputStream(LOG_FILE)));
			System.setErr(new PrintStream(new FileOutputStream(ERROR_FILE)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static File prepareFile(String path) throws IOException {
		File file = new File(path);

		if (!file.exists()) {
			file.createNewFile();
		}

		return file;
	}

	private static void createDirectoryIfNotExists(String path) {
		File file = new File(path);

		if (!file.exists()) {
			file.mkdirs();
		}
	}
}
