package am.ik.surveys.survey;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import am.ik.surveys.organization.OrganizationId;
import am.ik.surveys.util.FileLoader;
import org.mybatis.scripting.thymeleaf.SqlGenerator;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SurveyRepository {

	private final JdbcClient jdbcClient;

	private final SqlGenerator sqlGenerator;

	private final RowMapper<Survey> rowMapper = (rs, rowNum) -> {
		final SurveyId surveyId = SurveyId.valueOf(rs.getLong("survey_id"));
		final String surveyTitle = rs.getString("survey_title");
		final OffsetDateTime startDateTime = rs.getTimestamp("start_date_time").toInstant().atOffset(ZoneOffset.UTC);
		final OffsetDateTime endDateTime = rs.getTimestamp("end_date_time").toInstant().atOffset(ZoneOffset.UTC);
		final OrganizationId organizationId = OrganizationId.valueOf(rs.getLong("organization_id"));
		final boolean isPublic = rs.getBoolean("is_public");
		return new Survey(surveyId, surveyTitle, startDateTime, endDateTime, organizationId, isPublic);
	};

	public SurveyRepository(JdbcClient jdbcClient, SqlGenerator sqlGenerator) {
		this.jdbcClient = jdbcClient;
		this.sqlGenerator = sqlGenerator;
	}

	@Transactional(readOnly = true)
	public List<Survey> findByOrganizationId(OrganizationId organizationId) {
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("organizationId",
				organizationId.asLong());
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/survey/findByOrganizationId.sql"),
				params.getValues());
		return this.jdbcClient.sql(sql).paramSource(params).query(this.rowMapper).list();
	}

	@Transactional(readOnly = true)
	public Optional<Survey> findById(SurveyId surveyId) {
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("surveyId", surveyId.asLong());
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/survey/findById.sql"),
				params.getValues(), params::addValue);
		return this.jdbcClient.sql(sql).paramSource(params).query(this.rowMapper).optional();
	}

	public int insert(Survey survey) {
		final MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("surveyId", survey.surveyId().asLong())
			.addValue("surveyTitle", survey.surveyTitle())
			.addValue("startDateTime", Timestamp.from(survey.startDateTime().toInstant()))
			.addValue("endDateTime", Timestamp.from(survey.endDateTime().toInstant()))
			.addValue("organizationId", survey.organizationId().asLong())
			.addValue("isPublic", survey.isPublic());
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/survey/insert.sql"),
				params.getValues(), params::addValue);
		return this.jdbcClient.sql(sql).paramSource(params).update();
	}

	public int deleteById(SurveyId surveyId) {
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("surveyId", surveyId.asLong());
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/survey/deleteById.sql"),
				params.getValues(), params::addValue);
		return this.jdbcClient.sql(sql).paramSource(params).update();
	}

}
