package am.ik.surveys.questiongroup.web;

import am.ik.surveys.questiongroup.QuestionGroup;
import am.ik.surveys.questiongroup.QuestionGroupId;

class QuestionGroupRequest {

	private String questionGroupTitle;

	private String questionGroupType;

	public String getQuestionGroupTitle() {
		return questionGroupTitle;
	}

	public void setQuestionGroupTitle(String questionGroupTitle) {
		this.questionGroupTitle = questionGroupTitle;
	}

	public String getQuestionGroupType() {
		return questionGroupType;
	}

	public void setQuestionGroupType(String questionGroupType) {
		this.questionGroupType = questionGroupType;
	}

	public QuestionGroup toQuestionGroup(QuestionGroupId questionGroupId) {
		return new QuestionGroup(questionGroupId, this.questionGroupTitle, this.questionGroupType);
	}

}
