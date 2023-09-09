package am.ik.surveys.question;

import java.util.List;
import java.util.Optional;

import am.ik.surveys.Fixtures;

public class MockQuestionRepository extends QuestionRepository {

	public static volatile Question inserted;

	public static void clear() {
		inserted = null;
	}

	public MockQuestionRepository() {
		super(null, jdbcTemplate, null);
	}

	@Override
	public List<Question> findAll() {
		return Fixtures.questions;
	}

	@Override
	public Optional<Question> findById(QuestionId questionId) {
		return Optional.ofNullable(Fixtures.questionMap.get(questionId));
	}

	@Override
	public int insert(Question question) {
		inserted = question;
		return 1;
	}

}
