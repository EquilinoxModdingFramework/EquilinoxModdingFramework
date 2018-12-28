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

	public String toString() {
		return "Class: " + this.classToPatch + " | Method: " + this.methodToPatch + " | Patch: " + this.patchPlace
				+ " | Code: " + this.codeToInject;
	}
}
