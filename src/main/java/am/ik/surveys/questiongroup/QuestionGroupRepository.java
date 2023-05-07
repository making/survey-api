package am.ik.surveys.questiongroup;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import am.ik.surveys.util.FileLoader;
import org.mybatis.scripting.thymeleaf.SqlGenerator;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class QuestionGroupRepository {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	private final SqlGenerator sqlGenerator;

	private RowMapper<QuestionGroup> rowMapper = new RowMapper<QuestionGroup>() {
		@Override
		public QuestionGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
			final QuestionGroupId questionGroupId = QuestionGroupId.valueOf(rs.getString("question_group_id"));
			final String questionGroupTitle = rs.getString("question_group_title");
			return new QuestionGroup(questionGroupId, questionGroupTitle);
		}
	};

	public QuestionGroupRepository(NamedParameterJdbcTemplate jdbcTemplate, SqlGenerator sqlGenerator) {
		this.jdbcTemplate = jdbcTemplate;
		this.sqlGenerator = sqlGenerator;
	}

	@Transactional(readOnly = true)
	public List<QuestionGroup> findAll() {
		final MapSqlParameterSource params = new MapSqlParameterSource();
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/questiongroup/findAll.sql"),
				params.getValues(), params::addValue);
		return this.jdbcTemplate.query(sql, params, rowMapper);
	}

	@Transactional(readOnly = true)
	public Optional<QuestionGroup> findById(QuestionGroupId questionGroupId) {
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("questionGroupId",
				questionGroupId.asString());
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/questiongroup/findById.sql"),
				params.getValues(), params::addValue);
		return Optional.ofNullable(DataAccessUtils.singleResult(this.jdbcTemplate.query(sql, params, rowMapper)));
	}

	public int insert(QuestionGroup questionGroup) {
		final MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("questionGroupId", questionGroup.questionGroupId().asString())
			.addValue("questionGroupTitle", questionGroup.questionGroupTitle());
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/questiongroup/insert.sql"),
				params.getValues(), params::addValue);
		return this.jdbcTemplate.update(sql, params);
	}

	public int deleteById(QuestionGroupId questionGroupId) {
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("questionGroupId",
				questionGroupId.asString());
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/questiongroup/deleteById.sql"),
				params.getValues(), params::addValue);
		return this.jdbcTemplate.update(sql, params);
	}

}
