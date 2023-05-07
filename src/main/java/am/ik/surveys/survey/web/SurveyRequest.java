package am.ik.surveys.survey.web;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import am.ik.surveys.survey.Survey;
import am.ik.surveys.survey.SurveyId;

class SurveyRequest {

	private String surveyTitle = "";

	private OffsetDateTime startDateTime = Instant.ofEpochSecond(0).atOffset(ZoneOffset.UTC);

	private OffsetDateTime endDateTime = LocalDate.of(3000, 1, 1).atStartOfDay().atOffset(ZoneOffset.UTC);

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

	public Survey toSurvey(SurveyId surveyId) {
		return new Survey(surveyId, this.surveyTitle, this.startDateTime, this.endDateTime);
	}

}
