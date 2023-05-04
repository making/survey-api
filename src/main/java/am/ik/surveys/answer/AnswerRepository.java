package am.ik.surveys.answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import am.ik.surveys.question.QuestionChoiceId;
import am.ik.surveys.question.QuestionId;
import am.ik.surveys.survey.SurveyId;
import am.ik.surveys.surveyquestion.SurveyQuestionId;
import am.ik.surveys.util.FileLoader;
import org.mybatis.scripting.thymeleaf.SqlGenerator;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AnswerRepository {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	private final SqlGenerator sqlGenerator;

	public AnswerRepository(NamedParameterJdbcTemplate jdbcTemplate, SqlGenerator sqlGenerator) {
		this.jdbcTemplate = jdbcTemplate;
		this.sqlGenerator = sqlGenerator;
	}

	private final ResultSetExtractor<List<Answer>> resultSetExtractor = rs -> {
		final List<Answer> answers = new ArrayList<>();
		AnswerId previousAnswerId = null;
		ChosenAnswer chosenAnswer = null;
		while (rs.next()) {
			final AnswerId answerId = AnswerId.valueOf(rs.getString("answer_id"));
			final SurveyId surveyId = SurveyId.valueOf(rs.getString("survey_id"));
			final QuestionId questionId = QuestionId.valueOf(rs.getString("question_id"));
			final SurveyQuestionId surveyQuestionId = new SurveyQuestionId(surveyId, questionId);
			final RespondentId respondentId = RespondentId.valueOf(rs.getString("respondent_id"));
			final String answerText = rs.getString("answer_text");
			final String s = rs.getString("question_choice_id");
			if (s != null) {
				if (chosenAnswer == null || !answerId.equals(previousAnswerId)) {
					chosenAnswer = new ChosenAnswer(answerId, surveyQuestionId, respondentId, new ArrayList<>());
					answers.add(chosenAnswer);
				}
				chosenAnswer.chosenItems().add(new ChosenItem(QuestionChoiceId.valueOf(s), answerText));
			}
			else {
				answers.add(new DescriptiveAnswer(answerId, surveyQuestionId, respondentId, answerText));
			}
			previousAnswerId = answerId;
		}
		return answers;
	};

	@Transactional(readOnly = true)
	public Optional<Answer> findById(AnswerId answerId) {
		final MapSqlParameterSource params = new MapSqlParameterSource()
				.addValue("answerId", answerId.asString());
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/answer/findById.sql"),
				params.getValues(), params::addValue);
		return Optional.ofNullable(DataAccessUtils.singleResult(this.jdbcTemplate.query(sql, params, resultSetExtractor)));
	}

	@Transactional(readOnly = true)
	public List<Answer> findBySurveyId(SurveyId surveyId) {
		final MapSqlParameterSource params = new MapSqlParameterSource()
				.addValue("surveyId", surveyId.asString());
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/answer/findAllBySurveyId.sql"),
				params.getValues(), params::addValue);
		return this.jdbcTemplate.query(sql, params, resultSetExtractor);
	}

	public int insert(Answer answer) {
		final MapSqlParameterSource params = new MapSqlParameterSource()
				.addValue("answerId", answer.answerId().asString())
				.addValue("surveyId", answer.surveyQuestionId().surveyId().asString())
				.addValue("questionId", answer.surveyQuestionId().questionId().asString())
				.addValue("respondentId", answer.respondentId().asString());
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/answer/insertAnswer.sql"),
				params.getValues(), params::addValue);
		final int update = this.jdbcTemplate.update(sql, params);
		if (answer instanceof final DescriptiveAnswer descriptiveAnswer) {
			this.insertDescriptiveAnswer(descriptiveAnswer);
		}
		else if (answer instanceof final ChosenAnswer chosenAnswer) {
			this.insertChosenAnswer(chosenAnswer);
		}
		return update;
	}

	int insertDescriptiveAnswer(DescriptiveAnswer answer) {
		final MapSqlParameterSource params = new MapSqlParameterSource()
				.addValue("answerId", answer.answerId().asString())
				.addValue("answerText", answer.answerText());
		final String sql = this.sqlGenerator.generate(
				FileLoader.loadSqlAsString("sql/answer/insertDescriptiveAnswer.sql"), params.getValues(),
				params::addValue);
		return this.jdbcTemplate.update(sql, params);
	}

	int insertChosenAnswer(ChosenAnswer answer) {
		if (answer.chosenItems().isEmpty()) {
			return 0;
		}
		final MapSqlParameterSource[] params = answer.chosenItems()
				.stream()
				.map(chosenItem -> new MapSqlParameterSource().addValue("answerId", answer.answerId().asString())
						.addValue("questionChoiceId", chosenItem.questionChoiceId().asString())
						.addValue("answerText", chosenItem.answerText()))
				.toArray(MapSqlParameterSource[]::new);
		final String sql = this.sqlGenerator.generate(
				FileLoader.loadSqlAsString("sql/answer/insertChosenAnswer.sql"), params[0].getValues(),
				params[0]::addValue);
		return Arrays.stream(this.jdbcTemplate.batchUpdate(sql, params)).sum();
	}

	public int deleteById(AnswerId answerId) {
		final MapSqlParameterSource params = new MapSqlParameterSource()
				.addValue("answerId", answerId.asString());
		final String sql = this.sqlGenerator.generate(
				FileLoader.loadSqlAsString("sql/answer/deleteAnswerById.sql"), params.getValues(),
				params::addValue);
		return this.jdbcTemplate.update(sql, params);
	}
}
