package am.ik.surveys.surveyquestion;

import am.ik.surveys.question.QuestionId;
import am.ik.surveys.survey.SurveyId;

/**
 * アンケート設問ID
 *
 * @param surveyId アンケートID
 * @param questionId 設問ID
 */
public record SurveyQuestionId(SurveyId surveyId, QuestionId questionId) {
}
