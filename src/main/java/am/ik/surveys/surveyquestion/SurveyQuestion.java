package am.ik.surveys.surveyquestion;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * アンケート設問
 *
 * @param surveyQuestionId アンケート設問ID
 * @param required 回答必須
 */
public record SurveyQuestion(@JsonUnwrapped SurveyQuestionId surveyQuestionId, boolean required) {
}
