package am.ik.surveys.role;

public enum Verb {

	GET, LIST, CREATE, UPDATE, DELETE;

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}

}
