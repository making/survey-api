package am.ik.surveys.questiongroup;

import am.ik.surveys.tsid.TsidHolder;
import com.github.f4b6a3.tsid.Tsid;

/**
 * 設問グループID
 *
 * @param value ID
 */
public record QuestionGroupId(Tsid value) implements TsidHolder {
	public static QuestionGroupId valueOf(String s) {
		return new QuestionGroupId(Tsid.from(s));
	}
}
