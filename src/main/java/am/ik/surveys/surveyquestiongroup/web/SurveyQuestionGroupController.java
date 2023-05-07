package am.ik.surveys.surveyquestiongroup.web;

import java.util.List;

import am.ik.surveys.questiongroup.QuestionGroupId;
import am.ik.surveys.survey.SurveyId;
import am.ik.surveys.surveyquestiongroup.SurveyQuestionGroup;
import am.ik.surveys.surveyquestiongroup.SurveyQuestionGroupRepository;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/surveys/{surveyId}/survey_question_groups")
public class SurveyQuestionGroupController {

	private final SurveyQuestionGroupRepository surveyQuestionGroupRepository;

	public SurveyQuestionGroupController(SurveyQuestionGroupRepository surveyQuestionGroupRepository) {
		this.surveyQuestionGroupRepository = surveyQuestionGroupRepository;
	}

	@GetMapping(path = "")
	public List<SurveyQuestionGroup> getSurveyQuestionGroups(@PathVariable SurveyId surveyId) {
		return this.surveyQuestionGroupRepository.findBySurveyId(surveyId);
	}

	@DeleteMapping(path = "")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSurveyQuestionGroup(@PathVariable SurveyId surveyId) {
		this.surveyQuestionGroupRepository.deleteBySurveyId(surveyId);
	}

	@PutMapping(path = "/{questionGroupId}")
	public SurveyQuestionGroup postSurveyQuestionGroup(@PathVariable SurveyId surveyId,
			@PathVariable QuestionGroupId questionGroupId) {
		final SurveyQuestionGroup surveyQuestionGroup = new SurveyQuestionGroup(surveyId, questionGroupId);
		this.surveyQuestionGroupRepository.insert(surveyQuestionGroup);
		return surveyQuestionGroup;
	}

	@DeleteMapping(path = "/{questionGroupId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSurveyQuestionGroup(@PathVariable SurveyId surveyId,
			@PathVariable QuestionGroupId questionGroupId) {
		final SurveyQuestionGroup surveyQuestionGroup = new SurveyQuestionGroup(surveyId, questionGroupId);
		this.surveyQuestionGroupRepository.delete(surveyQuestionGroup);
	}

}
