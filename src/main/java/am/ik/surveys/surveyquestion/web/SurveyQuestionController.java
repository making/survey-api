package am.ik.surveys.surveyquestion.web;

import java.util.List;

import am.ik.surveys.question.QuestionId;
import am.ik.surveys.survey.SurveyId;
import am.ik.surveys.surveyquestion.SurveyQuestion;
import am.ik.surveys.surveyquestion.SurveyQuestionId;
import am.ik.surveys.surveyquestion.SurveyQuestionRepository;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/surveys/{surveyId}/survey_questions")
public class SurveyQuestionController {

	private final SurveyQuestionRepository surveyQuestionRepository;

	public SurveyQuestionController(SurveyQuestionRepository surveyQuestionRepository) {
		this.surveyQuestionRepository = surveyQuestionRepository;
	}

	@GetMapping(path = "")
	public List<SurveyQuestion> getSurveyQuestions(@PathVariable SurveyId surveyId) {
		return this.surveyQuestionRepository.findBySurveyId(surveyId);
	}

	@DeleteMapping(path = "")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSurveyQuestions(@PathVariable SurveyId surveyId) {
		this.surveyQuestionRepository.deleteBySurveyId(surveyId);
	}

	@PostMapping(path = "/{questionId}")
	public SurveyQuestion postSurveyQuestions(@PathVariable SurveyId surveyId, @PathVariable QuestionId questionId,
			@RequestBody SurveyQuestionRequest request) {
		final SurveyQuestionId surveyQuestionId = new SurveyQuestionId(surveyId, questionId);
		final SurveyQuestion surveyQuestion = request.toSurveyQuestion(surveyQuestionId);
		this.surveyQuestionRepository.insert(surveyQuestion);
		return surveyQuestion;
	}

	@DeleteMapping(path = "/{questionId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSurveyQuestion(@PathVariable SurveyId surveyId, @PathVariable QuestionId questionId) {
		final SurveyQuestionId surveyQuestionId = new SurveyQuestionId(surveyId, questionId);
		this.surveyQuestionRepository.deleteById(surveyQuestionId);
	}

}
