package am.ik.surveys.question.web;

import java.net.URI;
import java.util.List;

import am.ik.surveys.question.Question;
import am.ik.surveys.question.QuestionChoice;
import am.ik.surveys.question.QuestionChoiceId;
import am.ik.surveys.question.QuestionId;
import am.ik.surveys.question.QuestionRepository;
import am.ik.surveys.question.SelectiveQuestion;
import am.ik.surveys.tsid.TsidGenerator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
@RequestMapping(path = "/questions")
public class QuestionController {

	private final QuestionRepository questionRepository;

	private final TsidGenerator tsidGenerator;

	public QuestionController(QuestionRepository questionRepository, TsidGenerator tsidGenerator) {
		this.questionRepository = questionRepository;
		this.tsidGenerator = tsidGenerator;
	}

	@GetMapping(path = "")
	public List<Question> getQuestions() {
		return this.questionRepository.findAll();
	}

	@PostMapping(path = "")
	public ResponseEntity<Question> postQuestions(@RequestBody QuestionRequest request, UriComponentsBuilder builder) {
		final QuestionId questionId = new QuestionId(this.tsidGenerator.generate());
		final Question question = request.toQuestion(questionId, this.tsidGenerator);
		this.questionRepository.insert(question);
		final URI location = builder.replacePath("/questions/{questionId}").build(questionId.asString());
		return ResponseEntity.created(location).body(question);
	}

	@GetMapping(path = "/{questionId}")
	public Question getQuestion(@PathVariable QuestionId questionId) {
		return this.questionRepository.findById(questionId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
					"The given question id is not found (%s)".formatted(questionId.asString())));
	}

	@DeleteMapping(path = "/{questionId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteSurvey(@PathVariable QuestionId questionId) {
		this.questionRepository.deleteById(questionId);
	}

	@GetMapping(path = "/{questionId}/question_choices")
	public List<QuestionChoice> getQuestionChoices(@PathVariable QuestionId questionId) {
		return this.questionRepository.findAllQuestionChoicesByQuestionId(questionId);
	}

	@PostMapping(path = "/{questionId}/question_choices")
	@Transactional
	public ResponseEntity<QuestionChoice> postQuestionChoices(@PathVariable QuestionId questionId,
			@RequestBody QuestionChoiceRequest request, UriComponentsBuilder builder) {
		final Question question = this.questionRepository.findById(questionId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
					"The given question id is not found (%s)".formatted(questionId.asString())));
		if (question instanceof final SelectiveQuestion selectiveQuestion) {
			final QuestionChoiceId questionChoiceId = new QuestionChoiceId(this.tsidGenerator.generate());
			final QuestionChoice questionChoice = request.toQuestionChoice(questionChoiceId);
			this.questionRepository.updateQuestionChoices(selectiveQuestion.addQuestionChoice(questionChoice));
			final URI location = builder.replacePath("/{questionId}/question_choices/{questionChoiceId}")
				.build(questionId.asString(), questionChoiceId.asString());
			return ResponseEntity.created(location).body(questionChoice);
		}
		else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The given question is selective.");
		}
	}

	@GetMapping(path = "/{questionId}/question_choices/{questionChoiceId}")
	public QuestionChoice getQuestionChoice(@PathVariable QuestionId questionId,
			@PathVariable QuestionChoiceId questionChoiceId) {
		return this.questionRepository.findQuestionChoiceByQuestionIdAndId(questionId, questionChoiceId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
					"The requested question choice is not found (questionId = %s, questionChoiceId = %s)"
						.formatted(questionId.asString(), questionChoiceId.asString())));
	}

	@DeleteMapping(path = "/{questionId}/question_choices/{questionChoiceId}")
	@Transactional
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteQuestionChoice(@PathVariable QuestionId questionId,
			@PathVariable QuestionChoiceId questionChoiceId) {
		final Question question = this.questionRepository.findById(questionId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
					"The given question id is not found (%s)".formatted(questionId.asString())));
		if (question instanceof final SelectiveQuestion selectiveQuestion) {
			this.questionRepository.updateQuestionChoices(selectiveQuestion.removeQuestionChoices(questionChoiceId));
		}
		else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The given question is selective.");
		}
	}

}
