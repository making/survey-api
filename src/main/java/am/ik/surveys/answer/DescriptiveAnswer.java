package am.ik.surveys.answer;

import am.ik.surveys.surveyquestion.SurveyQuestionId;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * 記述式設問回答
 *
 * @param answerId 回答ID
 * @param surveyQuestionId アンケート設問ID
 * @param respondentId 回答者ID
 * @param answerText 回答内容
 */
public record DescriptiveAnswer(AnswerId answerId, @JsonUnwrapped SurveyQuestionId surveyQuestionId,
		@JsonUnwrapped RespondentId respondentId, String answerText) implements Answer {
}
