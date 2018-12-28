package kd.equilinox.mods;

import kd.equilinox.events.inits.InitializationEvent;
import kd.equilinox.events.inits.PostInitializationEvent;
import kd.equilinox.events.inits.PreInitializationEvent;
import kd.equilinox.events.sessions.PostLoadSessionEvent;
import kd.equilinox.modloader.ModLoader;

/**
 * Defines a base mod class which will be recognized by a {@link ModLoader} and
 * loaded.
 * 
 * @author Krzysztof Dobrzynski - k.dobrzynski94@gmail.com
 */
public interface IMod {
	public String getModName();

	default void preInit(PreInitializationEvent event) {
	}

	default void init(InitializationEvent event) {
	}

	default void postInit(PostInitializationEvent event) {
	}

	default void postLoadSession(PostLoadSessionEvent event) {
	}
}
