package am.ik.surveys.questiongroup.web;

import java.util.List;

import am.ik.surveys.question.QuestionId;
import am.ik.surveys.questiongroup.QuestionGroupId;
import am.ik.surveys.questiongroup.QuestionGroupQuestion;
import am.ik.surveys.questiongroup.QuestionGroupQuestionId;
import am.ik.surveys.questiongroup.QuestionGroupQuestionRepository;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/question_groups/{questionGroupId}/question_group_questions")
public class QuestionGroupQuestionController {

	private final QuestionGroupQuestionRepository questionGroupQuestionRepository;

	public QuestionGroupQuestionController(QuestionGroupQuestionRepository questionGroupQuestionRepository) {
		this.questionGroupQuestionRepository = questionGroupQuestionRepository;
	}

	@GetMapping(path = "")
	public List<QuestionGroupQuestion> getQuestionGroupQuestions(@PathVariable QuestionGroupId questionGroupId) {
		return this.questionGroupQuestionRepository.findByQuestionGroupId(questionGroupId);
	}

	@DeleteMapping(path = "")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteQuestionGroupQuestions(@PathVariable QuestionGroupId questionGroupId) {
		this.questionGroupQuestionRepository.deleteByQuestionGroupId(questionGroupId);
	}

	@PutMapping(path = "/{questionId}")
	public QuestionGroupQuestion postQuestionGroupQuestions(@PathVariable QuestionGroupId questionGroupId,
			@PathVariable QuestionId questionId, @RequestBody QuestionGroupQuestionRequest request) {
		final QuestionGroupQuestionId questionGroupQuestionId = new QuestionGroupQuestionId(questionGroupId,
				questionId);
		final QuestionGroupQuestion questionGroupQuestion = request.toQuestionGroupQuestion(questionGroupQuestionId);
		this.questionGroupQuestionRepository.insert(questionGroupQuestion);
		return questionGroupQuestion;
	}

	@DeleteMapping(path = "/{questionId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteQuestionGroupQuestion(@PathVariable QuestionGroupId questionGroupId,
			@PathVariable QuestionId questionId) {
		final QuestionGroupQuestionId questionGroupQuestionId = new QuestionGroupQuestionId(questionGroupId,
				questionId);
		this.questionGroupQuestionRepository.deleteById(questionGroupQuestionId);
	}

}
