package kd.equilinox.launcher.patches;

/**
 * Model of single patch.
 * 
 * @author Krzysztof Dobrzynski - k.dobrzynski94@gmail.com
 */
public class Patch {
	/**
	 * Class which should be patched;
	 */
	public String classToPatch;
	/**
	 * Method of a class which should be patched.
	 */
	public String methodToPatch;
	/**
	 * B - Before / A - After
	 */
	public PatchPlace patchPlace;
	/**
	 * Code which should be injected into class.
	 */
	public String codeToInject;
	/**
	 * Optional parameter which will force patching-module to search using specified
	 * method name and parameters.
	 */
	public String[] methodParameters = null;

	public String toString() {
		return "Class: " + this.classToPatch + " | Method: " + this.methodToPatch + " | Patch: " + this.patchPlace
				+ " | Code: " + this.codeToInject;
	}
}
