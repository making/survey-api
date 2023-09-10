package am.ik.surveys.question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import am.ik.surveys.util.FileLoader;
import org.mybatis.scripting.thymeleaf.SqlGenerator;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Repository
@Transactional
public class QuestionRepository {

	private final JdbcClient jdbcClient;

	private final NamedParameterJdbcTemplate jdbcTemplate;

	private final SqlGenerator sqlGenerator;

	final RowMapper<QuestionChoice> questionChoiceRowMapper = (rs, rowNum) -> {
		final byte[] s = rs.getBytes("question_choice_id");
		if (s == null) {
			return null;
		}
		final QuestionChoiceId questionChoiceId = QuestionChoiceId.valueOf(s);
		final String questionChoiceText = rs.getString("question_choice_text");
		final int score = rs.getInt("score");
		final boolean allowFreeText = rs.getBoolean("allow_free_text");
		return new QuestionChoice(questionChoiceId, questionChoiceText, score, allowFreeText);
	};

	private final ResultSetExtractor<List<Question>> resultSetExtractor = rs -> {
		final List<Question> questions = new ArrayList<>();
		QuestionId previousQuestionId = null;
		SelectiveQuestion selectiveQuestion = null;
		while (rs.next()) {
			final QuestionId questionId = QuestionId.valueOf(rs.getBytes("question_id"));
			final String questionText = rs.getString("question_text");
			final int maxChoices = rs.getInt("max_choices");
			if (maxChoices > 0) {
				final QuestionChoice questionChoice = questionChoiceRowMapper.mapRow(rs, 0);
				if (selectiveQuestion == null || !questionId.equals(previousQuestionId)) {
					selectiveQuestion = new SelectiveQuestion(questionId, questionText, new ArrayList<>(), maxChoices);
					questions.add(selectiveQuestion);
				}
				if (questionChoice != null) {
					selectiveQuestion.questionChoices().add(questionChoice);
				}
			}
			else {
				questions.add(new DescriptiveQuestion(questionId, questionText));
			}
			previousQuestionId = questionId;
		}
		return questions;
	};

	public QuestionRepository(JdbcClient jdbcClient, NamedParameterJdbcTemplate jdbcTemplate,
			SqlGenerator sqlGenerator) {
		this.jdbcClient = jdbcClient;
		this.jdbcTemplate = jdbcTemplate;
		this.sqlGenerator = sqlGenerator;
	}

