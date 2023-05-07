package am.ik.surveys.questiongroupquestion.web;

import am.ik.surveys.questiongroupquestion.QuestionGroupQuestion;
import am.ik.surveys.questiongroupquestion.QuestionGroupQuestionId;

class QuestionGroupQuestionRequest {

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
