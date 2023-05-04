package am.ik.surveys.surveyquestion;

import java.util.List;
import java.util.Objects;

import am.ik.surveys.question.QuestionId;
import am.ik.surveys.survey.SurveyId;
import am.ik.surveys.util.FileLoader;
import org.mybatis.scripting.thymeleaf.SqlGenerator;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SurveyQuestionRepository {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	private final SqlGenerator sqlGenerator;

	private final RowMapper<SurveyQuestion> rowMapper = (rs, rowNum) -> {
		final SurveyId surveyId = SurveyId.valueOf(rs.getString("survey_id"));
		final QuestionId questionId = QuestionId.valueOf(rs.getString("question_id"));
		final boolean required = rs.getBoolean("required");
		return new SurveyQuestion(new SurveyQuestionId(surveyId, questionId), required);
	};

	public SurveyQuestionRepository(NamedParameterJdbcTemplate jdbcTemplate, SqlGenerator sqlGenerator) {
		this.jdbcTemplate = jdbcTemplate;
		this.sqlGenerator = sqlGenerator;
	}

	@Transactional(readOnly = true)
	public List<SurveyQuestion> findBySurveyId(SurveyId surveyId) {
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("surveyId", surveyId.asString());
		final String sql = this.sqlGenerator.generate(
				FileLoader.loadSqlAsString("sql/surveyquestion/findBySurveyId.sql"), params.getValues(),
				params::addValue);
		return this.jdbcTemplate.query(sql, params, rowMapper);
	}

	long countQuestionById(QuestionId questionId) {
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("questionId", questionId.asString());
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/question/countById.sql"),
				params.getValues(), params::addValue);
		return Objects.requireNonNullElse(this.jdbcTemplate.queryForObject(sql, params, Long.class), 0L);
	}

	public int insert(SurveyQuestion surveyQuestion) {
		final SurveyQuestionId surveyQuestionId = surveyQuestion.surveyQuestionId();
		// check constraint manually
		if (this.countQuestionById(surveyQuestionId.questionId()) == 0) {
			throw new DataIntegrityViolationException(
					"Key (question_id)=(%s) is not present in table \"question\" or \"selective_question\""
						.formatted(surveyQuestionId.questionId().asString()));
		}
		final MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("surveyId", surveyQuestionId.surveyId().asString())
			.addValue("questionId", surveyQuestionId.questionId().asString())
			.addValue("required", surveyQuestion.required());
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/surveyquestion/insert.sql"),
				params.getValues(), params::addValue);
		return this.jdbcTemplate.update(sql, params);
	}

	public int deleteById(SurveyQuestionId surveyQuestionId) {
		final MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("surveyId", surveyQuestionId.surveyId().asString())
			.addValue("questionId", surveyQuestionId.questionId().asString());
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/surveyquestion/deleteById.sql"),
				params.getValues(), params::addValue);
		return this.jdbcTemplate.update(sql, params);
	}

	public int deleteBySurveyId(SurveyId surveyId) {
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("surveyId", surveyId.asString());
		final String sql = this.sqlGenerator.generate(
				FileLoader.loadSqlAsString("sql/surveyquestion/deleteBySurveyId.sql"), params.getValues(),
				params::addValue);
		return this.jdbcTemplate.update(sql, params);
	}

}
