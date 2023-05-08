package am.ik.surveys.survey.web;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import am.ik.surveys.question.Question;
import am.ik.surveys.question.QuestionId;
import am.ik.surveys.question.QuestionRepository;
import am.ik.surveys.questiongroup.QuestionGroupId;
import am.ik.surveys.questiongroup.QuestionGroupQuestion;
import am.ik.surveys.questiongroup.QuestionGroupQuestionRepository;
import am.ik.surveys.questiongroup.QuestionGroupRepository;
import am.ik.surveys.survey.Survey;
import am.ik.surveys.survey.SurveyId;
import am.ik.surveys.survey.SurveyQuestionGroup;
import am.ik.surveys.survey.SurveyQuestionGroupRepository;
import am.ik.surveys.survey.SurveyRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toUnmodifiableSet;

@Service
public class SurveyHandler {

	private final SurveyRepository surveyRepository;

	private final SurveyQuestionGroupRepository surveyQuestionGroupRepository;

	private final QuestionGroupRepository questionGroupRepository;

	private final QuestionGroupQuestionRepository questionGroupQuestionRepository;

	private final QuestionRepository questionRepository;

	public SurveyHandler(SurveyRepository surveyRepository, SurveyQuestionGroupRepository surveyQuestionGroupRepository,
			QuestionGroupRepository questionGroupRepository,
			QuestionGroupQuestionRepository questionGroupQuestionRepository, QuestionRepository questionRepository) {
		this.surveyRepository = surveyRepository;
		this.surveyQuestionGroupRepository = surveyQuestionGroupRepository;
		this.questionGroupRepository = questionGroupRepository;
		this.questionGroupQuestionRepository = questionGroupQuestionRepository;
		this.questionRepository = questionRepository;
	}

	@Transactional
	public SurveyResponse getSurvey(SurveyId surveyId, boolean includeQuestions) {
		final Survey survey = this.surveyRepository.findById(surveyId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
					"The given survey id is not found (" + surveyId.asString() + ")"));
		final List<SurveyQuestionGroup> surveyQuestionGroups = this.surveyQuestionGroupRepository
			.findBySurveyId(surveyId);
		final Set<QuestionGroupId> questionGroupIds = surveyQuestionGroups.stream()
			.map(SurveyQuestionGroup::questionGroupId)
			.collect(toUnmodifiableSet());
		final List<QuestionGroupQuestion> questionGroupQuestions = includeQuestions
				? this.questionGroupQuestionRepository.findByQuestionGroupIds(questionGroupIds) : List.of();
		final Map<QuestionGroupId, List<QuestionGroupQuestion>> qgqsMap = questionGroupQuestions.stream()
			.collect(groupingBy(qgq -> qgq.questionGroupQuestionId().questionGroupId()));
		final Set<QuestionId> questionIds = questionGroupQuestions.stream()
			.map(qgq -> qgq.questionGroupQuestionId().questionId())
			.collect(toUnmodifiableSet());
		final Map<QuestionId, Question> questionMap = includeQuestions ? this.questionRepository.findByIds(questionIds)
			.stream()
			.collect(Collectors.toMap(Question::questionId, identity())) : Map.of();
		final List<SurveyResponse.QuestionGroup> questionGroups = this.questionGroupRepository
			.findByIds(questionGroupIds)
			.stream()
			.map(qg -> {
				if (includeQuestions) {
					final List<SurveyResponse.QuestionGroup.Question> questions = qgqsMap.get(qg.questionGroupId())
						.stream()
						.map(qgq -> {
							final Question question = questionMap.get(qgq.questionGroupQuestionId().questionId());
							return new SurveyResponse.QuestionGroup.Question(question, qgq.required());
						})
						.toList();
					return new SurveyResponse.QuestionGroup(qg, questions);
				}
				else {
					return new SurveyResponse.QuestionGroup(qg);
				}
			})
			.toList();
		return new SurveyResponse(survey, questionGroups);
	}

}