	@Transactional(readOnly = true)
	public List<Question> findAll() {
		final MapSqlParameterSource params = new MapSqlParameterSource();
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/question/findAll.sql"),
				params.getValues(), params::addValue);
		return this.jdbcClient.sql(sql).paramSource(params).query(this.resultSetExtractor);
	}

	@Transactional(readOnly = true)
	public List<Question> findByIds(Set<QuestionId> questionIds) {
		if (questionIds.isEmpty()) {
			return List.of();
		}
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("questionIds", questionIds);
		final Iterator<QuestionId> itr = questionIds.iterator();
		int i = 0;
		while (itr.hasNext()) {
			params.addValue("questionIds[%d]".formatted(i++), itr.next().toBytesSqlParameterValue());
		}
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/question/findByIds.sql"),
				params.getValues(), params::addValue);
		// return
		// this.jdbcClient.sql(sql).paramSource(params).query(this.resultSetExtractor);
		return this.jdbcTemplate.query(sql, params, resultSetExtractor);
	}

	@Transactional(readOnly = true)
	public Optional<Question> findById(QuestionId questionId) {
		return DataAccessUtils.optionalResult(this.findByIds(Set.of(questionId)));
	}

	public int insert(Question question) {
		final MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("questionId", question.questionId().toBytesSqlParameterValue())
			.addValue("questionText", question.questionText());
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/question/insert.sql"),
				params.getValues(), params::addValue);
		final int update = this.jdbcClient.sql(sql).paramSource(params).update();
		if (question instanceof final DescriptiveQuestion descriptiveQuestion) {
			this.insertDescriptiveQuestion(descriptiveQuestion);
		}
		else if (question instanceof final SelectiveQuestion selectiveQuestion) {
			this.insertSelectiveQuestion(selectiveQuestion);
		}
		return update;
	}

	int insertDescriptiveQuestion(DescriptiveQuestion question) {
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("questionId",
				question.questionId().toBytesSqlParameterValue());
		final String sql = this.sqlGenerator.generate(
				FileLoader.loadSqlAsString("sql/question/insertDescriptiveQuestion.sql"), params.getValues(),
				params::addValue);
		return this.jdbcClient.sql(sql).paramSource(params).update();
	}

	int insertSelectiveQuestion(SelectiveQuestion question) {
		final MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("questionId", question.questionId().toBytesSqlParameterValue())
			.addValue("maxChoices", question.maxChoices());
		final String sql = this.sqlGenerator.generate(
				FileLoader.loadSqlAsString("sql/question/insertSelectiveQuestion.sql"), params.getValues(),
				params::addValue);
		final int update = this.jdbcClient.sql(sql).paramSource(params).update();
		this.insertQuestionChoices(question.questionId(), question.questionChoices());
		return update;
	}

	public int deleteById(QuestionId questionId) {
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("questionId",
				questionId.toBytesSqlParameterValue());
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/question/deleteById.sql"),
				params.getValues(), params::addValue);
		return this.jdbcClient.sql(sql).paramSource(params).update();
	}

	@Transactional(readOnly = true)
	public List<QuestionChoice> findAllQuestionChoicesByQuestionId(QuestionId questionId) {
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("questionId",
				questionId.toBytesSqlParameterValue());
		final String sql = this.sqlGenerator.generate(
				FileLoader.loadSqlAsString("sql/questionchoice/findAllByQuestionId.sql"), params.getValues(),
				params::addValue);
		return this.jdbcClient.sql(sql).paramSource(params).query(this.questionChoiceRowMapper).list();
	}

	@Transactional(readOnly = true)
	public Optional<QuestionChoice> findQuestionChoiceByQuestionIdAndId(QuestionId questionId,
			QuestionChoiceId questionChoiceId) {
		final MapSqlParameterSource params = new MapSqlParameterSource()
			.addValue("questionId", questionId.toBytesSqlParameterValue())
			.addValue("questionChoiceId", questionChoiceId.toBytesSqlParameterValue());
		final String sql = this.sqlGenerator.generate(
				FileLoader.loadSqlAsString("sql/questionchoice/findByQuestionIdAndId.sql"), params.getValues(),
				params::addValue);
		return this.jdbcClient.sql(sql).paramSource(params).query(this.questionChoiceRowMapper).optional();
	}

	public int updateQuestionChoices(SelectiveQuestion question) {
		this.deleteQuestionChoicesByQuestionId(question.questionId());
		return this.insertQuestionChoices(question.questionId(), question.questionChoices());
	}

	int insertQuestionChoices(QuestionId questionId, List<QuestionChoice> questionChoices) {
		if (CollectionUtils.isEmpty(questionChoices)) {
			return 0;
		}
		final MapSqlParameterSource[] params = questionChoices.stream()
			.map(questionChoice -> new MapSqlParameterSource()
				.addValue("questionChoiceId", questionChoice.questionChoiceId().toBytesSqlParameterValue())
				.addValue("questionId", questionId.toBytesSqlParameterValue())
				.addValue("questionChoiceText", questionChoice.questionChoiceText())
				.addValue("score", questionChoice.score())
				.addValue("allowFreeText", questionChoice.allowFreeText()))
			.toArray(MapSqlParameterSource[]::new);
		final String sql = this.sqlGenerator.generate(FileLoader.loadSqlAsString("sql/questionchoice/insert.sql"),
				params[0]);
		return Arrays.stream(this.jdbcTemplate.batchUpdate(sql, params)).sum();
	}

	int deleteQuestionChoicesByQuestionId(QuestionId questionId) {
		final MapSqlParameterSource params = new MapSqlParameterSource().addValue("questionId",
				questionId.toBytesSqlParameterValue());
		final String sql = this.sqlGenerator.generate(
				FileLoader.loadSqlAsString("sql/questionchoice/deleteByQuestionId.sql"), params.getValues(),
				params::addValue);
		return this.jdbcClient.sql(sql).paramSource(params).update();
	}

}
