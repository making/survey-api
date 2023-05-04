package am.ik.surveys.survey;

import am.ik.surveys.tsid.TsidHolder;
import com.github.f4b6a3.tsid.Tsid;

/**
 * アンケートID
 *
 * @param value ID
 */
public record SurveyId(Tsid value) implements TsidHolder {
	public static SurveyId valueOf(String s) {
		return new SurveyId(Tsid.from(s));
	}
}
