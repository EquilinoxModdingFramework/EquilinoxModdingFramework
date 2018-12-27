package kd.equilinox.core.patches;

/**
 * @author Krzysztof Dobrzynski - k.dobrzynski94@gmail.com
 */
public enum PatchPlace {
	Before("B"), After("A");

	private String id;

	PatchPlace(String id) {
		this.id = id;
	}

	public String toString() {
		return this.id;
	}

	public static PatchPlace getById(String id) {
		switch (id) {
		case "B":
			return PatchPlace.Before;
		case "A":
			return PatchPlace.After;
		default:
			return null;
		}
	}
}
