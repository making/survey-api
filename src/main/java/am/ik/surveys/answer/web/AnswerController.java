package am.ik.surveys.answer.web;

import java.net.URI;
import java.util.List;

import am.ik.surveys.answer.Answer;
import am.ik.surveys.answer.AnswerId;
import am.ik.surveys.answer.AnswerRepository;
import am.ik.surveys.answer.RespondentId;
import am.ik.surveys.question.QuestionId;
import am.ik.surveys.survey.SurveyId;
import am.ik.surveys.tsid.TsidGenerator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
public class AnswerController {

	private final AnswerRepository answerRepository;

	private final TsidGenerator tsidGenerator;

	public AnswerController(AnswerRepository answerRepository, TsidGenerator tsidGenerator) {
		this.answerRepository = answerRepository;
		this.tsidGenerator = tsidGenerator;
	}

	@GetMapping(path = "/surveys/{surveyId}/answers")
	public List<Answer> getAnswersBySurveyId(@PathVariable SurveyId surveyId,
			@RequestParam(required = false) QuestionId questionId) {
		return this.answerRepository.findBySurveyId(surveyId);
	}

	@PostMapping(path = "/surveys/{surveyId}/answers")
	public ResponseEntity<Answer> postAnswers(@PathVariable SurveyId surveyId, @RequestBody AnswerRequest request,
			UriComponentsBuilder builder, Authentication authentication) {
		final AnswerId answerId = new AnswerId(this.tsidGenerator.generate());
		if (authentication != null) {
			request.setRespondentId(new RespondentId(authentication.getName()));
		}
		final Answer answer = request.toAnswer(answerId, surveyId);
		this.answerRepository.insert(answer);
		final URI location = builder.replacePath("/answers/{answerId}").build(answerId.asString());
		return ResponseEntity.created(location).body(answer);
	}

	@GetMapping(path = "/answers/{answerId}")
	public Answer getAnswer(@PathVariable AnswerId answerId) {
		return this.answerRepository.findById(answerId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
					"The given answer id is not found (%s)".formatted(answerId.asString())));
	}

	@DeleteMapping(path = "/answers/{answerId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteAnswer(@PathVariable AnswerId answerId) {
		this.answerRepository.deleteById(answerId);
	}

}
