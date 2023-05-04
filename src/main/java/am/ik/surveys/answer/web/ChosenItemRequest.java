package am.ik.surveys.answer.web;

import am.ik.surveys.answer.ChosenItem;
import am.ik.surveys.question.QuestionChoiceId;

public class ChosenItemRequest {

	private QuestionChoiceId questionChoiceId;

	private String answerText;

	public QuestionChoiceId getQuestionChoiceId() {
		return questionChoiceId;
	}

	public void setQuestionChoiceId(QuestionChoiceId questionChoiceId) {
		this.questionChoiceId = questionChoiceId;
	}

	public String getAnswerText() {
		return answerText;
	}

	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}

	public ChosenItem toChoice() {
		return new ChosenItem(this.questionChoiceId, this.answerText);
	}

}
