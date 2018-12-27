package kd.equilinox.core;

import java.io.File;

import kd.equilinox.core.patches.GamePatcher;
import kd.equilinox.utils.FileUtils;
import kd.equilinox.utils.Logger;

/**
 * Start point of the framework which is responsible for running game with
 * custom agent.
 * 
 * @author Krzysztof Dobrzynski - k.dobrzynski94@gmail.com
 */
public class Main {
	private static File FRAMEWORK_FILE;
	private static File GAME_FILE;
	private static String RUN_GAME_COMMAND;

	public static void main(String[] args) {
		findFiles();
		backupGameFile();
		patchGame();
		buildRunCommand();
		startGame();
	}

	private static void findFiles() {
		FRAMEWORK_FILE = FileUtils.getFrameworkFile();
		Logger.info("Found framework file at: " + FRAMEWORK_FILE.getAbsolutePath());

		GAME_FILE = FileUtils.getGameFile();
		Logger.info("Found game file at: " + GAME_FILE.getAbsolutePath());
	}

	private static void backupGameFile() {
		if (!FileUtils.backupFile(GAME_FILE)) {
			Logger.error("Cannot create backup file.");
			Logger.error("Exiting...");
		}
	}

	private static void patchGame() {
		GamePatcher patcher = new GamePatcher(FRAMEWORK_FILE, GAME_FILE);
		patcher.run();
	}

	private static void buildRunCommand() {
		RUN_GAME_COMMAND = "java -jar " + GAME_FILE.getName();
		Logger.info("Command: '" + RUN_GAME_COMMAND + "'");
	}

	private static void startGame() {
		Logger.info("Starting game...");
//		try {
//			Runtime.getRuntime().exec(RUN_GAME_COMMAND, null, null);
//		} catch (IOException exception) {
//			Logger.error(exception);
//		}
		Logger.info("Game started.");
	}
}
