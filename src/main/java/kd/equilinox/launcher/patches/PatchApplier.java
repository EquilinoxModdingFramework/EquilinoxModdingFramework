package kd.equilinox.launcher.patches;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;

import kd.equilinox.utils.FileUtils;
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
	 * Temporary file into which all patches will be written.
	 */
	private final File tmpFile;
	/**
	 * Classes which has been patched.
	 */
	private final Map<String, byte[]> patchedClasses;

	public PatchApplier(File gameFile, Map<String, byte[]> patchedClasses) {
		this.gameFile = gameFile;
		this.patchedClasses = patchedClasses;
		this.tmpFile = new File(this.gameFile.getAbsolutePath() + ".tmp");
	}

	public void run() {
		this.prepareTemporaryFile();
		this.injectPatchedClasses();
		this.injectEMFClasses();
		this.renameTemporaryFile();
	}

	private void renameTemporaryFile() {
		File afterPatchFile = new File(
				this.gameFile.getAbsolutePath().replace(".jar", " - Patched by Equilinox Modding Framework.jar"));
		this.tmpFile.renameTo(afterPatchFile);
	}

	private void injectEMFClasses() {
		try {
			File frameworkFile = FileUtils.getFrameworkFile();

			// Extract framework files.
			Logger.info("Extracting framework files...");
			int code = Runtime.getRuntime().exec("jar xf " + frameworkFile.getName()).waitFor();
			if (code != 0) {
				Logger.error("Error when unpacking framework file. Code: " + code);
				return;
			}

			// Inject "kd" directory into temporary file.
			Logger.info("Inject framework files into temporary file...");
			code = Runtime.getRuntime().exec("jar uf " + this.gameFile.getName() + ".tmp kd").waitFor();
			if (code != 0) {
				Logger.error("Error when injecting framework files into temporary file. Code: " + code);
				return;
			}

			// Clean
			Logger.info("Post-injection cleaning...");
			this.clear(new File("kd"));
			this.clear(new File("javassist"));
			this.clear(new File("patches.txt"));
			this.clear(new File("META-INF"));
		} catch (Exception e) {
			Logger.error("Error when patching game file.");
			Logger.error(e);
		}
	}

	private void clear(File file) {
		if (file.isDirectory()) {
			for (File innerFile : file.listFiles()) {
				this.clear(innerFile);
			}
		}

		if (!file.delete()) {
			Logger.error("Can't delete file: " + file.getAbsolutePath());
		}
	}

	private void prepareTemporaryFile() {
		// Remove old tmp file.
		if (this.tmpFile.exists()) {
			this.tmpFile.delete();
		}

		// Prepare temporary file.
		Path gameFilePath = this.gameFile.toPath();
		Path tmpFilePath = this.tmpFile.toPath();

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
