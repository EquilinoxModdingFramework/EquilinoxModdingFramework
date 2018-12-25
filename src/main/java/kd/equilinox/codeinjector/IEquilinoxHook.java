package kd.equilinox.codeinjector;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;

/**
 * Hook for single class.
 * 
 * @author Krzysztof Dobrzynski - k.dobrzynski94@gmail.com
 */
public interface IEquilinoxHook {
	public String[] importPackages();

	public String getModifyingClass();

	public String getModifyingMethod();

	public void modifyMethod(CtClass gameClass, CtMethod gameMethod) throws CannotCompileException;
}
