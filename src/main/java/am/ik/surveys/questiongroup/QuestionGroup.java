package am.ik.surveys.questiongroup;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

public record QuestionGroup(QuestionGroupId questionGroupId, String questionGroupTitle) {
	@JsonProperty
	Instant createdAt() {
		return this.questionGroupId.asInstant();
	}
}
