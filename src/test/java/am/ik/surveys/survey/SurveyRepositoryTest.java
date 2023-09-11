package am.ik.surveys.survey;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import am.ik.surveys.TestConfig;
import am.ik.surveys.organization.Organization;
import am.ik.surveys.organization.OrganizationId;
import io.hypersistence.tsid.TSID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
@Import({ TestConfig.class, SurveyRepository.class })
class SurveyRepositoryTest {

	@Autowired
	SurveyRepository surveyRepository;

	@Autowired
	JdbcClient jdbcClient;

	Organization organization = new Organization(new OrganizationId(TSID.fast()), "test", Set.of());

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

	@BeforeEach
	void setup() {
		this.jdbcClient.sql("DELETE from organization WHERE organization_name = ?")
			.param(organization.organizationName())
			.update();
		this.jdbcClient.sql("INSERT INTO organization(organization_id, organization_name) VALUES (?, ?)")
			.param(organization.organizationId().asBytes())
			.param(organization.organizationName())
			.update();
	}

	@Test
	void insertAndFindAndDelete() {
		LocalDate now = LocalDate.now();
		OffsetDateTime startDateTime = now.with(firstDayOfMonth()).atStartOfDay().atOffset(ZoneOffset.UTC);
		OffsetDateTime endDateTime = now.with(lastDayOfMonth()).atStartOfDay().atOffset(ZoneOffset.UTC);
		SurveyId surveyId = new SurveyId(TSID.fast());
		Survey survey = new Survey(surveyId, "Test", startDateTime, endDateTime, this.organization.organizationId(),
				true);
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