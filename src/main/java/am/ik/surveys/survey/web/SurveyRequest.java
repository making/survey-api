package am.ik.surveys.survey.web;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import am.ik.surveys.organization.OrganizationId;
import am.ik.surveys.survey.Survey;
import am.ik.surveys.survey.SurveyId;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SurveyRequest {

	private String surveyTitle = "";

	private OffsetDateTime startDateTime = Instant.ofEpochSecond(0).atOffset(ZoneOffset.UTC);

	private OffsetDateTime endDateTime = LocalDate.of(3000, 1, 1).atStartOfDay().atOffset(ZoneOffset.UTC);

	private boolean isPublic = false;

	public String getSurveyTitle() {
		return surveyTitle;
	}

	public void setSurveyTitle(String surveyTitle) {
		this.surveyTitle = surveyTitle;
	}

	public OffsetDateTime getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(OffsetDateTime startDateTime) {
		this.startDateTime = startDateTime;
	}

	public OffsetDateTime getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(OffsetDateTime endDateTime) {
		this.endDateTime = endDateTime;
	}

	public boolean isPublic() {
		return isPublic;
	}

	@JsonProperty("is_public")
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public Survey toSurvey(SurveyId surveyId, OrganizationId organizationId) {
		return new Survey(surveyId, this.surveyTitle, this.startDateTime, this.endDateTime, organizationId,
				this.isPublic);
	}

}
