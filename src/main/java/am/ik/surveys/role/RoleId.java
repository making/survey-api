package am.ik.surveys.role;

import am.ik.surveys.tsid.TsidHolder;
import io.hypersistence.tsid.TSID;

/**
 * ロールID
 *
 * @param value ID
 */
public record RoleId(TSID value) implements TsidHolder {
	public static RoleId valueOf(String s) {
		return new RoleId(TSID.from(s));
	}

	public static RoleId valueOf(long l) {
		return new RoleId(TSID.from(l));
	}
}
