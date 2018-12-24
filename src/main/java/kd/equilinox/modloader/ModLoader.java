package kd.equilinox.modloader;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import kd.equilinox.mods.IMod;
import kd.equilinox.utils.FileUtils;
import kd.equilinox.utils.Logger;

/**
 * Core class which is responsible for loading mods and other class
 * transformers.
 * 
 * @author Krzysztof Dobrzynski - k.dobrzynski94@gmail.com
 */
public class ModLoader implements IModLoader {
	/**
	 * A set of JAR files found in mods folder. NOTE: They don't need to be all
	 * mods.
	 */
	private Set<File> jarFiles = new HashSet<>();
	/**
	 * Holds an information about custom class transformers. Classes which
	 * implements {@link ClassFileTransformer}.
	 */
	private Set<Class<?>> classTransformers = new HashSet<>();
	/**
	 * Holds an information about mods core classes. Classes which implements
	 * {@link IMod}.
	 */
	private Set<Class<?>> modCoreClasses = new HashSet<>();
	/**
	 * Holds an information about currently loaded mods.
	 */
	private Set<IMod> mods = new HashSet<>();

	public void scanForModFiles() {
		Path modsFolder = Paths.get(FileUtils.MODS_DIR);
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(modsFolder)) {
			for (Path file : stream) {
				File modFile = file.toFile();
				String fullName = modFile.getName();
				if (fullName.endsWith(".jar") || fullName.endsWith(".zip")) {
					jarFiles.add(modFile);
					Logger.info("Found JAR file: " + fullName);
				}
			}
		} catch (Exception x) {
			System.err.println(x);
		}
		Logger.info("Found: " + this.jarFiles.size() + " JAR files.");
	}

	public void scanJars() {
		try {
			for (File file : this.jarFiles) {
				JarFile jarFile = new JarFile(file);
				Collection<JarEntry> entries = this.getJarEntries(jarFile);
				URLClassLoader urlClassLoader = this.getUrlClassLoader(file);

				for (JarEntry entry : entries) {
					Class<?> modClass = this.loadClass(urlClassLoader, entry);

					if (modClass == null) {
						continue;
					}

					if (modClass.isAssignableFrom(ClassFileTransformer.class)
							|| !Modifier.isAbstract(modClass.getModifiers())) {
						this.classTransformers.add(modClass);
						Logger.info("Found ClassFileTransformer: " + modClass.getName());
					}

					if (modClass.isAssignableFrom(IMod.class) || !Modifier.isAbstract(modClass.getModifiers())) {
						this.modCoreClasses.add(modClass);
						Logger.info("Found IMod class: " + modClass.getName());
					}
				}
			}
		} catch (Exception ex) {
			Logger.error(ex);
		}

		Logger.info("Total found ClassFileTransformer: " + this.classTransformers.size());
		Logger.info("Total found mods: " + this.mods.size());
	}

	public void loadClassTransformers(Instrumentation instrumentation) {
		try {
			for (Class<?> modClass : this.classTransformers) {
				ClassFileTransformer modTransformer = (ClassFileTransformer) modClass.newInstance();
				instrumentation.addTransformer(modTransformer);
			}
		} catch (Exception ex) {
			Logger.error(ex);
		}
	}

	public void loadMods() {
		try {
			Logger.info("Searching for mods...");
			for (Class<?> modClass : this.modCoreClasses) {
				IMod modCoreClass = (IMod) modClass.newInstance();
				this.mods.add(modCoreClass);
				Logger.info("Found mod: " + modCoreClass.getModName());
			}

			Logger.info("Entering preinitialization stage...");
			for (IMod mod : this.mods) {
				Logger.info("Loading mod: " + mod.getModName());
				mod.preInit(this);
			}

			Logger.info("Entering initialization stage...");
			for (IMod mod : this.mods) {
				Logger.info("Loading mod: " + mod.getModName());
				mod.init(this);
			}

			Logger.info("Entering postinitialization stage...");
			for (IMod mod : this.mods) {
				Logger.info("Loading mod: " + mod.getModName());
				mod.postInit(this);
			}
		} catch (Exception ex) {
			Logger.error(ex);
		}
	}

	private Collection<JarEntry> getJarEntries(JarFile jarFile) {
		Collection<JarEntry> jarEntries = new HashSet<>();
		Enumeration<JarEntry> entries = jarFile.entries();

		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			jarEntries.add(entry);
		}

		return jarEntries;
	}

	private URLClassLoader getUrlClassLoader(File file) throws MalformedURLException {
		URL[] urls = { new URL("jar:file:" + file.getAbsolutePath() + "!/"),
				new URL("jar:" + file.getAbsolutePath() + "!/") };
		URLClassLoader urlClassLoader = URLClassLoader.newInstance(urls);
		return urlClassLoader;
	}

	private Class<?> loadClass(URLClassLoader urlClassLoader, JarEntry entry) throws ClassNotFoundException {
		if (!entry.getName().endsWith(".class")) {
			return null;
		}

		String className = entry.getName().substring(0, entry.getName().length() - 6);
		className = className.replace('/', '.');
		Class<?> clazz = urlClassLoader.loadClass(className);
		return clazz;
	}
}