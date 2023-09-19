package am.ik.surveys.answer;

import am.ik.surveys.tsid.TsidHolder;
import io.hypersistence.tsid.TSID;

/**
 * 回答ID
 *
 * @param value ID
 */
public record AnswerId(TSID value) implements TsidHolder {
	public static AnswerId valueOf(String s) {
		return new AnswerId(TSID.from(s));
	}

	public static AnswerId valueOf(long l) {
		return new AnswerId(TSID.from(l));
	}

}
