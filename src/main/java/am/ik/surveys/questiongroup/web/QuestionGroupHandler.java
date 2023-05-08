package am.ik.surveys.questiongroup.web;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import am.ik.surveys.question.Question;
import am.ik.surveys.question.QuestionId;
import am.ik.surveys.question.QuestionRepository;
import am.ik.surveys.questiongroup.QuestionGroup;
import am.ik.surveys.questiongroup.QuestionGroupId;
import am.ik.surveys.questiongroup.QuestionGroupQuestion;
import am.ik.surveys.questiongroup.QuestionGroupQuestionRepository;
import am.ik.surveys.questiongroup.QuestionGroupRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toUnmodifiableSet;

@Service
public class QuestionGroupHandler {

	private final QuestionGroupRepository questionGroupRepository;

	private final QuestionGroupQuestionRepository questionGroupQuestionRepository;

	private final QuestionRepository questionRepository;

	public QuestionGroupHandler(QuestionGroupRepository questionGroupRepository,
			QuestionGroupQuestionRepository questionGroupQuestionRepository, QuestionRepository questionRepository) {
		this.questionGroupRepository = questionGroupRepository;
		this.questionGroupQuestionRepository = questionGroupQuestionRepository;
		this.questionRepository = questionRepository;
	}

	@Transactional(readOnly = true)
	public QuestionGroupResponse getQuestionGroup(QuestionGroupId questionGroupId) {
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

	@Transactional(readOnly = true)
	public List<QuestionGroupResponse> getQuestionGroups(Set<QuestionGroupId> questionGroupIds) {
		final List<QuestionGroupQuestion> questionGroupQuestions = this.questionGroupQuestionRepository
			.findByQuestionGroupIds(questionGroupIds);
		final Map<QuestionGroupId, List<QuestionGroupQuestion>> qgqsMap = questionGroupQuestions.stream()
			.collect(groupingBy(qgq -> qgq.questionGroupQuestionId().questionGroupId()));
		final Set<QuestionId> questionIds = questionGroupQuestions.stream()
			.map(qgq -> qgq.questionGroupQuestionId().questionId())
			.collect(toUnmodifiableSet());
		final Map<QuestionId, Question> questionMap = this.questionRepository.findByIds(questionIds)
			.stream()
			.collect(Collectors.toMap(Question::questionId, identity()));
		return this.questionGroupRepository.findByIds(questionGroupIds).stream().map(questionGroup -> {
			final List<QuestionGroupResponse.Question> questions = qgqsMap.get(questionGroup.questionGroupId())
				.stream()
				.map(qgq -> new QuestionGroupResponse.Question(
						questionMap.get(qgq.questionGroupQuestionId().questionId()), qgq.required()))
				.toList();
			return new QuestionGroupResponse(questionGroup, questions);
		}).toList();
	}

}
