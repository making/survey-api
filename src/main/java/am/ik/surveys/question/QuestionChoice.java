package am.ik.surveys.question;

import java.time.Instant;

import am.ik.surveys.json.IncludeCreatedAt;

/**
 * 設問選択肢
 *
 * @param questionChoiceId 設問選択肢ID
 * @param questionChoiceText 選択肢本文
 * @param allowFreeText 自由記述可
 */
public record QuestionChoice(QuestionChoiceId questionChoiceId, String questionChoiceText,
		boolean allowFreeText) implements IncludeCreatedAt {
	public QuestionChoice(QuestionChoiceId questionChoiceId, String questionChoiceText) {
		this(questionChoiceId, questionChoiceText, false);
	}

	@Override
	public Instant createdAt() {
		return questionChoiceId.asInstant();
	}
}
