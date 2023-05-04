package am.ik.surveys.survey;

import java.time.Instant;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * アンケート
 *
 * @param surveyId アンケートID
 * @param surveyTitle アンケートタイトル
 * @param startDateTime 開始予定日時
 * @param endDateTime 終了予定日時
 */
public record Survey(SurveyId surveyId, String surveyTitle, OffsetDateTime startDateTime, OffsetDateTime endDateTime) {

	@JsonProperty
	public Instant createdAt() {
		return this.surveyId.asInstant();
	}
}
