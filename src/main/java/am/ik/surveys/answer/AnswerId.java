package am.ik.surveys.answer;

import am.ik.surveys.tsid.TsidHolder;
import com.github.f4b6a3.tsid.Tsid;

/**
 * 回答ID
 *
 * @param value ID
 */
public record AnswerId(Tsid value) implements TsidHolder {
	public static AnswerId valueOf(String s) {
		return new AnswerId(Tsid.from(s));
	}
}
