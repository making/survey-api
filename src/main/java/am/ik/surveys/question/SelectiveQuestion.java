package am.ik.surveys.question;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @param questionId 設問ID
 * @param questionText 設問文
 * @param questionChoices 設問選択肢リスト
 * @param maxChoices 選択可能数
 */
public record SelectiveQuestion(QuestionId questionId, String questionText, List<QuestionChoice> questionChoices,
		int maxChoices) implements Question {
	public SelectiveQuestion withQuestionChoices(List<QuestionChoice> questionChoices) {
		return new SelectiveQuestion(this.questionId, this.questionText, questionChoices, this.maxChoices);
	}

	public SelectiveQuestion addQuestionChoice(QuestionChoice questionChoice) {
		final List<QuestionChoice> updated = new ArrayList<>(this.questionChoices);
		updated.add(questionChoice);
		return this.withQuestionChoices(updated);
	}

	public SelectiveQuestion removeQuestionChoices(QuestionChoiceId questionChoiceId) {
		final List<QuestionChoice> updated = new ArrayList<>(this.questionChoices);
		updated.removeIf(questionChoice -> Objects.equals(questionChoice.questionChoiceId(), questionChoiceId));
		return this.withQuestionChoices(updated);
	}
}
