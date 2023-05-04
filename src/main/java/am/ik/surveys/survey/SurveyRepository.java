package am.ik.surveys.survey;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
public class SurveyRepository {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	private final SqlGenerator sqlGenerator;

	private final RowMapper<Survey> rowMapper = (rs, rowNum) -> {
		final SurveyId surveyId = SurveyId.valueOf(rs.getString("survey_id"));
		final String surveyTitle = rs.getString("survey_title");
		final OffsetDateTime startDateTime = rs.getTimestamp("start_date_time")
			.toLocalDateTime()
			.atOffset(ZoneOffset.ofHours(9));
		final OffsetDateTime endDateTime = rs.getTimestamp("end_date_time")
			.toLocalDateTime()
			.atOffset(ZoneOffset.ofHours(9));
		return new Survey(surveyId, surveyTitle, startDateTime, endDateTime);
	};

	public SurveyRepository(NamedParameterJdbcTemplate jdbcTemplate, SqlGenerator sqlGenerator) {
		this.jdbcTemplate = jdbcTemplate;
		this.sqlGenerator = sqlGenerator;
	}

	@Transactional(readOnly = true)
	public List<Survey> findAll() {
		final MapSqlParameterSource params = new MapSqlParameterSource();
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/survey/findAll.sql"),
				params.getValues(), params::addValue);
		return this.jdbcTemplate.query(sql, params, rowMapper);
	}

	@Transactional(readOnly = true)
	public Optional<Survey> findById(SurveyId surveyId) {
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("surveyId", surveyId.asString());
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/survey/findById.sql"),
				params.getValues(), params::addValue);
		return Optional.ofNullable(DataAccessUtils.singleResult(this.jdbcTemplate.query(sql, params, rowMapper)));
	}

	public int insert(Survey survey) {
		final MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("surveyId", survey.surveyId().asString())
			.addValue("surveyTitle", survey.surveyTitle())
			.addValue("startDateTime", Timestamp.from(survey.startDateTime().toInstant()))
			.addValue("endDateTime", Timestamp.from(survey.endDateTime().toInstant()));
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/survey/insert.sql"),
				params.getValues(), params::addValue);
		return this.jdbcTemplate.update(sql, params);
	}

	public int deleteById(SurveyId surveyId) {
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("surveyId", surveyId.asString());
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/survey/deleteById.sql"),
				params.getValues(), params::addValue);
		return this.jdbcTemplate.update(sql, params);
	}

}
