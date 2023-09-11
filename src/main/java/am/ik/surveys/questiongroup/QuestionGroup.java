package am.ik.surveys.questiongroup;

import java.time.Instant;

import am.ik.surveys.json.IncludeCreatedAt;
import am.ik.surveys.organization.OrganizationId;

public record QuestionGroup(QuestionGroupId questionGroupId, OrganizationId organizationId, String questionGroupTitle,
		String questionGroupType) implements IncludeCreatedAt {

	@Override
	public Instant createdAt() {
		return this.questionGroupId.asInstant();
	}
}
