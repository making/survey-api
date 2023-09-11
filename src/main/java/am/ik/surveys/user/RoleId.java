package am.ik.surveys.user;

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

	public static RoleId valueOf(byte[] bytes) {
		return new RoleId(TSID.from(bytes));
	}
}
