package am.ik.surveys.question;

import java.time.Instant;

import am.ik.surveys.json.IncludeCreatedAt;

public sealed interface Question extends IncludeCreatedAt permits DescriptiveQuestion, SelectiveQuestion {

	QuestionId questionId();

	String questionText();

	@Override
	default Instant createdAt() {
		return this.questionId().asInstant();
	}

}
