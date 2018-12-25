package kd.equilinox.codeinjector;

import java.util.HashSet;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import kd.equilinox.codeinjector.hooks.InitializationHook;
import kd.equilinox.codeinjector.hooks.PostInitializationHook;
import kd.equilinox.codeinjector.hooks.PreInitializationHook;
import kd.equilinox.utils.Logger;

/**
 * Injector which should inject hooks into Equilinox source code.
 * 
 * @author Krzysztof Dobrzynski - k.dobrzynski94@gmail.com
 */
public class EquilinoxCodeInjector {
	private Set<IEquilinoxHook> hooks = new HashSet<>();

	public EquilinoxCodeInjector() {
		this.hooks.add(new PreInitializationHook());
		this.hooks.add(new InitializationHook());
		this.hooks.add(new PostInitializationHook());
	}

	public void run() {
		ClassPool pool = ClassPool.getDefault();
		for (IEquilinoxHook hook : this.hooks) {
			for (String packageName : hook.importPackages()) {
				pool.importPackage(packageName);
				Logger.info("Importing package: " + packageName + ", for hook: " + hook.getClass().getName());
			}
		}

		try {
			for (IEquilinoxHook hook : this.hooks) {
				CtClass gameClass = pool.get(hook.getModifyingClass());
				CtMethod gameMethod = gameClass.getDeclaredMethod(hook.getModifyingMethod());
				Logger.info("Running hook: " + hook.getClass().getName());
				hook.modifyMethod(gameClass, gameMethod);
				gameClass.writeFile(); // TODO: Load build class.
				Logger.info("Finished hooking: " + hook.getClass().getName());
			}
		} catch (Exception ex) {
			Logger.error(ex);
		}
	}
}
