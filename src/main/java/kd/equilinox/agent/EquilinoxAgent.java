package kd.equilinox.agent;

import java.lang.instrument.Instrumentation;

import kd.equilinox.modloader.ModLoader;
import kd.equilinox.utils.Logger;

/**
 * Agent which will run before the game main method is called.
 * 
 * @author Krzysztof Dobrzynski - k.dobrzynski94@gmail.com
 */
public class EquilinoxAgent {
	public static void premain(String args, Instrumentation instrumentation) {
		Logger.info("Starting agent...");

		runModLoader(instrumentation);

		Logger.info("Work done.");
	}

	private static void runModLoader(Instrumentation instrumentation) {
		Logger.info("Stating ModLoader...");

		ModLoader modLoader = new ModLoader();

		Logger.info("Scanning for mod files...");
		modLoader.scanForModFiles();
		
		Logger.info("Scanning found JARs...");
		modLoader.scanJars();

		Logger.info("Loading class transformers...");
		modLoader.loadClassTransformers(instrumentation);

		Logger.info("Loading mods...");
		modLoader.loadMods();

		Logger.info("ModLoader finished.");
	}
}
