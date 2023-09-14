package am.ik.surveys.config;

import am.ik.accesslogger.AccessLogger;
import am.ik.surveys.security.OrganizationBasedAuthorization;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import org.springframework.boot.actuate.autoconfigure.web.exchanges.HttpExchangesProperties;
import org.springframework.boot.actuate.web.exchanges.servlet.HttpExchangesFilter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

import static am.ik.surveys.role.Resource.ANSWER;
import static am.ik.surveys.role.Resource.ORGANIZATION_USER;
import static am.ik.surveys.role.Resource.QUESTION;
import static am.ik.surveys.role.Resource.QUESTION_CHOICE;
import static am.ik.surveys.role.Resource.QUESTION_GROUP;
import static am.ik.surveys.role.Resource.QUESTION_GROUP_QUESTION;
import static am.ik.surveys.role.Resource.SURVEY;
import static am.ik.surveys.role.Resource.SURVEY_QUESTION_GROUP;
import static am.ik.surveys.role.Verb.CREATE;
import static am.ik.surveys.role.Verb.DELETE;
import static am.ik.surveys.role.Verb.GET;
import static am.ik.surveys.role.Verb.LIST;
import static am.ik.surveys.role.Verb.UPDATE;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({ HttpExchangesProperties.class, JwtProperties.class })
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, HttpExchangesProperties properties,
			OrganizationBasedAuthorization authorization) throws Exception {
		return http.authorizeHttpRequests(it -> {
			it // @formatter:off
				.requestMatchers("/actuator/**", "/error").permitAll()
				.requestMatchers(HttpMethod.POST, "/users", "/organizations", "/token").permitAll()
				.requestMatchers(HttpMethod.GET, "/organizations/{organizationId}").access(authorization.alwaysAuthorized(SURVEY, GET))
				.requestMatchers(HttpMethod.POST, "/organizations/{organizationId}/surveys").access(authorization.alwaysAuthorized(SURVEY, CREATE))
				.requestMatchers(HttpMethod.GET, "/organizations/{organizationId}/surveys").access(authorization.alwaysAuthorized(SURVEY, LIST))
				.requestMatchers(HttpMethod.POST, "/organizations/{organizationId}/question_groups").access(authorization.alwaysAuthorized(QUESTION_GROUP, CREATE))
				.requestMatchers(HttpMethod.GET, "/organizations/{organizationId}/question_groups").access(authorization.alwaysAuthorized(QUESTION_GROUP, LIST))
				.requestMatchers(HttpMethod.POST, "/organizations/{organizationId}/questions").access(authorization.alwaysAuthorized(QUESTION, CREATE))
				.requestMatchers(HttpMethod.GET, "/organizations/{organizationId}/questions").access(authorization.alwaysAuthorized(QUESTION, LIST))
				.requestMatchers(HttpMethod.PUT, "/organizations/{organizationId}/organization_users").access(authorization.alwaysAuthorized(ORGANIZATION_USER, UPDATE))
				.requestMatchers(HttpMethod.DELETE, "/organizations/{organizationId}/organization_users").access(authorization.alwaysAuthorized(ORGANIZATION_USER, DELETE))
				.requestMatchers(HttpMethod.GET, "/surveys/{surveyId}").access(authorization.permitForPublicSurvey(SURVEY, GET))
				.requestMatchers(HttpMethod.DELETE, "/surveys/{surveyId}").access(authorization.alwaysAuthorized(SURVEY, DELETE))
				.requestMatchers(HttpMethod.POST, "/surveys/{surveyId}/answers").access(authorization.permitForPublicSurvey(ANSWER, CREATE))
				.requestMatchers(HttpMethod.GET, "/surveys/{surveyId}/answers").access(authorization.alwaysAuthorized(ANSWER, LIST))
				.requestMatchers(HttpMethod.GET, "/surveys/{surveyId}/survey_question_groups").access(authorization.alwaysAuthorized(SURVEY_QUESTION_GROUP, LIST))
				.requestMatchers(HttpMethod.DELETE, "/surveys/{surveyId}/survey_question_groups").access(authorization.alwaysAuthorized(SURVEY_QUESTION_GROUP, DELETE))
				.requestMatchers(HttpMethod.PUT, "/surveys/{surveyId}/survey_question_groups/{questionGroupId}").access(authorization.alwaysAuthorized(SURVEY_QUESTION_GROUP, UPDATE))
				.requestMatchers(HttpMethod.DELETE, "/surveys/{surveyId}/survey_question_groups/{questionGroupId}").access(authorization.alwaysAuthorized(SURVEY_QUESTION_GROUP, DELETE))
				.requestMatchers(HttpMethod.GET, "/question_groups/{questionGroupId}").access(authorization.alwaysAuthorized(QUESTION_GROUP, GET))
				.requestMatchers(HttpMethod.DELETE, "/question_groups/{questionGroupId}").access(authorization.alwaysAuthorized(QUESTION_GROUP, DELETE))
				.requestMatchers(HttpMethod.GET, "/question_groups/{questionGroupId}/question_group_questions").access(authorization.alwaysAuthorized(QUESTION_GROUP_QUESTION, LIST))
				.requestMatchers(HttpMethod.DELETE, "/question_groups/{questionGroupId}/question_group_questions").access(authorization.alwaysAuthorized(QUESTION_GROUP_QUESTION, DELETE))
				.requestMatchers(HttpMethod.PUT, "/question_groups/{questionGroupId}/question_group_questions/{questionId}").access(authorization.alwaysAuthorized(QUESTION_GROUP_QUESTION, UPDATE))
				.requestMatchers(HttpMethod.DELETE, "/question_groups/{questionGroupId}/question_group_questions/{questionId}").access(authorization.alwaysAuthorized(QUESTION_GROUP_QUESTION, DELETE))
				.requestMatchers(HttpMethod.GET, "/questions/{questionId}").access(authorization.alwaysAuthorized(QUESTION, GET))
				.requestMatchers(HttpMethod.DELETE, "/questions/{questionId}").access(authorization.alwaysAuthorized(QUESTION, DELETE))
				.requestMatchers(HttpMethod.GET, "/questions/{questionId}/question_choices").access(authorization.alwaysAuthorized(QUESTION_CHOICE, LIST))
				.requestMatchers(HttpMethod.POST, "/questions/{questionId}/question_choices").access(authorization.alwaysAuthorized(QUESTION_CHOICE, CREATE))
				.requestMatchers(HttpMethod.GET, "/questions/{questionId}/question_choices/{questionChoiceId}").access(authorization.alwaysAuthorized(QUESTION_CHOICE, GET))
				.requestMatchers(HttpMethod.DELETE, "/questions/{questionId}/question_choices/{questionChoiceId}").access(authorization.alwaysAuthorized(QUESTION_CHOICE, DELETE))
				.anyRequest().denyAll();
				// @formatter:on
		}).httpBasic(it -> {
		}).oauth2ResourceServer(it -> it.jwt(jwt -> {
		}))
			.sessionManagement(it -> it.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.csrf(it -> it.disable())
			.addFilterAfter(new HttpExchangesFilter(new AccessLogger(), properties.getRecording().getInclude()),
					SecurityContextHolderAwareRequestFilter.class)
			.build();
	}

	@Bean
	public JwtEncoder jwtEncoder(JwtProperties properties) {
		final JWK jwk = new RSAKey.Builder(properties.publicKey()).privateKey(properties.privateKey()).build();
		final JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
		return new NimbusJwtEncoder(jwks);
	}

}
