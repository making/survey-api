package am.ik.surveys.question;

import am.ik.surveys.organization.OrganizationId;

/**
 * @param questionId 設問ID
 * @param organizationId 組織ID
 * @param questionText 設問文
 */
public record DescriptiveQuestion(QuestionId questionId, OrganizationId organizationId,
		String questionText) implements Question {
}
