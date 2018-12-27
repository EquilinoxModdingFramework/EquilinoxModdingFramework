package kd.equilinox.core.patches;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import kd.equilinox.utils.Logger;

/**
 * The purpose of this class is to read all patches.
 * 
 * @author Krzysztof Dobrzynski - k.dobrzynski94@gmail.com
 */
public class GamePatcher {
	/**
	 * EMF JAR file.
	 */
	private final File frameworkFile;
	/**
	 * Equilinox JAR file.
	 */
	private final File gameFile;
	/**
	 * Name of the file which contains all the patches.
	 */
	private final String patchesFileName = "patches.txt";

	public GamePatcher(File frameworkFile, File gameFile) {
		this.frameworkFile = frameworkFile;
		this.gameFile = gameFile;
	}

	public void run() {
		Collection<Patch> patches = this.readPatches();
		Logger.info("Processing " + patches.size() + " patches...");

		this.processPatches(patches);
		Logger.info("Game patched.");
	}

	private void processPatches(Collection<Patch> patches) {
		FilePatcher patcher = new FilePatcher(this.gameFile, patches);
		patcher.run();
	}

	private Collection<Patch> readPatches() {
		Logger.info("Reading patches...");
		Collection<Patch> patches = new HashSet<>();
		try (JarFile jarFile = new JarFile(this.frameworkFile)) {
			JarEntry patchesEntry = jarFile.getJarEntry(this.patchesFileName);
			InputStream inputStream = jarFile.getInputStream(patchesEntry);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			Stream<String> lines = reader.lines().filter(line -> line.startsWith("#"));

			lines.forEach(patchLine -> {
				Logger.info("Reading patch: '" + patchLine + "'");
				Patch patch = this.parsePatch(patchLine);
				patches.add(patch);
				Logger.info("Patch read.");
			});
		} catch (IOException ex) {
			Logger.error(ex);
		}
		return patches;
	}

	private Patch parsePatch(String patchLine) {
		String[] patchParts = patchLine.split("\\|");

		Patch patch = new Patch();
		patch.classToPatch = trim(patchParts[0]).substring(1);
		patch.methodToPatch = trim(patchParts[1]);
		patch.patchPlace = PatchPlace.getById(trim(patchParts[2]));
		patch.codeToInject = patchParts[3];

		return patch;
	}

	private String trim(String input) {
		return input.replaceAll("\\s+", "");
	}
}
