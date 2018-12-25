package kd.equilinox.codeinjector.hooks;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import kd.equilinox.codeinjector.IEquilinoxHook;

/**
 * Hook for PostInitialization stage. Injects code after the actual code in main
 * method.
 * 
 * @author Krzysztof Dobrzynski - k.dobrzynski94@gmail.com
 */
public class PostInitializationHook implements IEquilinoxHook {
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
		gameMethod.insertAfter("kd.equilinox.modloader.ModLoader.INSTANCE.runPostInitialization();");
	}
}
