package am.ik.surveys.user;

import am.ik.surveys.tsid.TsidHolder;
import io.hypersistence.tsid.TSID;

/**
 * パーミッションID
 *
 * @param value ID
 */
public record PermissionId(TSID value) implements TsidHolder {
	public static PermissionId valueOf(String s) {
		return new PermissionId(TSID.from(s));
	}

	public static PermissionId valueOf(byte[] bytes) {
		return new PermissionId(TSID.from(bytes));
	}
}
