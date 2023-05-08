package am.ik.surveys.survey.web;

import java.util.List;

import am.ik.surveys.survey.Survey;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public record SurveyResponse(@JsonUnwrapped Survey survey, List<QuestionGroup> questionGroups) {
	record QuestionGroup(@JsonUnwrapped am.ik.surveys.questiongroup.QuestionGroup questionGroup,
			@JsonInclude(Include.NON_NULL) List<Question> questions) {

		public QuestionGroup(am.ik.surveys.questiongroup.QuestionGroup qg) {
			this(qg, null);
		}

		record Question(@JsonUnwrapped am.ik.surveys.question.Question question, boolean required) {

		}
	}
}
