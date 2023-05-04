package am.ik.surveys.answer;

import am.ik.surveys.question.QuestionChoiceId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 選択回答
 *
 * @param questionChoiceId 設問選択肢ID
 * @param answerText 記述回答
 */
public record ChosenItem(QuestionChoiceId questionChoiceId,
						 @JsonInclude(Include.NON_NULL) String answerText) {

	public ChosenItem(QuestionChoiceId questionChoiceId) {
		this(questionChoiceId, null);
	}
}