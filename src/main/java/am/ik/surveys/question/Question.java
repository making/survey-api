package am.ik.surveys.question;

import java.time.Instant;
import java.util.Objects;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonProperty;

public sealed interface Question permits DefaultQuestion, SelectiveQuestion {

	QuestionId questionId();

	String questionText();

	@JsonProperty
	default Instant createdAt() {
		return this.questionId().asInstant();
	}

	default Predicate<Question> isEqual() {
		return question -> Objects.equals(question.questionId(), this.questionId());
	}

}
