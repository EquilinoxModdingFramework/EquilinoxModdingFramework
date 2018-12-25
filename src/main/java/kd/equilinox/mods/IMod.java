package kd.equilinox.mods;

import kd.equilinox.events.InitializationEvent;
import kd.equilinox.events.PostInitializationEvent;
import kd.equilinox.events.PreInitializationEvent;
import kd.equilinox.modloader.ModLoader;

/**
 * Defines a base mod class which will be recognized by a {@link ModLoader} and
 * loaded.
 * 
 * @author Krzysztof Dobrzynski - k.dobrzynski94@gmail.com
 */
public interface IMod {
	public String getModName();

	public void preInit(PreInitializationEvent event);

	public void init(InitializationEvent event);

	public void postInit(PostInitializationEvent event);
}
