package am.ik.surveys.questiongroup.web;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import am.ik.surveys.question.Question;
import am.ik.surveys.question.QuestionId;
import am.ik.surveys.question.QuestionRepository;
import am.ik.surveys.questiongroup.QuestionGroup;
import am.ik.surveys.questiongroup.QuestionGroupId;
import am.ik.surveys.questiongroup.QuestionGroupQuestion;
import am.ik.surveys.questiongroup.QuestionGroupQuestionRepository;
import am.ik.surveys.questiongroup.QuestionGroupRepository;
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

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toUnmodifiableSet;

@RestController
@RequestMapping(path = "/question_groups")
public class QuestionGroupController {

	private final QuestionGroupRepository questionGroupRepository;

	private final QuestionGroupQuestionRepository questionGroupQuestionRepository;

	private final QuestionRepository questionRepository;

	private final TsidGenerator tsidGenerator;

	public QuestionGroupController(QuestionGroupRepository questionGroupRepository,
			QuestionGroupQuestionRepository questionGroupQuestionRepository, QuestionRepository questionRepository,
			TsidGenerator tsidGenerator) {
		this.questionGroupRepository = questionGroupRepository;
		this.questionGroupQuestionRepository = questionGroupQuestionRepository;
		this.questionRepository = questionRepository;
		this.tsidGenerator = tsidGenerator;
	}

	@GetMapping(path = "")
	public List<QuestionGroup> getQuestionGroups() {
		return this.questionGroupRepository.findAll();
	}

	@PostMapping(path = "")
	public ResponseEntity<QuestionGroup> postQuestionGroups(@RequestBody QuestionGroupRequest request,
			UriComponentsBuilder builder) {
		final QuestionGroupId questionChoiceId = new QuestionGroupId(this.tsidGenerator.generate());
		final QuestionGroup questionGroup = request.toQuestionGroup(questionChoiceId);
		this.questionGroupRepository.insert(questionGroup);
		final URI location = builder.replacePath("/question_groups/{questionGroupId}")
			.build(questionChoiceId.asString());
		return ResponseEntity.created(location).body(questionGroup);
	}

	@GetMapping(path = "/{questionGroupId}")
	@Transactional
	public QuestionGroupResponse getQuestionGroup(@PathVariable QuestionGroupId questionGroupId) {
		final QuestionGroup questionGroup = this.questionGroupRepository.findById(questionGroupId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
					"The given question group id is not found (%s)".formatted(questionGroupId.asString())));
		final List<QuestionGroupQuestion> questionGroupQuestions = this.questionGroupQuestionRepository
			.findByQuestionGroupId(questionGroupId);
		final Set<QuestionId> questionIds = questionGroupQuestions.stream()
			.map(qgq -> qgq.questionGroupQuestionId().questionId())
			.collect(toUnmodifiableSet());
		final Map<QuestionId, Question> questionMap = this.questionRepository.findByIds(questionIds)
			.stream()
			.collect(toMap(Question::questionId, identity()));
		final List<QuestionGroupResponse.Question> questions = questionGroupQuestions.stream()
			.map(qgq -> new QuestionGroupResponse.Question(questionMap.get(qgq.questionGroupQuestionId().questionId()),
					qgq.required()))
			.toList();
		return new QuestionGroupResponse(questionGroup, questions);
	}

	@DeleteMapping(path = "/{questionGroupId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteQuestionGroup(@PathVariable QuestionGroupId questionGroupId) {
		this.questionGroupRepository.deleteById(questionGroupId);
	}

}
