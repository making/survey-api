package am.ik.surveys;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import am.ik.surveys.answer.Answer;
import am.ik.surveys.answer.AnswerId;
import am.ik.surveys.answer.ChosenAnswer;
import am.ik.surveys.answer.ChosenItem;
import am.ik.surveys.answer.DescriptiveAnswer;
import am.ik.surveys.answer.Respondent;
import am.ik.surveys.answer.RespondentId;
import am.ik.surveys.question.DescriptiveQuestion;
import am.ik.surveys.question.Question;
import am.ik.surveys.question.QuestionChoice;
import am.ik.surveys.question.QuestionChoiceId;
import am.ik.surveys.question.QuestionId;
import am.ik.surveys.question.SelectiveQuestion;
import am.ik.surveys.questiongroup.QuestionGroup;
import am.ik.surveys.questiongroup.QuestionGroupId;
import am.ik.surveys.questiongroup.QuestionGroupQuestion;
import am.ik.surveys.questiongroup.QuestionGroupQuestionId;
import am.ik.surveys.survey.Survey;
import am.ik.surveys.survey.SurveyId;
import am.ik.surveys.survey.SurveyQuestionGroup;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class Fixtures {

	public static final List<Survey> surveys = List.of(
			new Survey(SurveyId.valueOf("0C6VQGH19C6HY"), "テストアンケート", OffsetDateTime.parse("2019-08-01T00:00:00+09:00"),
					OffsetDateTime.parse("2019-08-31T00:00:00+09:00")),
			new Survey(SurveyId.valueOf("0C7510JMXF9C2"), "Demo Survey",
					OffsetDateTime.parse("2019-10-01T00:00:00+09:00"),
					OffsetDateTime.parse("2019-10-05T00:00:00+09:00")));

	public static final Map<SurveyId, Survey> surveyMap = surveys.stream().collect(toMap(Survey::surveyId, identity()));

	public static final Survey s1 = surveys.get(0);

	public static final QuestionGroup qg1 = new QuestionGroup(QuestionGroupId.valueOf("0C6VQGH1DC6H1"), "テストアンケート",
			"default");

	public static final List<Question> questions = List.of(
			new SelectiveQuestion(QuestionId.valueOf("0C6VQGH1DC6HZ"), "この設計はいけてますか?",
					List.of(new QuestionChoice(QuestionChoiceId.valueOf("0C6WYJV4BG554"), "はい", 1),
							new QuestionChoice(QuestionChoiceId.valueOf("0C6WYJV4BG555"), "いいえ", 0)),
					1),
			new DescriptiveQuestion(QuestionId.valueOf("0C6VQGH1DC6J0"), "どういうところがいけてますか?"),
			new SelectiveQuestion(QuestionId.valueOf("0C6VQGH1DC6J1"), "他にも取り上げて欲しい設計がありますか?",
					List.of(new QuestionChoice(QuestionChoiceId.valueOf("0C6WYJV43G54Z"), "在庫", 0),
							new QuestionChoice(QuestionChoiceId.valueOf("0C6WYJV4BG550"), "カート", 0),
							new QuestionChoice(QuestionChoiceId.valueOf("0C6WYJV4BG551"), "お気に入り", 0),
							new QuestionChoice(QuestionChoiceId.valueOf("0C6WYJV4BG552"), "リコメンド", 0),
							new QuestionChoice(QuestionChoiceId.valueOf("0C6WYJV4BG553"), "その他", 0, true)),
					3));

	public static final Map<QuestionId, Question> questionMap = questions.stream()
		.collect(toMap(Question::questionId, identity()));

	public static final SelectiveQuestion q1 = (SelectiveQuestion) questions.get(0);

	public static final Question q2 = questions.get(1);

	public static final SelectiveQuestion q3 = (SelectiveQuestion) questions.get(2);

	public static final List<SurveyQuestionGroup> SURVEY_QUESTION_GROUPS = List
		.of(new SurveyQuestionGroup(s1.surveyId(), qg1.questionGroupId()));

	public static final SurveyQuestionGroup sqg1 = SURVEY_QUESTION_GROUPS.get(0);

	public static final List<QuestionGroupQuestion> QUESTION_GROUP_QUESTIONS = List.of(
			new QuestionGroupQuestion(new QuestionGroupQuestionId(qg1.questionGroupId(), q1.questionId()), true),
			new QuestionGroupQuestion(new QuestionGroupQuestionId(qg1.questionGroupId(), q2.questionId()), false),
			new QuestionGroupQuestion(new QuestionGroupQuestionId(qg1.questionGroupId(), q3.questionId()), true));

	public static final QuestionGroupQuestion qgq1 = QUESTION_GROUP_QUESTIONS.get(0);

	public static final QuestionGroupQuestion qgq2 = QUESTION_GROUP_QUESTIONS.get(1);

	public static final QuestionGroupQuestion qgq3 = QUESTION_GROUP_QUESTIONS.get(2);

	public static final List<Respondent> respondents = List.of(new Respondent(RespondentId.valueOf("0C74NQQQPY12Y")),
			new Respondent(RespondentId.valueOf("0C74NQQQTY12Z")),
			new Respondent(RespondentId.valueOf("0C74NQQQTY130")));

	static Respondent r1 = respondents.get(0);

	static Respondent r2 = respondents.get(1);

	static Respondent r3 = respondents.get(2);

	public static final List<Answer> answers = List.of(
			new ChosenAnswer(AnswerId.valueOf("0C74NQQQTY131"), s1.surveyId(), qg1.questionGroupId(), q1.questionId(),
					r1.respondentId(), List.of(new ChosenItem(q1.questionChoices().get(0).questionChoiceId()))),
			new DescriptiveAnswer(AnswerId.valueOf("0C74NQQQTY132"), s1.surveyId(), qg1.questionGroupId(),
					q2.questionId(), r1.respondentId(), "具体的なデータがあってわかりやすい"),
			new ChosenAnswer(AnswerId.valueOf("0C74NQQQTY133"), s1.surveyId(), qg1.questionGroupId(), q3.questionId(),
					r1.respondentId(), List.of(new ChosenItem(q3.questionChoices().get(0).questionChoiceId()))),
			new ChosenAnswer(AnswerId.valueOf("0C74NQQQTY134"), s1.surveyId(), qg1.questionGroupId(), q1.questionId(),
					r2.respondentId(), List.of(new ChosenItem(q3.questionChoices().get(1).questionChoiceId()))),
			new DescriptiveAnswer(AnswerId.valueOf("0C74NQQQTY135"), s1.surveyId(), qg1.questionGroupId(),
					q2.questionId(), r2.respondentId(), "ER図がわかりやすい"),
			new ChosenAnswer(AnswerId.valueOf("0C74NQQQTY136"), s1.surveyId(), qg1.questionGroupId(), q3.questionId(),
					r2.respondentId(), List.of(new ChosenItem(q3.questionChoices().get(3).questionChoiceId()))),
			new ChosenAnswer(AnswerId.valueOf("0C74NQQQTY137"), s1.surveyId(), qg1.questionGroupId(), q1.questionId(),
					r3.respondentId(), List.of(new ChosenItem(q1.questionChoices().get(0).questionChoiceId()))),
			new DescriptiveAnswer(AnswerId.valueOf("0C74NQQQTY138"), s1.surveyId(), qg1.questionGroupId(),
					q2.questionId(), r3.respondentId(), "ここまで複雑なモデルが必要なの"),
			new ChosenAnswer(AnswerId.valueOf("0C74NQQQTY139"), s1.surveyId(), qg1.questionGroupId(), q3.questionId(),
					r3.respondentId(), List.of(new ChosenItem(q3.questionChoices().get(4).questionChoiceId(), "検索"))));

	public static final Map<AnswerId, Answer> answerMap = answers.stream().collect(toMap(Answer::answerId, identity()));

	static Answer a1 = answers.get(0);

	static Answer a2 = answers.get(1);

	static Answer a3 = answers.get(2);

	static Answer a4 = answers.get(3);

	static Answer a5 = answers.get(4);

	static Answer a6 = answers.get(5);

	static Answer a7 = answers.get(6);

	static Answer a8 = answers.get(7);

	static Answer a9 = answers.get(8);

}
