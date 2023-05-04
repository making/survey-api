package am.ik.surveys.question;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 設問選択肢
 *
 * @param questionChoiceId 設問選択肢ID
 * @param questionChoiceText 選択肢本文
 * @param allowFreeText 自由記述可
 */
public record QuestionChoice(QuestionChoiceId questionChoiceId, String questionChoiceText, boolean allowFreeText) {
	public QuestionChoice(QuestionChoiceId questionChoiceId, String questionChoiceText) {
		this(questionChoiceId, questionChoiceText, false);
	}

	@JsonProperty
	public Instant createdAt() {
		return questionChoiceId.asInstant();
	}
}
