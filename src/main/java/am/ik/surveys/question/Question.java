package am.ik.surveys.question;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

public sealed interface Question permits DescriptiveQuestion, SelectiveQuestion {

	QuestionId questionId();

	String questionText();

	@JsonProperty
	default Instant createdAt() {
		return this.questionId().asInstant();
	}

}
