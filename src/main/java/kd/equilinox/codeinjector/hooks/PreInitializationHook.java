package kd.equilinox.codeinjector.hooks;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import kd.equilinox.codeinjector.IEquilinoxHook;

/**
 * Hook for PreInitialization stage. Injects code before the actual code in main
 * method.
 * 
 * @author Krzysztof Dobrzynski - k.dobrzynski94@gmail.com
 */
public class PreInitializationHook implements IEquilinoxHook {

	public String[] importPackages() {
		return new String[] { "main" };
	}

	public String getModifyingClass() {
		return "main.MainApp";
	}

	public String getModifyingMethod() {
		return "main";
	}

	public void modifyMethod(CtClass gameClass, CtMethod gameMethod) throws CannotCompileException {
		gameMethod.insertBefore("kd.equilinox.modloader.ModLoader.INSTANCE.runPreInitialization();");
	}
}
