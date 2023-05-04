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
import am.ik.surveys.question.DefaultQuestion;
import am.ik.surveys.question.Question;
import am.ik.surveys.question.QuestionChoice;
import am.ik.surveys.question.QuestionChoiceId;
import am.ik.surveys.question.QuestionId;
import am.ik.surveys.question.SelectiveQuestion;
import am.ik.surveys.survey.Survey;
import am.ik.surveys.survey.SurveyId;
import am.ik.surveys.surveyquestion.SurveyQuestion;
import am.ik.surveys.surveyquestion.SurveyQuestionId;

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

	public static final List<Question> questions = List.of(
			new SelectiveQuestion(QuestionId.valueOf("0C6VQGH1DC6HZ"), "この設計はいけてますか?",
					List.of(new QuestionChoice(QuestionChoiceId.valueOf("0C6WYJV4BG554"), "はい"),
							new QuestionChoice(QuestionChoiceId.valueOf("0C6WYJV4BG555"), "いいえ")),
					1),
			new DefaultQuestion(QuestionId.valueOf("0C6VQGH1DC6J0"), "どういうところがいけてますか?"),
			new SelectiveQuestion(QuestionId.valueOf("0C6VQGH1DC6J1"), "他にも取り上げて欲しい設計がありますか?",
					List.of(new QuestionChoice(QuestionChoiceId.valueOf("0C6WYJV43G54Z"), "在庫"),
							new QuestionChoice(QuestionChoiceId.valueOf("0C6WYJV4BG550"), "カート"),
							new QuestionChoice(QuestionChoiceId.valueOf("0C6WYJV4BG551"), "お気に入り"),
							new QuestionChoice(QuestionChoiceId.valueOf("0C6WYJV4BG552"), "リコメンド"),
							new QuestionChoice(QuestionChoiceId.valueOf("0C6WYJV4BG553"), "その他", true)),
					3));

	public static final Map<QuestionId, Question> questionMap = questions.stream()
		.collect(toMap(Question::questionId, identity()));

	public static final SelectiveQuestion q1 = (SelectiveQuestion) questions.get(0);

	public static final Question q2 = questions.get(1);

	public static final SelectiveQuestion q3 = (SelectiveQuestion) questions.get(2);

	public static final List<SurveyQuestion> surveyQuestions = List.of(
			new SurveyQuestion(new SurveyQuestionId(s1.surveyId(), q1.questionId()), true),
			new SurveyQuestion(new SurveyQuestionId(s1.surveyId(), q2.questionId()), false),
			new SurveyQuestion(new SurveyQuestionId(s1.surveyId(), q3.questionId()), true));

	public static final SurveyQuestion sq1 = surveyQuestions.get(0);

	public static final SurveyQuestion sq2 = surveyQuestions.get(1);

	public static final SurveyQuestion sq3 = surveyQuestions.get(2);

	public static final List<Respondent> respondents = List.of(new Respondent(RespondentId.valueOf("0C74NQQQPY12Y")),
			new Respondent(RespondentId.valueOf("0C74NQQQTY12Z")),
			new Respondent(RespondentId.valueOf("0C74NQQQTY130")));

	static Respondent r1 = respondents.get(0);

	static Respondent r2 = respondents.get(1);

	static Respondent r3 = respondents.get(2);

	public static final List<Answer> answers = List.of(
			new ChosenAnswer(AnswerId.valueOf("0C74NQQQTY131"), sq1.surveyQuestionId(), r1.respondentId(),
					List.of(new ChosenItem(q1.questionChoices().get(0).questionChoiceId()))),
			new DescriptiveAnswer(AnswerId.valueOf("0C74NQQQTY132"), sq2.surveyQuestionId(), r1.respondentId(),
					"具体的なデータがあってわかりやすい"),
			new ChosenAnswer(AnswerId.valueOf("0C74NQQQTY133"), sq3.surveyQuestionId(), r1.respondentId(),
					List.of(new ChosenItem(q3.questionChoices().get(0).questionChoiceId()))),
			new ChosenAnswer(AnswerId.valueOf("0C74NQQQTY134"), sq1.surveyQuestionId(), r2.respondentId(),
					List.of(new ChosenItem(q3.questionChoices().get(1).questionChoiceId()))),
			new DescriptiveAnswer(AnswerId.valueOf("0C74NQQQTY135"), sq2.surveyQuestionId(), r2.respondentId(),
					"ER図がわかりやすい"),
			new ChosenAnswer(AnswerId.valueOf("0C74NQQQTY136"), sq3.surveyQuestionId(), r2.respondentId(),
					List.of(new ChosenItem(q3.questionChoices().get(3).questionChoiceId()))),
			new ChosenAnswer(AnswerId.valueOf("0C74NQQQTY137"), sq1.surveyQuestionId(), r3.respondentId(),
					List.of(new ChosenItem(q1.questionChoices().get(0).questionChoiceId()))),
			new DescriptiveAnswer(AnswerId.valueOf("0C74NQQQTY138"), sq2.surveyQuestionId(), r3.respondentId(),
					"ここまで複雑なモデルが必要なの"),
			new ChosenAnswer(AnswerId.valueOf("0C74NQQQTY139"), sq3.surveyQuestionId(), r3.respondentId(),
					List.of(new ChosenItem(q3.questionChoices().get(4).questionChoiceId(), "検索"))));

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
