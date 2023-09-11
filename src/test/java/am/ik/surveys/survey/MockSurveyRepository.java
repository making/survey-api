package am.ik.surveys.survey;

import java.util.List;
import java.util.Optional;

import am.ik.surveys.Fixtures;
import am.ik.surveys.organization.OrganizationId;

public class MockSurveyRepository extends SurveyRepository {

	public static volatile Survey inserted;

	public static volatile SurveyId deleted;

	public MockSurveyRepository() {
		super(null, null);
	}

	@Override
	public Optional<Survey> findById(SurveyId surveyId) {
		return Optional.ofNullable(Fixtures.surveyMap.get(surveyId));
	}

	@Override
	public List<Survey> findByOrganizationId(OrganizationId organizationId) {
		return Fixtures.surveys;
	}

	@Override
	public int insert(Survey survey) {
		inserted = survey;
		return 1;
	}

	@Override
	public int deleteById(SurveyId surveyId) {
		deleted = surveyId;
		return 1;
	}

	public static void clear() {
		inserted = null;
		deleted = null;
	}

}
