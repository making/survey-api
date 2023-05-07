package am.ik.surveys.questiongroup.web;

import am.ik.surveys.questiongroup.QuestionGroup;
import am.ik.surveys.questiongroup.QuestionGroupId;

class QuestionGroupRequest {

	private String questionGroupTitle;

	public String getQuestionGroupTitle() {
		return questionGroupTitle;
	}

	public void setQuestionGroupTitle(String questionGroupTitle) {
		this.questionGroupTitle = questionGroupTitle;
	}

	public QuestionGroup toQuestionGroup(QuestionGroupId questionGroupId) {
		return new QuestionGroup(questionGroupId, this.questionGroupTitle);
	}

}
