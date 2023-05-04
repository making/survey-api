package am.ik.surveys.survey.web;

import java.net.URI;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping(path = "/surveys")
public class SurveyController {

	private final SurveyRepository surveyRepository;

	private final TsidGenerator tsidGenerator;

	public SurveyController(SurveyRepository surveyRepository, TsidGenerator tsidGenerator) {
		this.surveyRepository = surveyRepository;
		this.tsidGenerator = tsidGenerator;
	}

	@GetMapping(path = "")
	public List<Survey> getSurveys() {
		return this.surveyRepository.findAll();
	}

	@PostMapping(path = "")
	public ResponseEntity<Survey> postSurveys(@RequestBody SurveyRequest request, UriComponentsBuilder builder) {
		final SurveyId surveyId = new SurveyId(this.tsidGenerator.generate());
		final Survey survey = request.toSurvey(surveyId);
		this.surveyRepository.insert(survey);
		final URI location = builder.replacePath("/surveys/{surveyId}").build(surveyId.asString());
		return ResponseEntity.created(location).body(survey);
	}

	@GetMapping(path = "/{surveyId}")
	public Survey getSurvey(@PathVariable SurveyId surveyId) {
		return this.surveyRepository.findById(surveyId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
					"The given survey id is not found (" + surveyId.asString() + ")"));
	}

	@DeleteMapping(path = "/{surveyId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSurvey(@PathVariable SurveyId surveyId) {
		this.surveyRepository.deleteById(surveyId);
	}

}
