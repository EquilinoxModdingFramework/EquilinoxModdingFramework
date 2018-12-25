package kd.equilinox.agent;

import java.lang.instrument.Instrumentation;

import kd.equilinox.codeinjector.EquilinoxCodeInjector;
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

		loadCodeInjector(instrumentation);

		Logger.info("Work done.");
	}

	private static void runModLoader(Instrumentation instrumentation) {
		Logger.info("Stating ModLoader...");

		Logger.info("Scanning for mod files...");
		ModLoader.INSTANCE.scanForModFiles();

		Logger.info("Scanning found JARs...");
		ModLoader.INSTANCE.scanJars();

		Logger.info("Loading class transformers...");
		ModLoader.INSTANCE.loadClassTransformers(instrumentation);

		Logger.info("Loading mods...");
		ModLoader.INSTANCE.loadMods();
	}

	private static void loadCodeInjector(Instrumentation instrumentation) {
		Logger.info("Loading code injector...");

		EquilinoxCodeInjector codeInjector = new EquilinoxCodeInjector();
		codeInjector.run();

		Logger.info("Code injected.");
	}
}
