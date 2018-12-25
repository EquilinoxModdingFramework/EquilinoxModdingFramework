package kd.equilinox.codeinjector.hooks;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import kd.equilinox.codeinjector.IEquilinoxHook;

/**
 * Hook for Initialization stage. Injects code after the Equilinox modules
 * initialization.
 * 
 * @author Krzysztof Dobrzynski - k.dobrzynski94@gmail.com
 */
public class InitializationHook implements IEquilinoxHook {
	public String[] importPackages() {
		return new String[] { "gameManaging" };
	}

	public String getModifyingClass() {
		return "gameManaging.GameManager";
	}

	public String getModifyingMethod() {
		return "init";
	}

	public void modifyMethod(CtClass gameClass, CtMethod gameMethod) throws CannotCompileException {
		gameMethod.insertAfter("kd.equilinox.modloader.ModLoader.INSTANCE.runInitialization();");
	}
}
