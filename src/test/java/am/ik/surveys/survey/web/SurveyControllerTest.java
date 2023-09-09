package am.ik.surveys.survey.web;

import java.time.OffsetDateTime;

import am.ik.surveys.Fixtures;
import am.ik.surveys.survey.MockSurveyRepository;
import am.ik.surveys.survey.SurveyId;
import am.ik.surveys.survey.SurveyRepository;
import am.ik.surveys.tsid.TsidGenerator;
import io.hypersistence.tsid.TSID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SurveyController.class)
@Disabled
class SurveyControllerTest {

	@Autowired
	MockMvc mockMvc;

	@BeforeEach
	void clear() {
		MockSurveyRepository.clear();
	}

	@Test
	void getSurvey_200() throws Exception {
		this.mockMvc.perform(get("/surveys/{surveyId}", Fixtures.s1.surveyId().asString()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.survey_id").value(Fixtures.s1.surveyId().asString()))
			.andExpect(jsonPath("$.survey_title").value(Fixtures.s1.surveyTitle()))
			.andExpect(jsonPath("$.start_date_time").value("2019-08-01T00:00:00+09:00"))
			.andExpect(jsonPath("$.end_date_time").value("2019-08-31T00:00:00+09:00"));
	}

	@Test
	void getSurvey_404() throws Exception {
		this.mockMvc.perform(get("/surveys/0C6VAWEVB33AC"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.detail").value("The given survey id is not found (0C6VAWEVB33AC)"));
	}

	@Test
	void getSurveys_200() throws Exception {
		this.mockMvc.perform(get("/surveys"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(2))
			.andExpect(jsonPath("$[0].survey_id").value(Fixtures.s1.surveyId().asString()))
			.andExpect(jsonPath("$[0].survey_title").value(Fixtures.s1.surveyTitle()))
			.andExpect(jsonPath("$[0].start_date_time").value("2019-08-01T00:00:00+09:00"))
			.andExpect(jsonPath("$[0].end_date_time").value("2019-08-31T00:00:00+09:00"));
	}

	@Test
	void postSurveys_201() throws Exception {
		this.mockMvc
			.perform(post("/surveys").contentType(MediaType.APPLICATION_JSON)
				.content(
						"""
								{"survey_title":"テストアンケート", "start_date_time":"2019-10-01T00:00:00.000+09:00", "end_date_time":"2020-10-01T00:00:00.000+09:00"}
								"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.survey_id").value("0C6VAWEVB33AD"))
			.andExpect(jsonPath("$.survey_title").value("テストアンケート"))
			.andExpect(jsonPath("$.start_date_time").value("2019-09-30T15:00:00Z"))
			.andExpect(jsonPath("$.end_date_time").value("2020-09-30T15:00:00Z"))
			.andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/surveys/0C6VAWEVB33AD"));
		assertThat(MockSurveyRepository.inserted.surveyId()).isEqualTo(SurveyId.valueOf("0C6VAWEVB33AD"));
		assertThat(MockSurveyRepository.inserted.surveyTitle()).isEqualTo("テストアンケート");
		assertThat(MockSurveyRepository.inserted.startDateTime())
			.isEqualTo(OffsetDateTime.parse("2019-09-30T15:00:00Z"));
		assertThat(MockSurveyRepository.inserted.endDateTime()).isEqualTo(OffsetDateTime.parse("2020-09-30T15:00:00Z"));
	}

	@Test
	void postSurveys_201_default_date() throws Exception {
		this.mockMvc.perform(post("/surveys").contentType(MediaType.APPLICATION_JSON).content("""
				{"survey_title":"テストアンケート"}
				"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.survey_id").value("0C6VAWEVB33AD"))
			.andExpect(jsonPath("$.survey_title").value("テストアンケート"))
			.andExpect(jsonPath("$.start_date_time").value("1970-01-01T00:00:00Z"))
			.andExpect(jsonPath("$.end_date_time").value("3000-01-01T00:00:00Z"))
			.andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/surveys/0C6VAWEVB33AD"));
		assertThat(MockSurveyRepository.inserted.surveyId()).isEqualTo(SurveyId.valueOf("0C6VAWEVB33AD"));
		assertThat(MockSurveyRepository.inserted.surveyTitle()).isEqualTo("テストアンケート");
		assertThat(MockSurveyRepository.inserted.startDateTime())
			.isEqualTo(OffsetDateTime.parse("1970-01-01T00:00:00Z"));
		assertThat(MockSurveyRepository.inserted.endDateTime()).isEqualTo(OffsetDateTime.parse("3000-01-01T00:00:00Z"));
	}

	@Test
	void deleteSurvey_204() throws Exception {
		this.mockMvc.perform(delete("/surveys/0C6VAWEVB33AC")).andExpect(status().isNoContent());
		assertThat(MockSurveyRepository.deleted).isEqualTo(SurveyId.valueOf("0C6VAWEVB33AC"));
	}

	@TestConfiguration
	public static class Config {

		@Bean
		public TsidGenerator tsidGenerator() {
			return () -> TSID.from("0C6VAWEVB33AD");
		}

		@Bean
		public SurveyRepository surveyRepository() {
			return new MockSurveyRepository();
		}

	}

}