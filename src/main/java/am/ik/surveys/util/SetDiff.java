package am.ik.surveys.util;

import java.util.LinkedHashSet;
import java.util.Set;

public final class SetDiff<T> {

	private final Set<T> added;

	private final Set<T> deleted;

	public record Before<T>(Set<T> before) {
		public SetDiff<T> after(Set<T> after) {
			return new SetDiff<>(this.before, after);
		}
	}

	public static <T> SetDiff.Before<T> before(Set<T> before) {
		return new SetDiff.Before<>(before);
	}

	private SetDiff(Set<T> before, Set<T> after) {
		this.added = new LinkedHashSet<>(after);
		this.added.removeAll(before);
		this.deleted = new LinkedHashSet<>(before);
		this.deleted.removeAll(after);
	}

	public Set<T> added() {
		return this.added;
	}

	public Set<T> deleted() {
		return this.deleted;
	}

}
