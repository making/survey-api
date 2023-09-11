package am.ik.surveys.questiongroup;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import am.ik.surveys.organization.OrganizationId;
import am.ik.surveys.util.FileLoader;
import org.mybatis.scripting.thymeleaf.SqlGenerator;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class QuestionGroupRepository {

	private final JdbcClient jdbcClient;

	private final SqlGenerator sqlGenerator;

	private RowMapper<QuestionGroup> rowMapper = (rs, rowNum) -> {
		final QuestionGroupId questionGroupId = QuestionGroupId.valueOf(rs.getBytes("question_group_id"));
		final OrganizationId organizationId = OrganizationId.valueOf(rs.getBytes("organization_id"));
		final String questionGroupTitle = rs.getString("question_group_title");
		final String questionGroupType = rs.getString("question_group_type");
		return new QuestionGroup(questionGroupId, organizationId, questionGroupTitle, questionGroupType);
	};

	public QuestionGroupRepository(JdbcClient jdbcClient, SqlGenerator sqlGenerator) {
		this.jdbcClient = jdbcClient;
		this.sqlGenerator = sqlGenerator;
	}

	@Transactional(readOnly = true)
	public List<QuestionGroup> findByOrganizationId(OrganizationId organizationId) {
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("organizationId",
				organizationId.toBytesSqlParameterValue());
		final String sql = this.sqlGenerator.generate(
				FileLoader.loadSqlAsString("sql/questiongroup/findByOrganizationId.sql"), params.getValues(),
				params::addValue);
		return this.jdbcClient.sql(sql).paramSource(params).query(this.rowMapper).list();
	}

	@Transactional(readOnly = true)
	public Optional<QuestionGroup> findById(QuestionGroupId questionGroupId) {
		return DataAccessUtils.optionalResult(this.findByIds(Set.of(questionGroupId)));
	}

	@Transactional(readOnly = true)
	public List<QuestionGroup> findByIds(Set<QuestionGroupId> questionGroupIds) {
		if (questionGroupIds.isEmpty()) {
			return List.of();
		}
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("questionGroupIds", questionGroupIds);
		final Iterator<QuestionGroupId> itr = questionGroupIds.iterator();
		int i = 0;
		while (itr.hasNext()) {
			params.addValue("questionGroupIds[%d]".formatted(i++), itr.next().toBytesSqlParameterValue());
		}
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/questiongroup/findByIds.sql"),
				params.getValues(), params::addValue);
		return this.jdbcClient.sql(sql).paramSource(params).query(this.rowMapper).list();
	}

	public int insert(QuestionGroup questionGroup) {
		final MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("questionGroupId", questionGroup.questionGroupId().toBytesSqlParameterValue())
			.addValue("organizationId", questionGroup.organizationId().toBytesSqlParameterValue())
			.addValue("questionGroupTitle", questionGroup.questionGroupTitle())
			.addValue("questionGroupType", questionGroup.questionGroupType());
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/questiongroup/insert.sql"),
				params.getValues(), params::addValue);
		return this.jdbcClient.sql(sql).paramSource(params).update();
	}

	public int deleteById(QuestionGroupId questionGroupId) {
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("questionGroupId",
				questionGroupId.toBytesSqlParameterValue());
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/questiongroup/deleteById.sql"),
				params.getValues(), params::addValue);
		return this.jdbcClient.sql(sql).paramSource(params).update();
	}

}
