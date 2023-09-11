package am.ik.surveys.survey.web;

import java.net.URI;
import java.util.List;

import am.ik.surveys.organization.OrganizationId;
import am.ik.surveys.survey.Survey;
import am.ik.surveys.survey.SurveyId;
import am.ik.surveys.survey.SurveyRepository;
import am.ik.surveys.tsid.TsidGenerator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class SurveyController {

	private final SurveyHandler surveyHandler;

	private final SurveyRepository surveyRepository;

	private final TsidGenerator tsidGenerator;

	public SurveyController(SurveyHandler surveyHandler, SurveyRepository surveyRepository,
			TsidGenerator tsidGenerator) {
		this.surveyHandler = surveyHandler;
		this.surveyRepository = surveyRepository;
		this.tsidGenerator = tsidGenerator;
	}

	@GetMapping(path = "/organizations/{organizationId}/surveys")
	public List<Survey> getSurveys(@PathVariable OrganizationId organizationId) {
		return this.surveyRepository.findByOrganizationId(organizationId);
	}

	@PostMapping(path = "/organizations/{organizationId}/surveys")
	public ResponseEntity<Survey> postSurveys(@PathVariable OrganizationId organizationId,
			@RequestBody SurveyRequest request, UriComponentsBuilder builder) {
		final SurveyId surveyId = new SurveyId(this.tsidGenerator.generate());
		final Survey survey = request.toSurvey(surveyId, organizationId);
		this.surveyRepository.insert(survey);
		final URI location = builder.replacePath("/surveys/{surveyId}").build(surveyId.asString());
		return ResponseEntity.created(location).body(survey);
	}

	@GetMapping(path = "/surveys/{surveyId}")
	public SurveyResponse getSurvey(@PathVariable SurveyId surveyId,
			@RequestParam(name = "include_questions", required = false) boolean includeQuestions) {
		return this.surveyHandler.getSurvey(surveyId, includeQuestions);
	}

	@DeleteMapping(path = "/surveys/{surveyId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSurvey(@PathVariable SurveyId surveyId) {
		this.surveyRepository.deleteById(surveyId);
	}

}
