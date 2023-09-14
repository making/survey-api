package am.ik.surveys.security;

import java.util.function.Supplier;

import am.ik.surveys.organization.OrganizationId;
import am.ik.surveys.question.Question;
import am.ik.surveys.question.QuestionId;
import am.ik.surveys.question.QuestionRepository;
import am.ik.surveys.questiongroup.QuestionGroup;
import am.ik.surveys.questiongroup.QuestionGroupId;
import am.ik.surveys.questiongroup.QuestionGroupRepository;
import am.ik.surveys.role.Resource;
import am.ik.surveys.role.Verb;
import am.ik.surveys.survey.Survey;
import am.ik.surveys.survey.SurveyId;
import am.ik.surveys.survey.SurveyRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.web.server.ResponseStatusException;

public class OrganizationBasedAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

	private final SurveyRepository surveyRepository;

	private final QuestionGroupRepository questionGroupRepository;

	private final QuestionRepository questionRepository;

	private final Resource resource;

	private final Verb verb;

	private final boolean permitIfPublic;

	public OrganizationBasedAuthorizationManager(SurveyRepository surveyRepository,
			QuestionGroupRepository questionGroupRepository, QuestionRepository questionRepository, Resource resource,
			Verb verb, boolean permitIfPublic) {
		this.surveyRepository = surveyRepository;
		this.questionGroupRepository = questionGroupRepository;
		this.questionRepository = questionRepository;
		this.resource = resource;
		this.verb = verb;
		this.permitIfPublic = permitIfPublic;
	}

	@Override
	public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
		OrganizationId organizationId;
		if (context.getVariables().containsKey("surveyId")) {
			final SurveyId surveyId = SurveyId.valueOf(context.getVariables().get("surveyId"));
			final Survey survey = this.surveyRepository.findById(surveyId)
				.orElseThrow(
						() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The requested survey is not found."));
			if (survey.isPublic() && this.permitIfPublic) {
				return new AuthorizationDecision(true);
			}
			organizationId = survey.organizationId();
		}
		else if (context.getVariables().containsKey("questionGroupId")) {
			final QuestionGroupId questionGroupId = QuestionGroupId
				.valueOf(context.getVariables().get("questionGroupId"));
			final QuestionGroup questionGroup = this.questionGroupRepository.findById(questionGroupId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"The requested question group is not found."));
			organizationId = questionGroup.organizationId();
		}
		else if (context.getVariables().containsKey("questionId")) {
			final QuestionId questionId = QuestionId.valueOf(context.getVariables().get("questionId"));
			final Question question = this.questionRepository.findById(questionId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"The requested question is not found."));
			organizationId = question.organizationId();
		}
		else if (context.getVariables().containsKey("organizationId")) {
			organizationId = OrganizationId.valueOf(context.getVariables().get("organizationId"));
		}
		else {
			throw new IllegalStateException(
					"None of 'surveyId' / `questionGroupId`/ `questionId` / `organizationId` is specified.");
		}
		final Authentication auth = authentication.get();
		final String prefix = auth instanceof JwtAuthenticationToken ? "SCOPE_" : "";
		return new AuthorizationDecision(auth.getAuthorities()
			.contains(new SimpleGrantedAuthority(
					"%s%s|%s|%s".formatted(prefix, organizationId.asString(), Resource.WILDCARD, this.verb)))
				|| auth.getAuthorities()
					.contains(new SimpleGrantedAuthority(
							"%s%s|%s|%s".formatted(prefix, organizationId.asString(), this.resource, this.verb))));
	}

}
