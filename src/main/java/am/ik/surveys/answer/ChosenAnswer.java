package am.ik.surveys.answer;

import java.util.List;

import am.ik.surveys.question.QuestionId;
import am.ik.surveys.questiongroup.QuestionGroupId;
import am.ik.surveys.survey.SurveyId;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * 選択式設問回答
 *
 * @param answerId 回答ID
 * @param surveyId アンケートID
 * @param questionGroupId 設問グループID
 * @param questionId 設問ID
 * @param respondentId 回答者ID
 * @param chosenItems 選択回答
 */
public record ChosenAnswer(AnswerId answerId, SurveyId surveyId, QuestionGroupId questionGroupId, QuestionId questionId,
		@JsonUnwrapped RespondentId respondentId, List<ChosenItem> chosenItems) implements Answer {

}
