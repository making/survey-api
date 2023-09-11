package am.ik.surveys.questiongroup.web;

import am.ik.surveys.organization.OrganizationId;
import am.ik.surveys.questiongroup.QuestionGroup;
import am.ik.surveys.questiongroup.QuestionGroupId;

public class QuestionGroupRequest {

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

	public QuestionGroup toQuestionGroup(QuestionGroupId questionGroupId, OrganizationId organizationId) {
		return new QuestionGroup(questionGroupId, organizationId, this.questionGroupTitle, this.questionGroupType);
	}

}
