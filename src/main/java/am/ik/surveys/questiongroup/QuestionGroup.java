package am.ik.surveys.questiongroup;

import java.time.Instant;

import am.ik.surveys.json.IncludeCreatedAt;

public record QuestionGroup(QuestionGroupId questionGroupId, String questionGroupTitle,
		String questionGroupType) implements IncludeCreatedAt {

	@Override
	public Instant createdAt() {
		return this.questionGroupId.asInstant();
	}
}
