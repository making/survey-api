package am.ik.surveys.answer;

import java.time.Instant;

import am.ik.surveys.question.QuestionId;
import am.ik.surveys.questiongroup.QuestionGroupId;
import am.ik.surveys.survey.SurveyId;
import com.fasterxml.jackson.annotation.JsonProperty;

public sealed interface Answer permits DescriptiveAnswer, ChosenAnswer {

	AnswerId answerId();

	SurveyId surveyId();

	QuestionGroupId questionGroupId();

	QuestionId questionId();

	RespondentId respondentId();

	@JsonProperty
	default Instant createdAt() {
		return this.answerId().asInstant();
	}

}
