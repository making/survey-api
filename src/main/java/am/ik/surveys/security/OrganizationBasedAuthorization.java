package am.ik.surveys.security;

import am.ik.surveys.question.QuestionRepository;
import am.ik.surveys.questiongroup.QuestionGroupRepository;
import am.ik.surveys.role.Resource;
import am.ik.surveys.role.Verb;
import am.ik.surveys.survey.SurveyRepository;

import org.springframework.stereotype.Component;

@Component
public class OrganizationBasedAuthorization {

	private final SurveyRepository surveyRepository;

	private final QuestionGroupRepository questionGroupRepository;

	private final QuestionRepository questionRepository;

	public OrganizationBasedAuthorization(SurveyRepository surveyRepository,
			QuestionGroupRepository questionGroupRepository, QuestionRepository questionRepository) {
		this.surveyRepository = surveyRepository;
		this.questionGroupRepository = questionGroupRepository;
		this.questionRepository = questionRepository;
	}

	public OrganizationBasedAuthorizationManager alwaysAuthorized(Resource resource, Verb verb) {
		return new OrganizationBasedAuthorizationManager(this.surveyRepository, this.questionGroupRepository,
				this.questionRepository, resource, verb, false);
	}

	public OrganizationBasedAuthorizationManager permitForPublicSurvey(Resource resource, Verb verb) {
		return new OrganizationBasedAuthorizationManager(this.surveyRepository, this.questionGroupRepository,
				this.questionRepository, resource, verb, true);
	}

}
