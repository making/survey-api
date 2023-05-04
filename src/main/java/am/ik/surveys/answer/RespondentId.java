package am.ik.surveys.answer;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 回答者ID
 *
 * @param value ID
 */
public record RespondentId(@JsonProperty("respondent_id") String value) {
	public static RespondentId valueOf(String s) {
		return new RespondentId(s);
	}

	public String asString() {
		return this.value;
	}
}
