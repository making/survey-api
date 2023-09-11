package am.ik.surveys.user;

import am.ik.surveys.tsid.TsidHolder;
import io.hypersistence.tsid.TSID;

/**
 * ユーザーID
 *
 * @param value ID
 */
public record UserId(TSID value) implements TsidHolder {
	public static UserId valueOf(String s) {
		return new UserId(TSID.from(s));
	}

	public static UserId valueOf(byte[] bytes) {
		return new UserId(TSID.from(bytes));
	}
}
