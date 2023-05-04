package am.ik.surveys.question;

import am.ik.surveys.tsid.TsidHolder;
import com.github.f4b6a3.tsid.Tsid;

/**
 * 設問ID
 *
 * @param value ID
 */
public record QuestionId(Tsid value) implements TsidHolder {
	public static QuestionId valueOf(String s) {
		return new QuestionId(Tsid.from(s));
	}
}
