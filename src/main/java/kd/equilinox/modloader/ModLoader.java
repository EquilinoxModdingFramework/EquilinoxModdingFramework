package kd.equilinox.modloader;

import java.io.File;
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

import kd.equilinox.events.inits.InitializationEvent;
import kd.equilinox.events.inits.PostInitializationEvent;
import kd.equilinox.events.inits.PreInitializationEvent;
import kd.equilinox.events.sessions.PostLoadSessionEvent;
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
	 * Instance of current Mod Loader.
	 */
	public static final IModLoader INSTANCE = new ModLoader();

	/**
	 * A set of JAR files found in mods folder. NOTE: They don't need to be all
	 * mods.
	 */
	private Set<File> jarFiles = new HashSet<>();
	/**
	 * Holds an information about mods core classes. Classes which implements
	 * {@link IMod}.
	 */
	private Set<Class<?>> modCoreClasses = new HashSet<>();
	/**
	 * Holds an information about currently loaded mods.
	 */
	private Set<IMod> mods = new HashSet<>();

	private ModLoader() {
		Logger.info("Stating ModLoader...");

		Logger.info("Scanning for mod files...");
		this.scanForModFiles();

		Logger.info("Scanning JARs...");
		this.scanJars();

		Logger.info("Loading mods...");
		this.loadMods();

		Logger.info("Starting mods initialization...");
		this.runPreInitialization();
	}

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

					if (IMod.class.isAssignableFrom(modClass) && !Modifier.isAbstract(modClass.getModifiers())) {
						this.modCoreClasses.add(modClass);
						Logger.info("Found IMod class: " + modClass.getName());
					}
				}
			}
		} catch (Exception ex) {
			Logger.error(ex);
		}

		Logger.info("Total found IMod classes: " + this.modCoreClasses.size());
	}

	public void loadMods() {
		try {
			Logger.info("Searching for mods...");
			for (Class<?> modClass : this.modCoreClasses) {
				IMod modCoreClass = (IMod) modClass.newInstance();
				this.mods.add(modCoreClass);
				Logger.info("Found mod: " + modCoreClass.getModName());
			}
		} catch (Exception ex) {
			Logger.error(ex);
		}
	}

	public void runPreInitialization() {
		Logger.info("Entering PreInitialization stage...");
		for (IMod mod : this.mods) {
			Logger.info("Loading mod: " + mod.getModName());
			PreInitializationEvent event = new PreInitializationEvent();
			mod.preInit(event);
		}
	}

	public void runInitialization() {
		Logger.info("Entering Initialization stage...");
		for (IMod mod : this.mods) {
			Logger.info("Loading mod: " + mod.getModName());
			InitializationEvent event = new InitializationEvent();
			mod.init(event);
		}
	}

	public void runPostInitialization() {
		Logger.info("Entering PostInitialization stage...");
		for (IMod mod : this.mods) {
			Logger.info("Loading mod: " + mod.getModName());
			PostInitializationEvent event = new PostInitializationEvent();
			mod.postInit(event);
		}
	}

	public void runPostLoadSession(Object session) {
		Logger.info("Entering PostLoadSession stage...");
		for (IMod mod : this.mods) {
			Logger.info("Loading mod: " + mod.getModName());
			PostLoadSessionEvent event = new PostLoadSessionEvent(session);
			mod.postLoadSession(event);
		}
	}

	public Collection<JarEntry> getJarEntries(JarFile jarFile) {
		Collection<JarEntry> jarEntries = new HashSet<>();
		Enumeration<JarEntry> entries = jarFile.entries();

		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			jarEntries.add(entry);
		}

		return jarEntries;
	}

	public URLClassLoader getUrlClassLoader(File file) throws MalformedURLException {
		URL[] urls = { new URL("jar:file:" + file.getAbsolutePath() + "!/") };
		URLClassLoader urlClassLoader = URLClassLoader.newInstance(urls);
		return urlClassLoader;
	}

	public Class<?> loadClass(URLClassLoader urlClassLoader, JarEntry entry) throws ClassNotFoundException {
		if (!entry.getName().endsWith(".class")) {
			return null;
		}

		String className = entry.getName().substring(0, entry.getName().length() - 6);
		className = className.replace('/', '.');
		Class<?> clazz = urlClassLoader.loadClass(className);
		return clazz;
	}
}
