package am.ik.surveys.surveyquestion.web;

import am.ik.surveys.surveyquestion.SurveyQuestion;
import am.ik.surveys.surveyquestion.SurveyQuestionId;

public class SurveyQuestionRequest {

	private boolean required;

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public SurveyQuestion toSurveyQuestion(SurveyQuestionId surveyQuestionId) {
		return new SurveyQuestion(surveyQuestionId, required);
	}

}
