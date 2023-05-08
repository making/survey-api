package am.ik.surveys.question.web;

import am.ik.surveys.question.Question;
import am.ik.surveys.question.QuestionChoice;
import am.ik.surveys.question.QuestionChoiceId;
import am.ik.surveys.question.QuestionId;
import am.ik.surveys.question.QuestionRepository;
import am.ik.surveys.question.SelectiveQuestion;
import am.ik.surveys.tsid.TsidGenerator;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class QuestionHandler {

	private final QuestionRepository questionRepository;

	private final TsidGenerator tsidGenerator;

	public QuestionHandler(QuestionRepository questionRepository, TsidGenerator tsidGenerator) {
		this.questionRepository = questionRepository;
		this.tsidGenerator = tsidGenerator;
	}

	@Transactional
	public QuestionChoice postQuestionChoices(QuestionId questionId, QuestionChoiceRequest request) {
		final Question question = this.questionRepository.findById(questionId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
					"The given question id is not found (%s)".formatted(questionId.asString())));
		if (question instanceof final SelectiveQuestion selectiveQuestion) {
			final QuestionChoiceId questionChoiceId = new QuestionChoiceId(this.tsidGenerator.generate());
			final QuestionChoice questionChoice = request.toQuestionChoice(questionChoiceId);
			this.questionRepository.updateQuestionChoices(selectiveQuestion.addQuestionChoice(questionChoice));
			return questionChoice;
		}
		else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The given question is selective.");
		}
	}

	@Transactional
	public void deleteQuestionChoice(QuestionId questionId, QuestionChoiceId questionChoiceId) {
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
