package am.ik.surveys.answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import am.ik.surveys.question.QuestionChoiceId;
import am.ik.surveys.question.QuestionId;
import am.ik.surveys.questiongroup.QuestionGroupId;
import am.ik.surveys.survey.SurveyId;
import am.ik.surveys.util.FileLoader;
import org.mybatis.scripting.thymeleaf.SqlGenerator;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AnswerRepository {

	private final JdbcClient jdbcClient;

	private final NamedParameterJdbcTemplate jdbcTemplate;

	private final SqlGenerator sqlGenerator;

	public AnswerRepository(JdbcClient jdbcClient, NamedParameterJdbcTemplate jdbcTemplate, SqlGenerator sqlGenerator) {
		this.jdbcClient = jdbcClient;
		this.jdbcTemplate = jdbcTemplate;
		this.sqlGenerator = sqlGenerator;
	}

	private final ResultSetExtractor<List<Answer>> resultSetExtractor = rs -> {
		final List<Answer> answers = new ArrayList<>();
		AnswerId previousAnswerId = null;
		ChosenAnswer chosenAnswer = null;
		while (rs.next()) {
			final AnswerId answerId = AnswerId.valueOf(rs.getLong("answer_id"));
			final SurveyId surveyId = SurveyId.valueOf(rs.getLong("survey_id"));
			final QuestionGroupId questionGroupId = QuestionGroupId.valueOf(rs.getLong("question_group_id"));
			final QuestionId questionId = QuestionId.valueOf(rs.getLong("question_id"));
			final RespondentId respondentId = RespondentId.valueOf(rs.getString("respondent_id"));
			final String answerText = rs.getString("answer_text");
			final long l = rs.getLong("question_choice_id");
			if (l != 0) {
				if (chosenAnswer == null || !answerId.equals(previousAnswerId)) {
					chosenAnswer = new ChosenAnswer(answerId, surveyId, questionGroupId, questionId, respondentId,
							new ArrayList<>());
					answers.add(chosenAnswer);
				}
				chosenAnswer.chosenItems().add(new ChosenItem(QuestionChoiceId.valueOf(l), answerText));
			}
			else {
				answers.add(new DescriptiveAnswer(answerId, surveyId, questionGroupId, questionId, respondentId,
						answerText));
			}
			previousAnswerId = answerId;
		}
		return answers;
	};

	@Transactional(readOnly = true)
	public Optional<Answer> findById(AnswerId answerId) {
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("answerId", answerId.asLong());
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/answer/findById.sql"),
				params.getValues(), params::addValue);
		return DataAccessUtils
			.optionalResult(this.jdbcClient.sql(sql).paramSource(params).query(this.resultSetExtractor));
	}

	@Transactional(readOnly = true)
	public List<Answer> findBySurveyId(SurveyId surveyId) {
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("surveyId", surveyId.asLong());
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/answer/findAllBySurveyId.sql"),
				params.getValues(), params::addValue);
		// return
		// this.jdbcClient.sql(sql).paramSource(params).query(this.resultSetExtractor);
		return this.jdbcTemplate.query(sql, params, resultSetExtractor);
	}

	public int insert(Answer answer) {
		final MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("answerId", answer.answerId().asLong())
			.addValue("surveyId", answer.surveyId().asLong())
			.addValue("questionGroupId", answer.questionGroupId().asLong())
			.addValue("questionId", answer.questionId().asLong())
			.addValue("respondentId", answer.respondentId().asString());
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/answer/insertAnswer.sql"),
				params.getValues(), params::addValue);
		final int update = this.jdbcClient.sql(sql).paramSource(params).update();
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
			.addValue("answerId", answer.answerId().asLong())
			.addValue("answerText", answer.answerText());
		final String sql = this.sqlGenerator.generate(
				FileLoader.loadSqlAsString("sql/answer/insertDescriptiveAnswer.sql"), params.getValues(),
				params::addValue);
		return this.jdbcClient.sql(sql).paramSource(params).update();
	}

	int insertChosenAnswer(ChosenAnswer answer) {
		if (answer.chosenItems().isEmpty()) {
			return 0;
		}
		final MapSqlParameterSource[] params = answer.chosenItems()
			.stream()
			.map(chosenItem -> new MapSqlParameterSource().addValue("answerId", answer.answerId().asLong())
				.addValue("questionChoiceId", chosenItem.questionChoiceId().asLong())
				.addValue("answerText", chosenItem.answerText()))
			.toArray(MapSqlParameterSource[]::new);
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/answer/insertChosenAnswer.sql"),
				params[0].getValues(), params[0]::addValue);
		return Arrays.stream(this.jdbcTemplate.batchUpdate(sql, params)).sum();
	}

	public int deleteById(AnswerId answerId) {
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("answerId", answerId.asLong());
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/answer/deleteAnswerById.sql"),
				params.getValues(), params::addValue);
		return this.jdbcClient.sql(sql).paramSource(params).update();
	}

}
