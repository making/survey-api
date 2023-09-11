package am.ik.surveys.question;

import java.util.List;
import java.util.Optional;

import am.ik.surveys.Fixtures;
import am.ik.surveys.organization.OrganizationId;

public class MockQuestionRepository extends QuestionRepository {

	public static volatile Question inserted;

	public static void clear() {
		inserted = null;
	}

	public MockQuestionRepository() {
		super(null, null, null);
	}

	@Override
	public List<Question> findByOrganizationId(OrganizationId organizationId) {
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
