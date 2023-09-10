package am.ik.surveys.survey;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;

import am.ik.surveys.TestConfig;
import io.hypersistence.tsid.TSID;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
@Import({ TestConfig.class, SurveyRepository.class })
class SurveyRepositoryTest {

	@Autowired
	SurveyRepository surveyRepository;

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

	@Test
	void insertAndFindAndDelete() {
		LocalDate now = LocalDate.now();
		OffsetDateTime startDateTime = now.with(TemporalAdjusters.firstDayOfMonth())
			.atStartOfDay()
			.atOffset(ZoneOffset.UTC);
		OffsetDateTime endDateTime = now.with(TemporalAdjusters.lastDayOfMonth())
			.atStartOfDay()
			.atOffset(ZoneOffset.UTC);
		SurveyId surveyId = new SurveyId(TSID.fast());
		Survey survey = new Survey(surveyId, "Test", startDateTime, endDateTime);
		int inserted = this.surveyRepository.insert(survey);
		assertThat(inserted).isEqualTo(1);
		Survey found = this.surveyRepository.findById(surveyId)
			.orElseThrow(() -> new IllegalStateException("survey not found " + surveyId));
		assertThat(found).isEqualTo(survey);
		int deleted = this.surveyRepository.deleteById(surveyId);
		assertThat(deleted).isEqualTo(1);
		assertThat(this.surveyRepository.findById(surveyId)).isEmpty();
	}

}