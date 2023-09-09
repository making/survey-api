package am.ik.surveys.survey;

import am.ik.surveys.tsid.TsidHolder;
import io.hypersistence.tsid.TSID;

/**
 * アンケートID
 *
 * @param value ID
 */
public record SurveyId(TSID value) implements TsidHolder {
	public static SurveyId valueOf(String s) {
		return new SurveyId(TSID.from(s));
	}
}
