package am.ik.surveys.survey;

import java.util.List;

import am.ik.surveys.questiongroup.QuestionGroupId;
import am.ik.surveys.util.FileLoader;
import org.mybatis.scripting.thymeleaf.SqlGenerator;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class SurveyQuestionGroupRepository {

	private final JdbcClient jdbcClient;

	private final SqlGenerator sqlGenerator;

	private final RowMapper<SurveyQuestionGroup> rowMapper = (rs, rowNum) -> {
		final SurveyId surveyId = SurveyId.valueOf(rs.getLong("survey_id"));
		final QuestionGroupId questionGroupId = QuestionGroupId.valueOf(rs.getLong("question_group_id"));
		return new SurveyQuestionGroup(surveyId, questionGroupId);
	};

	public SurveyQuestionGroupRepository(JdbcClient jdbcClient, SqlGenerator sqlGenerator) {
		this.jdbcClient = jdbcClient;
		this.sqlGenerator = sqlGenerator;
	}

	@Transactional(readOnly = true)
	public List<SurveyQuestionGroup> findBySurveyId(SurveyId surveyId) {
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("surveyId", surveyId.asLong());
		final String sql = this.sqlGenerator.generate(
				FileLoader.loadSqlAsString("sql/surveyquestiongroup/findBySurveyId.sql"), params.getValues(),
				params::addValue);
		return this.jdbcClient.sql(sql).paramSource(params).query(this.rowMapper).list();
	}

	public int insert(SurveyQuestionGroup surveyQuestionGroup) {
		final MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("surveyId", surveyQuestionGroup.surveyId().asLong())
			.addValue("questionGroupId", surveyQuestionGroup.questionGroupId().asLong());
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/surveyquestiongroup/insert.sql"),
				params.getValues(), params::addValue);
		return this.jdbcClient.sql(sql).paramSource(params).update();
	}

	public int delete(SurveyQuestionGroup surveyQuestionGroup) {
		final MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("surveyId", surveyQuestionGroup.surveyId().asLong())
			.addValue("questionGroupId", surveyQuestionGroup.questionGroupId().asLong());
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/surveyquestiongroup/delete.sql"),
				params.getValues(), params::addValue);
		return this.jdbcClient.sql(sql).paramSource(params).update();
	}

	public int deleteBySurveyId(SurveyId surveyId) {
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("surveyId", surveyId.asLong());
		final String sql = this.sqlGenerator.generate(
				FileLoader.loadSqlAsString("sql/surveyquestiongroup/deleteBySurveyId.sql"), params.getValues(),
				params::addValue);
		return this.jdbcClient.sql(sql).paramSource(params).update();
	}

}
