package kd.equilinox.core.patches;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;

import kd.equilinox.utils.Logger;

/**
 * The purpose of this class is to apply all the patches.
 * 
 * @author Krzysztof Dobrzynski - k.dobrzynski94@gmail.com
 */
public class PatchApplier {
	/**
	 * Equilinox JAR file.
	 */
	private final File gameFile;
	/**
	 * Classes which has been patched.
	 */
	private final Map<String, byte[]> patchedClasses;

	public PatchApplier(File gameFile, Map<String, byte[]> patchedClasses) {
		this.gameFile = gameFile;
		this.patchedClasses = patchedClasses;
	}

	public void run() {
		this.prepareTemporaryFile();
		this.injectPatchedClasses();
		this.injectEMFClasses();
		this.renameTemporaryFile();
	}

	private void renameTemporaryFile() {
		File tmpFile = new File(this.gameFile.getAbsolutePath() + ".tmp");
		File afterPatchFile = new File(this.gameFile.getAbsolutePath().replace(".jar", " - Patched.jar"));
		tmpFile.renameTo(afterPatchFile);
	}

	private void injectEMFClasses() {
		// TODO: Inject EMF classes into temporary file.
	}

	private void prepareTemporaryFile() {
		Path gameFilePath = this.gameFile.toPath();
		Path tmpFilePath = Paths.get(gameFilePath + ".tmp");

		try {
			Files.copy(gameFilePath, tmpFilePath);
		} catch (IOException e) {
			Logger.error("Error when creating temporary file for applying patches.");
			Logger.error(e);
			return;
		}
	}

	private void injectPatchedClasses() {
		for (Entry<String, byte[]> patch : this.patchedClasses.entrySet()) {
			Logger.info("Appling patch for class: " + patch.getKey());
			try {
				this.applyPatch(patch.getKey(), patch.getValue());
			} catch (Exception ex) {
				Logger.error(ex);
			}
			Logger.info("Patch applied.");
		}
	}

	private void applyPatch(String patchedClass, byte[] patchedClassData) throws Exception {
		File patchFile = new File(patchedClass);
		File patchDir = patchFile.getParentFile();

		if (!patchDir.exists()) {
			patchDir.mkdirs();
		}

		if (!patchFile.exists()) {
			patchFile.createNewFile();
		}

		FileOutputStream fos = new FileOutputStream(patchFile);
		fos.write(patchedClassData);
		fos.flush();
		fos.close();

		Logger.info("Patch file created (" + patchedClass + "). Starting injection...");

		String command = "jar uf " + this.gameFile.getName() + ".tmp " + Paths.get(patchedClass);
		Logger.info("Running command: '" + command + "'");
		int code = Runtime.getRuntime().exec(command).waitFor();
		Logger.info("Command finished with code: " + code);

		Logger.info("Removing patch file...");
		// TODO: Remove all folders which were previously created - folder cleaning.
		patchFile.delete();
		patchDir.delete();
		Logger.info("Patch file removed.");
	}
}
