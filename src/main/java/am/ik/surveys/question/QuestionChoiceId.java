package am.ik.surveys.question;

import am.ik.surveys.tsid.TsidHolder;
import com.github.f4b6a3.tsid.Tsid;

/**
 * 設問選択肢ID
 *
 * @param value ID
 */
public record QuestionChoiceId(Tsid value) implements TsidHolder {
	public static QuestionChoiceId valueOf(String s) {
		return new QuestionChoiceId(Tsid.from(s));
	}
}
