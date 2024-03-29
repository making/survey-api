package am.ik.surveys.survey;

import java.time.Instant;
import java.time.OffsetDateTime;

import am.ik.surveys.json.IncludeCreatedAt;
import am.ik.surveys.organization.OrganizationId;

/**
 * アンケート
 *
 * @param surveyId アンケートID
 * @param surveyTitle アンケートタイトル
 * @param startDateTime 開始予定日時
 * @param endDateTime 終了予定日時
 */
public record Survey(SurveyId surveyId, String surveyTitle, OffsetDateTime startDateTime, OffsetDateTime endDateTime,
		OrganizationId organizationId, boolean isPublic) implements IncludeCreatedAt {

	@Override
	public Instant createdAt() {
		return this.surveyId.asInstant();
	}
}
