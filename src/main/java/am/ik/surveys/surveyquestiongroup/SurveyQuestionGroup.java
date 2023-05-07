package am.ik.surveys.surveyquestiongroup;

import am.ik.surveys.questiongroup.QuestionGroupId;
import am.ik.surveys.survey.SurveyId;

public record SurveyQuestionGroup(SurveyId surveyId, QuestionGroupId questionGroupId) {
}
