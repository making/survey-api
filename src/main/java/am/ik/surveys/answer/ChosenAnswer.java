package am.ik.surveys.answer;

import java.util.List;

import am.ik.surveys.surveyquestion.SurveyQuestionId;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * 選択式設問回答
 *
 * @param answerId 回答ID
 * @param surveyQuestionId アンケート設問ID
 * @param respondentId 回答者ID
 * @param chosenItems 選択回答
 */
public record ChosenAnswer(AnswerId answerId, @JsonUnwrapped SurveyQuestionId surveyQuestionId,
		@JsonUnwrapped RespondentId respondentId, List<ChosenItem> chosenItems) implements Answer {

}
