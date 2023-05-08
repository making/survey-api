package am.ik.surveys.questiongroup;

import java.util.List;

import am.ik.surveys.question.QuestionId;
import am.ik.surveys.util.FileLoader;
import org.mybatis.scripting.thymeleaf.SqlGenerator;

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

	public int insert(QuestionGroupQuestion questionGroupQuestion) {
		final QuestionGroupQuestionId questionGroupQuestionId = questionGroupQuestion.questionGroupQuestionId();
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
