package am.ik.surveys.questiongroup;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import am.ik.surveys.question.QuestionId;
import am.ik.surveys.util.FileLoader;
import org.mybatis.scripting.thymeleaf.SqlGenerator;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class QuestionGroupQuestionRepository {

	private final JdbcClient jdbcClient;

	private final SqlGenerator sqlGenerator;

	private final RowMapper<QuestionGroupQuestion> rowMapper = (rs, rowNum) -> {
		final QuestionGroupId questionGroupId = QuestionGroupId.valueOf(rs.getBytes("question_group_id"));
		final QuestionId questionId = QuestionId.valueOf(rs.getBytes("question_id"));
		final boolean required = rs.getBoolean("required");
		return new QuestionGroupQuestion(new QuestionGroupQuestionId(questionGroupId, questionId), required);
	};

	public QuestionGroupQuestionRepository(JdbcClient jdbcClient, SqlGenerator sqlGenerator) {
		this.jdbcClient = jdbcClient;
		this.sqlGenerator = sqlGenerator;
	}

	@Transactional(readOnly = true)
	public List<QuestionGroupQuestion> findByQuestionGroupId(QuestionGroupId questionGroupId) {
		return this.findByQuestionGroupIds(Set.of(questionGroupId));
	}

	@Transactional(readOnly = true)
	public List<QuestionGroupQuestion> findByQuestionGroupIds(Set<QuestionGroupId> questionGroupIds) {
		if (questionGroupIds.isEmpty()) {
			return List.of();
		}
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("questionGroupIds", questionGroupIds);
		final Iterator<QuestionGroupId> itr = questionGroupIds.iterator();
		int i = 0;
		while (itr.hasNext()) {
			params.addValue("questionGroupIds[%d]".formatted(i++), itr.next().toBytesSqlParameterValue());
		}
		final String sql = this.sqlGenerator.generate(
				FileLoader.loadSqlAsString("sql/questiongroupquestion/findByQuestionGroupIds.sql"), params.getValues(),
				params::addValue);
		return this.jdbcClient.sql(sql).paramSource(params).query(this.rowMapper).list();
	}

	public int insert(QuestionGroupQuestion questionGroupQuestion) {
		final QuestionGroupQuestionId questionGroupQuestionId = questionGroupQuestion.questionGroupQuestionId();
		final MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("questionGroupId", questionGroupQuestionId.questionGroupId().toBytesSqlParameterValue())
			.addValue("questionId", questionGroupQuestionId.questionId().toBytesSqlParameterValue())
			.addValue("required", questionGroupQuestion.required());
		final String sql = this.sqlGenerator.generate(
				FileLoader.loadSqlAsString("sql/questiongroupquestion/insert.sql"), params.getValues(),
				params::addValue);
		return this.jdbcClient.sql(sql).paramSource(params).update();
	}

	public int deleteById(QuestionGroupQuestionId questionGroupQuestionId) {
		final MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("questionGroupId", questionGroupQuestionId.questionGroupId().toBytesSqlParameterValue())
			.addValue("questionId", questionGroupQuestionId.questionId().toBytesSqlParameterValue());
		final String sql = this.sqlGenerator.generate(
				FileLoader.loadSqlAsString("sql/questiongroupquestion/deleteById.sql"), params.getValues(),
				params::addValue);
		return this.jdbcClient.sql(sql).paramSource(params).update();
	}

	public int deleteByQuestionGroupId(QuestionGroupId questionGroupId) {
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("questionGroupId",
				questionGroupId.toBytesSqlParameterValue());
		final String sql = this.sqlGenerator.generate(
				FileLoader.loadSqlAsString("sql/questiongroupquestion/deleteByQuestionGroupId.sql"), params.getValues(),
				params::addValue);
		return this.jdbcClient.sql(sql).paramSource(params).update();
	}

}
