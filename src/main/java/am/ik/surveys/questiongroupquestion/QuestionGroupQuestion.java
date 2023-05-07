package am.ik.surveys.questiongroupquestion;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * 設問グループ設問
 *
 * @param questionGroupQuestionId 設問グループ設問ID
 * @param required 回答必須
 */
public record QuestionGroupQuestion(@JsonUnwrapped QuestionGroupQuestionId questionGroupQuestionId, boolean required) {
}
