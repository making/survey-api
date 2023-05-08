package am.ik.surveys.question.web;

import am.ik.surveys.question.QuestionChoice;
import am.ik.surveys.question.QuestionChoiceId;

public class QuestionChoiceRequest {

	private String questionChoiceText;

	private int score = 0;

	private boolean allowFreeText = false;

	public String getQuestionChoiceText() {
		return questionChoiceText;
	}

	public void setQuestionChoiceText(String questionChoiceText) {
		this.questionChoiceText = questionChoiceText;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public boolean isAllowFreeText() {
		return allowFreeText;
	}

	public void setAllowFreeText(boolean allowFreeText) {
		this.allowFreeText = allowFreeText;
	}

	public QuestionChoice toQuestionChoice(QuestionChoiceId questionChoiceId) {
		return new QuestionChoice(questionChoiceId, this.questionChoiceText, this.score, this.allowFreeText);
	}

}