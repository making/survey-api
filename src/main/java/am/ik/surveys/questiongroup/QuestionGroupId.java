package am.ik.surveys.questiongroup;

import am.ik.surveys.tsid.TsidHolder;
import io.hypersistence.tsid.TSID;

/**
 * 設問グループID
 *
 * @param value ID
 */
public record QuestionGroupId(TSID value) implements TsidHolder {
	public static QuestionGroupId valueOf(String s) {
		return new QuestionGroupId(TSID.from(s));
	}
}
