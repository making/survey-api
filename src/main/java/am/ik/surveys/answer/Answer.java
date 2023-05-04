package am.ik.surveys.answer;

import java.time.Instant;

import am.ik.surveys.surveyquestion.SurveyQuestionId;
import com.fasterxml.jackson.annotation.JsonProperty;

public sealed interface Answer permits DescriptiveAnswer, ChosenAnswer {

	AnswerId answerId();

	SurveyQuestionId surveyQuestionId();

	RespondentId respondentId();


	@JsonProperty
	default Instant createdAt() {
		return this.answerId().asInstant();
	}
}
