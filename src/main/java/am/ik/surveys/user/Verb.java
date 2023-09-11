package am.ik.surveys.user;

public enum Verb {

	GET, LIST, CREATE, UPDATE, DELETE;

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}

}
