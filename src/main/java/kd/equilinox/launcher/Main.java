package kd.equilinox.launcher;

import java.io.File;

import javax.swing.JOptionPane;

import kd.equilinox.launcher.patches.GamePatcher;
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

	public static void main(String[] args) {
		findFiles();
		backupGameFile();
		patchGame();
		exitLauncher();
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

	private static void exitLauncher() {
		JOptionPane.showMessageDialog(null, "Game patched successfully.", "Equilinox Modding Framework",
				JOptionPane.INFORMATION_MESSAGE);
	}
}
