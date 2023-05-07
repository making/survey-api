package am.ik.surveys.question.web;

import java.util.ArrayList;
import java.util.List;

import am.ik.surveys.question.DescriptiveQuestion;
import am.ik.surveys.question.Question;
import am.ik.surveys.question.QuestionChoice;
import am.ik.surveys.question.QuestionChoiceId;
import am.ik.surveys.question.QuestionId;
import am.ik.surveys.question.SelectiveQuestion;
import am.ik.surveys.tsid.TsidGenerator;

class QuestionRequest {

	private String questionText;

	private Integer maxChoices;

	private List<QuestionChoiceRequest> questionChoices = new ArrayList<>();

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public Integer getMaxChoices() {
		return maxChoices;
	}

	public void setMaxChoices(Integer maxChoices) {
		this.maxChoices = maxChoices;
	}

	public List<QuestionChoiceRequest> getQuestionChoices() {
		return questionChoices;
	}

	public void setQuestionChoices(List<QuestionChoiceRequest> questionChoices) {
		this.questionChoices = questionChoices;
	}

	public Question toQuestion(QuestionId questionId, TsidGenerator tsidGenerator) {
		if (this.maxChoices != null) {
			final List<QuestionChoice> choices = this.questionChoices.stream()
				.map(questionChoiceRequest -> questionChoiceRequest
					.toQuestionChoice(new QuestionChoiceId(tsidGenerator.generate())))
				.toList();
			return new SelectiveQuestion(questionId, this.questionText, choices, this.maxChoices);
		}
		else {
			return new DescriptiveQuestion(questionId, this.questionText);
		}
	}

}