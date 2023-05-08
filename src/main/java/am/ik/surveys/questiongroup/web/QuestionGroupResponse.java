package am.ik.surveys.questiongroup.web;

import java.util.List;

import am.ik.surveys.questiongroup.QuestionGroup;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

record QuestionGroupResponse(@JsonUnwrapped QuestionGroup questionGroup, List<Question> questions) {
	record Question(@JsonUnwrapped am.ik.surveys.question.Question question, boolean required) {

	}
}
