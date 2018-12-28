package kd.equilinox.launcher.patches;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import kd.equilinox.utils.Logger;

/**
 * The purpose of this class is to patch game files.
 * 
 * @author Krzysztof Dobrzynski - k.dobrzynski94@gmail.com
 */
public class FilePatcher {
	/**
	 * Equilinox JAR file.
	 */
	private final File gameFile;
	/**
	 * Patches which must be processed by this FilePatcher.
	 */
	private final Collection<Patch> patches;
	/**
	 * Classes which has been patched.
	 */
	private final Map<String, byte[]> patchedClasses;

	public FilePatcher(File gameFile, Collection<Patch> patches) {
		this.gameFile = gameFile;
		this.patches = patches;
		this.patchedClasses = new HashMap<>();
	}

	public void run() {
		this.processPatches();
		this.applyPatches();
	}

	private void applyPatches() {
		PatchApplier applier = new PatchApplier(gameFile, patchedClasses);
		applier.run();
	}

	private void processPatches() {
		try (JarFile jarFile = new JarFile(this.gameFile)) {
			for (Patch patch : this.patches) {
				Logger.info("Executing patch: " + patch);

				InputStream stream = null;

				// Check if class was previously patched.
				if (this.patchedClasses.containsKey(patch.classToPatch)) {
					byte[] patchedClass = this.patchedClasses.get(patch.classToPatch);
					stream = new ByteArrayInputStream(patchedClass);
				} else {
					JarEntry jarEntry = jarFile.getJarEntry(patch.classToPatch);
					stream = jarFile.getInputStream(jarEntry);
				}

				byte[] patchedClass = this.patchClass(stream, patch);
				this.patchedClasses.put(patch.classToPatch, patchedClass);

				Logger.info("Class patched.");
			}
		} catch (Exception ex) {
			Logger.error("Error when patching classes.");
			Logger.error(ex);
		}
	}

	private byte[] patchClass(InputStream stream, Patch patch) throws Exception {
		ClassPool pool = ClassPool.getDefault();
		pool.insertClassPath(this.gameFile.getName());

		CtClass clazz = pool.makeClass(stream);

		CtMethod method = this.findMethod(clazz, patch);

		if (patch.patchPlace == PatchPlace.Before) {
			method.insertBefore(patch.codeToInject);
		} else if (patch.patchPlace == PatchPlace.After) {
			method.insertAfter(patch.codeToInject);
		}

		byte[] modifiedClass = clazz.toBytecode();
		return modifiedClass;
	}

	private CtMethod findMethod(CtClass clazz, Patch patch) throws Exception {
		// Used for finding the method. Ignores if the method is overloaded.
		if (patch.methodParameters == null) {
			return clazz.getDeclaredMethod(patch.methodToPatch);
		}

		// Used when method is overloaded. Searches through method parameters.
		Collection<CtMethod> methods = new ArrayList<>();
		for (CtMethod method : clazz.getDeclaredMethods()) {
			if (method.getName().equals(patch.methodToPatch)) {
				methods.add(method);
			}
		}

		for (CtMethod method : methods) {
			CtClass[] paramTypes = method.getParameterTypes();
			if (!isRightMethod(paramTypes, patch.methodParameters)) {
				continue;
			}
			return method;
		}

		throw new NotFoundException("Cannot find right method.");
	}

	private boolean isRightMethod(CtClass[] paramTypes, String[] methodParameters) {
		for (int i = 0; i < paramTypes.length; ++i) {
			if (!methodParameters[i].equals(paramTypes[i].getName())) {
				return false;
			}
		}
		return true;
	}
}
