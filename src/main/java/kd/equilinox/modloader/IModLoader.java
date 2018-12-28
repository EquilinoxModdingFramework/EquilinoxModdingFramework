package kd.equilinox.modloader;

/**
 * One of the core interfaces which is responsible for holding an information
 * about Mod Loader state and pass that information to all loaded mods.
 * 
 * @author Krzysztof Dobrzynski - k.dobrzynski94@gmail.com
 */
public interface IModLoader {
	public void scanForModFiles();

	public void scanJars();

	public void loadMods();

	public void runPreInitialization();

	public void runInitialization();

	public void runPostInitialization();

	public void runPostLoadSession(Object session);
}
