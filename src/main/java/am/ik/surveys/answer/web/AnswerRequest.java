package am.ik.surveys.answer.web;

import java.util.List;

import am.ik.surveys.answer.Answer;
import am.ik.surveys.answer.AnswerId;
import am.ik.surveys.answer.ChosenAnswer;
import am.ik.surveys.answer.DescriptiveAnswer;
import am.ik.surveys.answer.RespondentId;
import am.ik.surveys.question.QuestionId;
import am.ik.surveys.questiongroup.QuestionGroupId;
import am.ik.surveys.survey.SurveyId;

public class AnswerRequest {

	private QuestionGroupId questionGroupId;

	private QuestionId questionId;

	private RespondentId respondentId;

	private String answerText;

	private List<ChosenItemRequest> choices;

	public QuestionGroupId getQuestionGroupId() {
		return questionGroupId;
	}

	public void setQuestionGroupId(QuestionGroupId questionGroupId) {
		this.questionGroupId = questionGroupId;
	}

	public QuestionId getQuestionId() {
		return questionId;
	}

	public void setQuestionId(QuestionId questionId) {
		this.questionId = questionId;
	}

	public RespondentId getRespondentId() {
		return respondentId;
	}

	public void setRespondentId(RespondentId respondentId) {
		this.respondentId = respondentId;
	}

	public String getAnswerText() {
		return answerText;
	}

	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}

	public List<ChosenItemRequest> getChoices() {
		return choices;
	}

	public void setChoices(List<ChosenItemRequest> choices) {
		this.choices = choices;
	}

	public Answer toAnswer(AnswerId answerId, SurveyId surveyId) {
		if (this.answerText != null) {
			return new DescriptiveAnswer(answerId, surveyId, this.questionGroupId, this.questionId, this.respondentId,
					this.answerText);
		}
		return new ChosenAnswer(answerId, surveyId, this.questionGroupId, this.questionId, this.respondentId,
				this.choices.stream().map(ChosenItemRequest::toChoice).toList());
	}

}
