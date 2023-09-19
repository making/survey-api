package am.ik.surveys.question;

import am.ik.surveys.tsid.TsidHolder;
import io.hypersistence.tsid.TSID;

/**
 * 設問選択肢ID
 *
 * @param value ID
 */
public record QuestionChoiceId(TSID value) implements TsidHolder {
	public static QuestionChoiceId valueOf(String s) {
		return new QuestionChoiceId(TSID.from(s));
	}

	public static QuestionChoiceId valueOf(long l) {
		return new QuestionChoiceId(TSID.from(l));
	}
}
