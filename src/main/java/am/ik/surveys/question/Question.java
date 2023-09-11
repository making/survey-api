package am.ik.surveys.question;

import java.time.Instant;

import am.ik.surveys.json.IncludeCreatedAt;
import am.ik.surveys.organization.OrganizationId;

public sealed interface Question extends IncludeCreatedAt permits DescriptiveQuestion, SelectiveQuestion {

	QuestionId questionId();

	OrganizationId organizationId();

	String questionText();

	@Override
	default Instant createdAt() {
		return this.questionId().asInstant();
	}

}
