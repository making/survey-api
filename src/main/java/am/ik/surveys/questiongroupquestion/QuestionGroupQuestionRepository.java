package am.ik.surveys.questiongroupquestion;

import java.util.List;
import java.util.Objects;

import am.ik.surveys.question.QuestionId;
import am.ik.surveys.questiongroup.QuestionGroupId;
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
public class QuestionGroupQuestionRepository {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	private final SqlGenerator sqlGenerator;

	private final RowMapper<QuestionGroupQuestion> rowMapper = (rs, rowNum) -> {
		final QuestionGroupId questionGroupId = QuestionGroupId.valueOf(rs.getString("question_group_id"));
		final QuestionId questionId = QuestionId.valueOf(rs.getString("question_id"));
		final boolean required = rs.getBoolean("required");
		return new QuestionGroupQuestion(new QuestionGroupQuestionId(questionGroupId, questionId), required);
	};

	public QuestionGroupQuestionRepository(NamedParameterJdbcTemplate jdbcTemplate, SqlGenerator sqlGenerator) {
		this.jdbcTemplate = jdbcTemplate;
		this.sqlGenerator = sqlGenerator;
	}

	@Transactional(readOnly = true)
	public List<QuestionGroupQuestion> findByQuestionGroupId(QuestionGroupId questionGroupId) {
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("questionGroupId",
				questionGroupId.asString());
		final String sql = this.sqlGenerator.generate(
				FileLoader.loadSqlAsString("sql/questiongroupquestion/findByQuestionGroupId.sql"), params.getValues(),
				params::addValue);
		return this.jdbcTemplate.query(sql, params, rowMapper);
	}

	long countQuestionById(QuestionId questionId) {
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("questionId", questionId.asString());
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/question/countById.sql"),
				params.getValues(), params::addValue);
		return Objects.requireNonNullElse(this.jdbcTemplate.queryForObject(sql, params, Long.class), 0L);
	}

	public int insert(QuestionGroupQuestion questionGroupQuestion) {
		final QuestionGroupQuestionId questionGroupQuestionId = questionGroupQuestion.questionGroupQuestionId();
		// check constraint manually
		if (this.countQuestionById(questionGroupQuestionId.questionId()) == 0) {
			throw new DataIntegrityViolationException(
					"Key (question_id)=(%s) is not present in table \"question\" or \"selective_question\""
						.formatted(questionGroupQuestionId.questionId().asString()));
		}
		final MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("questionGroupId", questionGroupQuestionId.questionGroupId().asString())
			.addValue("questionId", questionGroupQuestionId.questionId().asString())
			.addValue("required", questionGroupQuestion.required());
		final String sql = this.sqlGenerator.generate(
				FileLoader.loadSqlAsString("sql/questiongroupquestion/insert.sql"), params.getValues(),
				params::addValue);
		return this.jdbcTemplate.update(sql, params);
	}

	public int deleteById(QuestionGroupQuestionId questionGroupQuestionId) {
		final MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("questionGroupId", questionGroupQuestionId.questionGroupId().asString())
			.addValue("questionId", questionGroupQuestionId.questionId().asString());
		final String sql = this.sqlGenerator.generate(
				FileLoader.loadSqlAsString("sql/questiongroupquestion/deleteById.sql"), params.getValues(),
				params::addValue);
		return this.jdbcTemplate.update(sql, params);
	}

	public int deleteByQuestionGroupId(QuestionGroupId questionGroupId) {
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("questionGroupId",
				questionGroupId.asString());
		final String sql = this.sqlGenerator.generate(
				FileLoader.loadSqlAsString("sql/questiongroupquestion/deleteByQuestionGroupId.sql"), params.getValues(),
				params::addValue);
		return this.jdbcTemplate.update(sql, params);
	}

}
