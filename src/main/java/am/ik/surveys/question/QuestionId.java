package am.ik.surveys.question;

import am.ik.surveys.tsid.TsidHolder;
import io.hypersistence.tsid.TSID;

/**
 * 設問ID
 *
 * @param value ID
 */
public record QuestionId(TSID value) implements TsidHolder {
	public static QuestionId valueOf(String s) {
		return new QuestionId(TSID.from(s));
	}

	public static QuestionId valueOf(byte[] s) {
		return new QuestionId(TSID.from(s));
	}
}
