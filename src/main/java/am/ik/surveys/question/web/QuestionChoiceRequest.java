package am.ik.surveys.question.web;

import am.ik.surveys.question.QuestionChoice;
import am.ik.surveys.question.QuestionChoiceId;

public class QuestionChoiceRequest {

	private String questionChoiceText;

	private boolean allowFreeText = false;

	public String getQuestionChoiceText() {
		return questionChoiceText;
	}

	public void setQuestionChoiceText(String questionChoiceText) {
		this.questionChoiceText = questionChoiceText;
	}

	public boolean isAllowFreeText() {
		return allowFreeText;
	}

	public void setAllowFreeText(boolean allowFreeText) {
		this.allowFreeText = allowFreeText;
	}

	public QuestionChoice toQuestionChoice(QuestionChoiceId questionChoiceId) {
		return new QuestionChoice(questionChoiceId, this.questionChoiceText, this.allowFreeText);
	}

}