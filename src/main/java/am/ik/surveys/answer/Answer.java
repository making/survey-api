package am.ik.surveys.answer;

import java.time.Instant;

import am.ik.surveys.json.IncludeCreatedAt;
import am.ik.surveys.question.QuestionId;
import am.ik.surveys.questiongroup.QuestionGroupId;
import am.ik.surveys.survey.SurveyId;

public sealed interface Answer extends IncludeCreatedAt permits DescriptiveAnswer, ChosenAnswer {

	AnswerId answerId();

	SurveyId surveyId();

	QuestionGroupId questionGroupId();

	QuestionId questionId();

	RespondentId respondentId();

	@Override
	default Instant createdAt() {
		return this.answerId().asInstant();
	}

}
