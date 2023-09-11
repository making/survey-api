package am.ik.surveys.questiongroup.web;

import am.ik.surveys.questiongroup.QuestionGroupQuestion;
import am.ik.surveys.questiongroup.QuestionGroupQuestionId;

public class QuestionGroupQuestionRequest {

	private boolean required;

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public QuestionGroupQuestion toQuestionGroupQuestion(QuestionGroupQuestionId questionGroupQuestionId) {
		return new QuestionGroupQuestion(questionGroupQuestionId, required);
	}

}
