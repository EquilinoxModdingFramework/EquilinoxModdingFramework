package kd.equilinox.mods;

import kd.equilinox.modloader.IModLoader;
import kd.equilinox.modloader.ModLoader;

/**
 * Defines a base mod class which will be recognized by a {@link ModLoader} and
 * loaded.
 * 
 * @author Krzysztof Dobrzynski - k.dobrzynski94@gmail.com
 */
public interface IMod {
	public String getModName();

	public void preInit(IModLoader modLoader);

	public void init(IModLoader modLoader);

	public void postInit(IModLoader modLoader);
}
