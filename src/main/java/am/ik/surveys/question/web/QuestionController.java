package am.ik.surveys.question.web;

import java.net.URI;
import java.util.List;
import java.util.Set;

import am.ik.surveys.organization.OrganizationId;
import am.ik.surveys.question.Question;
import am.ik.surveys.question.QuestionChoice;
import am.ik.surveys.question.QuestionChoiceId;
import am.ik.surveys.question.QuestionId;
import am.ik.surveys.question.QuestionRepository;
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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class QuestionController {

	private final QuestionHandler questionHandler;

	private final QuestionRepository questionRepository;

	private final TsidGenerator tsidGenerator;

	public QuestionController(QuestionHandler questionHandler, QuestionRepository questionRepository,
			TsidGenerator tsidGenerator) {
		this.questionHandler = questionHandler;
		this.questionRepository = questionRepository;
		this.tsidGenerator = tsidGenerator;
	}

	@GetMapping(path = "/organizations/{organizationId}/questions")
	public List<Question> getQuestions(@PathVariable OrganizationId organizationId) {
		return this.questionRepository.findByOrganizationId(organizationId);
	}

	@GetMapping(path = "/organizations/{organizationId}/questions", params = "question_ids")
	public List<Question> getQuestions(@PathVariable OrganizationId organizationId,
			@RequestParam(name = "question_ids") Set<QuestionId> questionIds) {
		return this.questionRepository.findByIds(questionIds);
	}

	@PostMapping(path = "/organizations/{organizationId}/questions")
	public ResponseEntity<Question> postQuestions(@PathVariable OrganizationId organizationId,
			@RequestBody QuestionRequest request, UriComponentsBuilder builder) {
		final QuestionId questionId = new QuestionId(this.tsidGenerator.generate());
		final Question question = request.toQuestion(questionId, organizationId, this.tsidGenerator);
		this.questionRepository.insert(question);
		final URI location = builder.replacePath("/questions/{questionId}").build(questionId.asString());
		return ResponseEntity.created(location).body(question);
	}

	@GetMapping(path = "/questions/{questionId}")
	public Question getQuestion(@PathVariable QuestionId questionId) {
		return this.questionRepository.findById(questionId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
					"The given question id is not found (%s)".formatted(questionId.asString())));
	}

	@DeleteMapping(path = "/questions/{questionId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSurvey(@PathVariable QuestionId questionId) {
		this.questionRepository.deleteById(questionId);
	}

	@GetMapping(path = "/questions/{questionId}/question_choices")
	public List<QuestionChoice> getQuestionChoices(@PathVariable QuestionId questionId) {
		return this.questionRepository.findAllQuestionChoicesByQuestionId(questionId);
	}

	@PostMapping(path = "/questions/{questionId}/question_choices")
	public ResponseEntity<QuestionChoice> postQuestionChoices(@PathVariable QuestionId questionId,
			@RequestBody QuestionChoiceRequest request, UriComponentsBuilder builder) {
		final QuestionChoice questionChoice = this.questionHandler.postQuestionChoices(questionId, request);
		final URI location = builder.replacePath("/questions/{questionId}/question_choices/{questionChoiceId}")
			.build(questionId.asString(), questionChoice.questionChoiceId().asString());
		return ResponseEntity.created(location).body(questionChoice);
	}

	@GetMapping(path = "/questions/{questionId}/question_choices/{questionChoiceId}")
	public QuestionChoice getQuestionChoice(@PathVariable QuestionId questionId,
			@PathVariable QuestionChoiceId questionChoiceId) {
		return this.questionRepository.findQuestionChoiceByQuestionIdAndId(questionId, questionChoiceId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
					"The requested question choice is not found (questionId = %s, questionChoiceId = %s)"
						.formatted(questionId.asString(), questionChoiceId.asString())));
	}

	@DeleteMapping(path = "/questions/{questionId}/question_choices/{questionChoiceId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteQuestionChoice(@PathVariable QuestionId questionId,
			@PathVariable QuestionChoiceId questionChoiceId) {
		this.questionHandler.deleteQuestionChoice(questionId, questionChoiceId);
	}

}
