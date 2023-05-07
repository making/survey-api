package am.ik.surveys.question;

/**
 * @param questionId 設問ID
 * @param questionText 設問文
 */
public record DescriptiveQuestion(QuestionId questionId, String questionText) implements Question {
}
