package am.ik.surveys.questiongroup;

import am.ik.surveys.question.QuestionId;
import am.ik.surveys.questiongroup.QuestionGroupId;

/**
 * 設問グループ設問ID
 *
 * @param questionGroupId 設問グループID
 * @param questionId 設問ID
 */
public record QuestionGroupQuestionId(QuestionGroupId questionGroupId, QuestionId questionId) {
}
