package am.ik.surveys.e2e;

import java.net.URI;
import java.util.function.Consumer;

import am.ik.surveys.tsid.TsidGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.tsid.TSID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = { "logging.level.sql=info",
				"logging.level.org.springframework.jdbc.support.JdbcTransactionManager=info" })
@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureJsonTesters
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DemoScenario2IntegrationTests {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

	@LocalServerPort
	int port;

	RestClient restClient;

	@Autowired
	JacksonTester<JsonNode> json;

	@MockBean
	TsidGenerator tsidGenerator;

	@BeforeEach
	void setup() {
		this.restClient = RestClient.create("http://localhost:%d".formatted(this.port));
	}

	@Test
	@Order(1)
	void createUser() throws Exception {
		given(this.tsidGenerator.generate()).willReturn(TSID.from(0));
		final ResponseEntity<JsonNode> response = this.restClient.post()
			.uri("/users")
			.contentType(MediaType.APPLICATION_JSON)
			.body("""
					{
					  "email": "admin@example.com",
					  "password":  "Admin123!"
					}
					""")
			.retrieve()
			.toEntity(JsonNode.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
		assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
				{
				  "user_id": "0000000000000",
				  "email": "admin@example.com"
				}
				""");
	}

	Consumer<HttpHeaders> configureAuth() {
		return headers -> headers.setBasicAuth("admin@example.com", "Admin123!");
	}

	@Test
	@Order(2)
	void createOrganization() throws Exception {
		given(this.tsidGenerator.generate()).willReturn(TSID.from(1));
		final ResponseEntity<JsonNode> response = this.restClient.post()
			.uri("/organizations")
			.contentType(MediaType.APPLICATION_JSON)
			.body("""
					{
					  "organization_name": "JSUG",
					  "admin_email": "admin@example.com"
					}
					""")
			.retrieve()
			.toEntity(JsonNode.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
		assertThat(response.getHeaders().getLocation())
			.isEqualTo(URI.create("http://localhost:%d/organizations/0000000000001".formatted(this.port)));
		assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
				{
				  "organization_id": "0000000000001",
				  "organization_name": "JSUG"
				}
				""");
	}

	@Test
	@Order(3)
	void createSurvey() throws Exception {
		given(this.tsidGenerator.generate()).willReturn(TSID.from(2));
		final ResponseEntity<JsonNode> response = this.restClient.post()
			.uri("/organizations/0000000000001/surveys")
			.contentType(MediaType.APPLICATION_JSON)
			.headers(configureAuth())
			.body("""
					{
					  "survey_title":"Spring Fest 2023",
					  "start_date_time":"2023-10-01T00:00:00.000+09:00",
					  "end_date_time":"2024-10-01T00:00:00.000+09:00",
					  "is_public": true
					}
					""")
			.retrieve()
			.toEntity(JsonNode.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
		assertThat(response.getHeaders().getLocation())
			.isEqualTo(URI.create("http://localhost:%d/surveys/0000000000002".formatted(this.port)));
		assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
				{
				  "survey_id": "0000000000002",
				  "survey_title": "Spring Fest 2023",
				  "start_date_time": "2023-09-30T15:00:00Z",
				  "end_date_time": "2024-09-30T15:00:00Z",
				  "organization_id": "0000000000001",
				  "is_public": true
				}
				""");
	}

	@Test
	@Order(3)
	void createQuestionGroup() throws Exception {
		given(this.tsidGenerator.generate()).willReturn(TSID.from(3), TSID.from(4), TSID.from(5), TSID.from(6));
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/organizations/0000000000001/question_groups")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAuth())
				.body("""
						{
						  "question_group_title": "イベント全体の感想",
						  "question_group_type": "イベント全体の感想"
						}
						""")
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:%d/question_groups/0000000000003".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "question_group_id": "0000000000003",
					  "organization_id": "0000000000001",
					  "question_group_title": "イベント全体の感想",
					  "question_group_type": "イベント全体の感想"
					}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/organizations/0000000000001/question_groups")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAuth())
				.body("""
						{
						  "question_group_title": "セッションAの感想",
						  "question_group_type": "セッション毎の感想"
						}
						""")
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:%d/question_groups/0000000000004".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "question_group_id": "0000000000004",
					  "organization_id": "0000000000001",
					  "question_group_title": "セッションAの感想",
					  "question_group_type": "セッション毎の感想"
					}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/organizations/0000000000001/question_groups")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAuth())
				.body("""
						{
						  "question_group_title": "セッションBの感想",
						  "question_group_type": "セッション毎の感想"
						}
						""")
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:%d/question_groups/0000000000005".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "question_group_id": "0000000000005",
					  "organization_id": "0000000000001",
					  "question_group_title": "セッションBの感想",
					  "question_group_type": "セッション毎の感想"
					}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/organizations/0000000000001/question_groups")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAuth())
				.body("""
						{
						  "question_group_title": "セッションCの感想",
						  "question_group_type": "セッション毎の感想"
						}
						""")
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:%d/question_groups/0000000000006".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "question_group_id": "0000000000006",
					  "organization_id": "0000000000001",
					  "question_group_title": "セッションCの感想",
					  "question_group_type": "セッション毎の感想"
					}
					""");
		}
	}

	@Test
	@Order(3)
	void createQuestions() throws Exception {
		given(this.tsidGenerator.generate()).willReturn(TSID.from(4), TSID.from(5), TSID.from(6), TSID.from(7),
				TSID.from(8));
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/organizations/0000000000001/questions")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAuth())
				.body("""
						{
						  "question_text": "満足度はどうだったでしょうか？",
						  "max_choices": 1
						}
						""")
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:%d/questions/0000000000004".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "question_id": "0000000000004",
					  "organization_id": "0000000000001",
					  "question_text": "満足度はどうだったでしょうか？",
					  "question_choices": [],
					  "max_choices": 1
					}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/organizations/0000000000001/questions")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAuth())
				.body("""
						{
						  "question_text": "次回、期待するコンテンツを教えてください"
						}
						""")
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:%d/questions/0000000000005".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "question_id": "0000000000005",
					  "organization_id": "0000000000001",
					  "question_text": "次回、期待するコンテンツを教えてください"
					}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/organizations/0000000000001/questions")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAuth())
				.body("""
						{
						  "question_text": "全体で何かご意見があれば教えてください"
						}
						""")
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:%d/questions/0000000000006".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "question_id": "0000000000006",
					  "organization_id": "0000000000001",
					  "question_text": "全体で何かご意見があれば教えてください"
					}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/organizations/0000000000001/questions")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAuth())
				.body("""
						{
						  "question_text": "難易度は良かったでしょうか？",
						  "max_choices": 1
						}
						""")
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:%d/questions/0000000000007".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "question_id": "0000000000007",
					  "organization_id": "0000000000001",
					  "question_text": "難易度は良かったでしょうか？",
					  "question_choices": [],
					  "max_choices": 1
					}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/organizations/0000000000001/questions")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAuth())
				.body("""
						{
						  "question_text": "何かご意見があれば教えてください"
						}
						""")
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:%d/questions/0000000000008".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "question_id": "0000000000008",
					  "organization_id": "0000000000001",
					  "question_text": "何かご意見があれば教えてください"
					}
					""");
		}
	}

	@Test
	@Order(4)
	void mapQuestionToQuestionGroup() throws Exception {
		{
			final ResponseEntity<JsonNode> response = this.restClient.put()
				.uri("/question_groups/0000000000003/question_group_questions/0000000000004")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAuth())
				.body("""
						{
						  "required": true
						}
						""")
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "question_group_id": "0000000000003",
					  "question_id": "0000000000004",
					  "required": true
					}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.put()
				.uri("/question_groups/0000000000003/question_group_questions/0000000000005")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAuth())
				.body("""
						{
						  "required": false
						}
						""")
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "question_group_id": "0000000000003",
					  "question_id": "0000000000005",
					  "required": false
					}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.put()
				.uri("/question_groups/0000000000003/question_group_questions/0000000000006")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAuth())
				.body("""
						{
						  "required": false
						}
						""")
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "question_group_id": "0000000000003",
					  "question_id": "0000000000006",
					  "required": false
					}
					""");
		}
		for (int i = 0; i < 3; i++) {
			final String questionGroupId = "%013d".formatted(i + 4);
			{
				final ResponseEntity<JsonNode> response = this.restClient.put()
					.uri("/question_groups/%s/question_group_questions/0000000000004".formatted(questionGroupId))
					.contentType(MediaType.APPLICATION_JSON)
					.headers(configureAuth())
					.body("""
							{
							  "required": true
							}
							""")
					.retrieve()
					.toEntity(JsonNode.class);
				assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
				assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
				assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
						{
						  "question_group_id": "%s",
						  "question_id": "0000000000004",
						  "required": true
						}
						""".formatted(questionGroupId));
			}
			{
				final ResponseEntity<JsonNode> response = this.restClient.put()
					.uri("/question_groups/%s/question_group_questions/0000000000007".formatted(questionGroupId))
					.contentType(MediaType.APPLICATION_JSON)
					.headers(configureAuth())
					.body("""
							{
							  "required": true
							}
							""")
					.retrieve()
					.toEntity(JsonNode.class);
				assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
				assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
				assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
						{
						  "question_group_id": "%s",
						  "question_id": "0000000000007",
						  "required": true
						}
						""".formatted(questionGroupId));
			}
			{
				final ResponseEntity<JsonNode> response = this.restClient.put()
					.uri("/question_groups/%s/question_group_questions/0000000000008".formatted(questionGroupId))
					.contentType(MediaType.APPLICATION_JSON)
					.headers(configureAuth())
					.body("""
							{
							  "required": false
							}
							""")
					.retrieve()
					.toEntity(JsonNode.class);
				assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
				assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
				assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
						{
						  "question_group_id": "%s",
						  "question_id": "0000000000008",
						  "required": false
						}
						""".formatted(questionGroupId));
			}
		}
	}

	@Test
	@Order(4)
	void mapQuestionGroupToSurvey() throws Exception {
		for (int i = 0; i < 4; i++) {
			final String questionGroupId = "%013d".formatted(i + 3);
			final ResponseEntity<JsonNode> response = this.restClient.put()
				.uri("/surveys/0000000000002/survey_question_groups/%s".formatted(questionGroupId))
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAuth())
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "survey_id": "0000000000002",
					  "question_group_id": "%s"
					}
					""".formatted(questionGroupId));
		}
	}

	@Test
	@Order(4)
	void addQuestionChoices() throws Exception {
		given(this.tsidGenerator.generate()).willReturn(TSID.from(7), TSID.from(8), TSID.from(9), TSID.from(10),
				TSID.from(11), TSID.from(12), TSID.from(13), TSID.from(14), TSID.from(15), TSID.from(16));
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/questions/0000000000004/question_choices")
				.body("""
						{
						  "question_choice_text": "とても良かった",
						  "score": 5
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAuth())
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create(
					"http://localhost:%d/questions/0000000000004/question_choices/0000000000007".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
							{
							  "question_choice_id":"0000000000007",
							  "question_choice_text": "とても良かった",
							  "score": 5,
							  "allow_free_text": false
							}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/questions/0000000000004/question_choices")
				.body("""
						{
						  "question_choice_text": "良かった",
						  "score": 4,
						  "allow_free_text": false
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAuth())
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create(
					"http://localhost:%d/questions/0000000000004/question_choices/0000000000008".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
							{
							  "question_choice_id":"0000000000008",
							  "question_choice_text": "良かった",
							  "score": 4,
							  "allow_free_text": false
							}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/questions/0000000000004/question_choices")
				.body("""
						{
						  "question_choice_text": "普通",
						  "score": 3,
						  "allow_free_text": false
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAuth())
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create(
					"http://localhost:%d/questions/0000000000004/question_choices/0000000000009".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
							{
							  "question_choice_id":"0000000000009",
							  "question_choice_text": "普通",
							  "score": 3,
							  "allow_free_text": false
							}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/questions/0000000000004/question_choices")
				.body("""
						{
						  "question_choice_text": "悪かった",
						  "score": 2,
						  "allow_free_text": false
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAuth())
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create(
					"http://localhost:%d/questions/0000000000004/question_choices/000000000000A".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
							{
							  "question_choice_id":"000000000000A",
							  "question_choice_text": "悪かった",
							  "score": 2,
							  "allow_free_text": false
							}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/questions/0000000000004/question_choices")
				.body("""
						{
						  "question_choice_text": "とても悪かった",
						  "score": 1,
						  "allow_free_text": false
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAuth())
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create(
					"http://localhost:%d/questions/0000000000004/question_choices/000000000000B".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
							{
							  "question_choice_id":"000000000000B",
							  "question_choice_text": "とても悪かった",
							  "score": 1,
							  "allow_free_text": false
							}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/questions/0000000000007/question_choices")
				.body("""
						{
						  "question_choice_text": "とても難しかった",
						  "score": 5
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAuth())
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create(
					"http://localhost:%d/questions/0000000000007/question_choices/000000000000C".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
							{
							  "question_choice_id":"000000000000C",
							  "question_choice_text": "とても難しかった",
							  "score": 5,
							  "allow_free_text": false
							}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/questions/0000000000007/question_choices")
				.body("""
						{
						  "question_choice_text": "難しかった",
						  "score": 4,
						  "allow_free_text": false
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAuth())
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create(
					"http://localhost:%d/questions/0000000000007/question_choices/000000000000D".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
							{
							  "question_choice_id":"000000000000D",
							  "question_choice_text": "難しかった",
							  "score": 4,
							  "allow_free_text": false
							}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/questions/0000000000007/question_choices")
				.body("""
						{
						  "question_choice_text": "ちょうど良かった",
						  "score": 3,
						  "allow_free_text": false
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAuth())
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create(
					"http://localhost:%d/questions/0000000000007/question_choices/000000000000E".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
							{
							  "question_choice_id":"000000000000E",
							  "question_choice_text": "ちょうど良かった",
							  "score": 3,
							  "allow_free_text": false
							}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/questions/0000000000007/question_choices")
				.body("""
						{
						  "question_choice_text": "簡単だった",
						  "score": 2,
						  "allow_free_text": false
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAuth())
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create(
					"http://localhost:%d/questions/0000000000007/question_choices/000000000000F".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
							{
							  "question_choice_id":"000000000000F",
							  "question_choice_text": "簡単だった",
							  "score": 2,
							  "allow_free_text": false
							}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/questions/0000000000007/question_choices")
				.body("""
						{
						  "question_choice_text": "簡単すぎた",
						  "score": 1,
						  "allow_free_text": false
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAuth())
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create(
					"http://localhost:%d/questions/0000000000007/question_choices/000000000000G".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
							{
							  "question_choice_id":"000000000000G",
							  "question_choice_text": "簡単すぎた",
							  "score": 1,
							  "allow_free_text": false
							}
					""");
		}
	}

	@Test
	@Order(5)
	void viewSurvey() throws Exception {
		final ResponseEntity<JsonNode> response = this.restClient.get()
			.uri("/surveys/0000000000002?include_questions=true")
			.retrieve()
			.toEntity(JsonNode.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
		assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
				{
				  "survey_id": "0000000000002",
				  "survey_title": "Spring Fest 2023",
				  "start_date_time": "2023-09-30T15:00:00Z",
				  "end_date_time": "2024-09-30T15:00:00Z",
				  "organization_id": "0000000000001",
				  "is_public": true,
				  "question_groups": [
				    {
				      "question_group_id": "0000000000003",
				      "organization_id": "0000000000001",
				      "question_group_title": "イベント全体の感想",
				      "question_group_type": "イベント全体の感想",
				      "questions": [
				        {
				          "question_id": "0000000000004",
				          "organization_id": "0000000000001",
				          "question_text": "満足度はどうだったでしょうか？",
				          "question_choices": [
				            {
				              "question_choice_id": "0000000000007",
				              "question_choice_text": "とても良かった",
				              "score": 5,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "0000000000008",
				              "question_choice_text": "良かった",
				              "score": 4,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "0000000000009",
				              "question_choice_text": "普通",
				              "score": 3,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "000000000000A",
				              "question_choice_text": "悪かった",
				              "score": 2,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "000000000000B",
				              "question_choice_text": "とても悪かった",
				              "score": 1,
				              "allow_free_text": false
				            }
				          ],
				          "max_choices": 1,
				          "required": true
				        },
				        {
				          "question_id": "0000000000005",
				          "organization_id": "0000000000001",
				          "question_text": "次回、期待するコンテンツを教えてください",
				          "required": false
				        },
				        {
				          "question_id": "0000000000006",
				          "organization_id": "0000000000001",
				          "question_text": "全体で何かご意見があれば教えてください",
				          "required": false
				        }
				      ]
				    },
				    {
				      "question_group_id": "0000000000004",
				      "organization_id": "0000000000001",
				      "question_group_title": "セッションAの感想",
				      "question_group_type": "セッション毎の感想",
				      "questions": [
				        {
				          "question_id": "0000000000004",
				          "organization_id": "0000000000001",
				          "question_text": "満足度はどうだったでしょうか？",
				          "question_choices": [
				            {
				              "question_choice_id": "0000000000007",
				              "question_choice_text": "とても良かった",
				              "score": 5,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "0000000000008",
				              "question_choice_text": "良かった",
				              "score": 4,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "0000000000009",
				              "question_choice_text": "普通",
				              "score": 3,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "000000000000A",
				              "question_choice_text": "悪かった",
				              "score": 2,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "000000000000B",
				              "question_choice_text": "とても悪かった",
				              "score": 1,
				              "allow_free_text": false
				            }
				          ],
				          "max_choices": 1,
				          "required": true
				        },
				        {
				          "question_id": "0000000000007",
				          "organization_id": "0000000000001",
				          "question_text": "難易度は良かったでしょうか？",
				          "question_choices": [
				            {
				              "question_choice_id": "000000000000C",
				              "question_choice_text": "とても難しかった",
				              "score": 5,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "000000000000D",
				              "question_choice_text": "難しかった",
				              "score": 4,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "000000000000E",
				              "question_choice_text": "ちょうど良かった",
				              "score": 3,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "000000000000F",
				              "question_choice_text": "簡単だった",
				              "score": 2,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "000000000000G",
				              "question_choice_text": "簡単すぎた",
				              "score": 1,
				              "allow_free_text": false
				            }
				          ],
				          "max_choices": 1,
				          "required": true
				        },
				        {
				          "question_id": "0000000000008",
				          "organization_id": "0000000000001",
				          "question_text": "何かご意見があれば教えてください",
				          "required": false
				        }
				      ]
				    },
				    {
				      "question_group_id": "0000000000005",
				      "organization_id": "0000000000001",
				      "question_group_title": "セッションBの感想",
				      "question_group_type": "セッション毎の感想",
				      "questions": [
				        {
				          "question_id": "0000000000004",
				          "organization_id": "0000000000001",
				          "question_text": "満足度はどうだったでしょうか？",
				          "question_choices": [
				            {
				              "question_choice_id": "0000000000007",
				              "question_choice_text": "とても良かった",
				              "score": 5,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "0000000000008",
				              "question_choice_text": "良かった",
				              "score": 4,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "0000000000009",
				              "question_choice_text": "普通",
				              "score": 3,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "000000000000A",
				              "question_choice_text": "悪かった",
				              "score": 2,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "000000000000B",
				              "question_choice_text": "とても悪かった",
				              "score": 1,
				              "allow_free_text": false
				            }
				          ],
				          "max_choices": 1,
				          "required": true
				        },
				        {
				          "question_id": "0000000000007",
				          "organization_id": "0000000000001",
				          "question_text": "難易度は良かったでしょうか？",
				          "question_choices": [
				            {
				              "question_choice_id": "000000000000C",
				              "question_choice_text": "とても難しかった",
				              "score": 5,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "000000000000D",
				              "question_choice_text": "難しかった",
				              "score": 4,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "000000000000E",
				              "question_choice_text": "ちょうど良かった",
				              "score": 3,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "000000000000F",
				              "question_choice_text": "簡単だった",
				              "score": 2,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "000000000000G",
				              "question_choice_text": "簡単すぎた",
				              "score": 1,
				              "allow_free_text": false
				            }
				          ],
				          "max_choices": 1,
				          "required": true
				        },
				        {
				          "question_id": "0000000000008",
				          "organization_id": "0000000000001",
				          "question_text": "何かご意見があれば教えてください",
				          "required": false
				        }
				      ]
				    },
				    {
				      "question_group_id": "0000000000006",
				      "organization_id": "0000000000001",
				      "question_group_title": "セッションCの感想",
				      "question_group_type": "セッション毎の感想",
				      "questions": [
				        {
				          "question_id": "0000000000004",
				          "organization_id": "0000000000001",
				          "question_text": "満足度はどうだったでしょうか？",
				          "question_choices": [
				            {
				              "question_choice_id": "0000000000007",
				              "question_choice_text": "とても良かった",
				              "score": 5,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "0000000000008",
				              "question_choice_text": "良かった",
				              "score": 4,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "0000000000009",
				              "question_choice_text": "普通",
				              "score": 3,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "000000000000A",
				              "question_choice_text": "悪かった",
				              "score": 2,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "000000000000B",
				              "question_choice_text": "とても悪かった",
				              "score": 1,
				              "allow_free_text": false
				            }
				          ],
				          "max_choices": 1,
				          "required": true
				        },
				        {
				          "question_id": "0000000000007",
				          "organization_id": "0000000000001",
				          "question_text": "難易度は良かったでしょうか？",
				          "question_choices": [
				            {
				              "question_choice_id": "000000000000C",
				              "question_choice_text": "とても難しかった",
				              "score": 5,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "000000000000D",
				              "question_choice_text": "難しかった",
				              "score": 4,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "000000000000E",
				              "question_choice_text": "ちょうど良かった",
				              "score": 3,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "000000000000F",
				              "question_choice_text": "簡単だった",
				              "score": 2,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "000000000000G",
				              "question_choice_text": "簡単すぎた",
				              "score": 1,
				              "allow_free_text": false
				            }
				          ],
				          "max_choices": 1,
				          "required": true
				        },
				        {
				          "question_id": "0000000000008",
				          "organization_id": "0000000000001",
				          "question_text": "何かご意見があれば教えてください",
				          "required": false
				        }
				      ]
				    }
				  ]
				}
				""");
	}

	@Test
	@Order(5)
	void createAnswers() throws Exception {
		given(this.tsidGenerator.generate()).willReturn(TSID.from(14), TSID.from(15), TSID.from(16), TSID.from(17),
				TSID.from(18), TSID.from(19), TSID.from(20), TSID.from(21), TSID.from(22), TSID.from(23), TSID.from(24),
				TSID.from(25), TSID.from(26));
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/surveys/0000000000002/answers")
				.body("""
						{
						  "question_group_id": "0000000000003",
						  "question_id": "0000000000004",
						  "respondent_id": "demo1",
						  "choices": [
						    {
						      "question_choice_id": "0000000000007"
						    }
						  ]
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:%d/answers/000000000000E".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "answer_id": "000000000000E",
					  "survey_id": "0000000000002",
					  "question_group_id": "0000000000003",
					  "question_id": "0000000000004",
					  "respondent_id": "demo1",
					  "chosen_items": [
					    {
					      "question_choice_id": "0000000000007"
					    }
					  ]
					}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/surveys/0000000000002/answers")
				.body("""
						{
						  "question_group_id": "0000000000003",
						  "question_id": "0000000000004",
						  "respondent_id": "demo2",
						  "choices": [
						    {
						      "question_choice_id": "0000000000007"
						    }
						  ]
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:%d/answers/000000000000F".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "answer_id": "000000000000F",
					  "survey_id": "0000000000002",
					  "question_group_id": "0000000000003",
					  "question_id": "0000000000004",
					  "respondent_id": "demo2",
					  "chosen_items": [
					    {
					      "question_choice_id": "0000000000007"
					    }
					  ]
					}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/surveys/0000000000002/answers")
				.body("""
						{
						  "question_group_id": "0000000000003",
						  "question_id": "0000000000004",
						  "respondent_id": "demo3",
						  "choices": [
						    {
						      "question_choice_id": "0000000000008"
						    }
						  ]
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:%d/answers/000000000000G".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "answer_id": "000000000000G",
					  "survey_id": "0000000000002",
					  "question_group_id": "0000000000003",
					  "question_id": "0000000000004",
					  "respondent_id": "demo3",
					  "chosen_items": [
					    {
					      "question_choice_id": "0000000000008"
					    }
					  ]
					}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/surveys/0000000000002/answers")
				.body("""
						{
						  "question_group_id": "0000000000003",
						  "question_id": "0000000000004",
						  "respondent_id": "demo4",
						  "choices": [
						    {
						      "question_choice_id": "0000000000008"
						    }
						  ]
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:%d/answers/000000000000H".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "answer_id": "000000000000H",
					  "survey_id": "0000000000002",
					  "question_group_id": "0000000000003",
					  "question_id": "0000000000004",
					  "respondent_id": "demo4",
					  "chosen_items": [
					    {
					      "question_choice_id": "0000000000008"
					    }
					  ]
					}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/surveys/0000000000002/answers")
				.body("""
						{
						  "question_group_id": "0000000000003",
						  "question_id": "0000000000004",
						  "respondent_id": "demo5",
						  "choices": [
						    {
						      "question_choice_id": "0000000000009"
						    }
						  ]
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:%d/answers/000000000000J".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "answer_id": "000000000000J",
					  "survey_id": "0000000000002",
					  "question_group_id": "0000000000003",
					  "question_id": "0000000000004",
					  "respondent_id": "demo5",
					  "chosen_items": [
					    {
					      "question_choice_id": "0000000000009"
					    }
					  ]
					}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/surveys/0000000000002/answers")
				.body("""
						{
						  "question_group_id": "0000000000003",
						  "question_id": "0000000000004",
						  "respondent_id": "demo6",
						  "choices": [
						    {
						      "question_choice_id": "000000000000A"
						    }
						  ]
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:%d/answers/000000000000K".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "answer_id": "000000000000K",
					  "survey_id": "0000000000002",
					  "question_group_id": "0000000000003",
					  "question_id": "0000000000004",
					  "respondent_id": "demo6",
					  "chosen_items": [
					    {
					      "question_choice_id": "000000000000A"
					    }
					  ]
					}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/surveys/0000000000002/answers")
				.body("""
						{
						  "question_group_id": "0000000000003",
						  "question_id": "0000000000005",
						  "respondent_id": "demo1",
						  "answer_text": "Spring Boot 3への移行"
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:%d/answers/000000000000M".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "answer_id": "000000000000M",
					  "survey_id": "0000000000002",
					  "question_group_id": "0000000000003",
					  "question_id": "0000000000005",
					  "respondent_id": "demo1",
					  "answer_text": "Spring Boot 3への移行"
					}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/surveys/0000000000002/answers")
				.body("""
						{
						  "question_group_id": "0000000000003",
						  "question_id": "0000000000005",
						  "respondent_id": "demo2",
						  "answer_text": "Spring Security"
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:%d/answers/000000000000N".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "answer_id": "000000000000N",
					  "survey_id": "0000000000002",
					  "question_group_id": "0000000000003",
					  "question_id": "0000000000005",
					  "respondent_id": "demo2",
					  "answer_text": "Spring Security"
					}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/surveys/0000000000002/answers")
				.body("""
						{
						  "question_group_id": "0000000000003",
						  "question_id": "0000000000005",
						  "respondent_id": "demo3",
						  "answer_text": "Spring Integration"
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:%d/answers/000000000000P".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "answer_id": "000000000000P",
					  "survey_id": "0000000000002",
					  "question_group_id": "0000000000003",
					  "question_id": "0000000000005",
					  "respondent_id": "demo3",
					  "answer_text": "Spring Integration"
					}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/surveys/0000000000002/answers")
				.body("""
						{
						  "question_group_id": "0000000000003",
						  "question_id": "0000000000006",
						  "respondent_id": "demo1",
						  "answer_text": "ありがとうございました。"
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:%d/answers/000000000000Q".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "answer_id": "000000000000Q",
					  "survey_id": "0000000000002",
					  "question_group_id": "0000000000003",
					  "question_id": "0000000000006",
					  "respondent_id": "demo1",
					  "answer_text": "ありがとうございました。"
					}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/surveys/0000000000002/answers")
				.body("""
						{
						  "question_group_id": "0000000000003",
						  "question_id": "0000000000006",
						  "respondent_id": "demo2",
						  "answer_text": "お疲れ様でした。"
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:%d/answers/000000000000R".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "answer_id": "000000000000R",
					  "survey_id": "0000000000002",
					  "question_group_id": "0000000000003",
					  "question_id": "0000000000006",
					  "respondent_id": "demo2",
					  "answer_text": "お疲れ様でした。"
					}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/surveys/0000000000002/answers")
				.body("""
						{
						  "question_group_id": "0000000000003",
						  "question_id": "0000000000006",
						  "respondent_id": "demo3",
						  "answer_text": "次回も期待しています。"
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:%d/answers/000000000000S".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "answer_id": "000000000000S",
					  "survey_id": "0000000000002",
					  "question_group_id": "0000000000003",
					  "question_id": "0000000000006",
					  "respondent_id": "demo3",
					  "answer_text": "次回も期待しています。"
					}
					""");
		}
	}

	@Test
	@Order(6)
	void viewAnswer() throws Exception {
		final ResponseEntity<JsonNode> response = this.restClient.get()
			.uri("/surveys/0000000000002/answers")
			.headers(configureAuth())
			.retrieve()
			.toEntity(JsonNode.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
		assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
				[
				  {
				    "answer_id": "000000000000E",
				    "survey_id": "0000000000002",
				    "question_group_id": "0000000000003",
				    "question_id": "0000000000004",
				    "respondent_id": "demo1",
				    "chosen_items": [
				      {
				        "question_choice_id": "0000000000007"
				      }
				    ]
				  },
				  {
				    "answer_id": "000000000000F",
				    "survey_id": "0000000000002",
				    "question_group_id": "0000000000003",
				    "question_id": "0000000000004",
				    "respondent_id": "demo2",
				    "chosen_items": [
				      {
				        "question_choice_id": "0000000000007"
				      }
				    ]
				  },
				  {
				    "answer_id": "000000000000G",
				    "survey_id": "0000000000002",
				    "question_group_id": "0000000000003",
				    "question_id": "0000000000004",
				    "respondent_id": "demo3",
				    "chosen_items": [
				      {
				        "question_choice_id": "0000000000008"
				      }
				    ]
				  },
				  {
				    "answer_id": "000000000000H",
				    "survey_id": "0000000000002",
				    "question_group_id": "0000000000003",
				    "question_id": "0000000000004",
				    "respondent_id": "demo4",
				    "chosen_items": [
				      {
				        "question_choice_id": "0000000000008"
				      }
				    ]
				  },
				  {
				    "answer_id": "000000000000J",
				    "survey_id": "0000000000002",
				    "question_group_id": "0000000000003",
				    "question_id": "0000000000004",
				    "respondent_id": "demo5",
				    "chosen_items": [
				      {
				        "question_choice_id": "0000000000009"
				      }
				    ]
				  },
				  {
				    "answer_id": "000000000000K",
				    "survey_id": "0000000000002",
				    "question_group_id": "0000000000003",
				    "question_id": "0000000000004",
				    "respondent_id": "demo6",
				    "chosen_items": [
				      {
				        "question_choice_id": "000000000000A"
				      }
				    ]
				  },
				  {
				    "answer_id": "000000000000M",
				    "survey_id": "0000000000002",
				    "question_group_id": "0000000000003",
				    "question_id": "0000000000005",
				    "respondent_id": "demo1",
				    "answer_text": "Spring Boot 3への移行"
				  },
				  {
				    "answer_id": "000000000000N",
				    "survey_id": "0000000000002",
				    "question_group_id": "0000000000003",
				    "question_id": "0000000000005",
				    "respondent_id": "demo2",
				    "answer_text": "Spring Security"
				  },
				  {
				    "answer_id": "000000000000P",
				    "survey_id": "0000000000002",
				    "question_group_id": "0000000000003",
				    "question_id": "0000000000005",
				    "respondent_id": "demo3",
				    "answer_text": "Spring Integration"
				  },
				  {
				    "answer_id": "000000000000Q",
				    "survey_id": "0000000000002",
				    "question_group_id": "0000000000003",
				    "question_id": "0000000000006",
				    "respondent_id": "demo1",
				    "answer_text": "ありがとうございました。"
				  },
				  {
				    "answer_id": "000000000000R",
				    "survey_id": "0000000000002",
				    "question_group_id": "0000000000003",
				    "question_id": "0000000000006",
				    "respondent_id": "demo2",
				    "answer_text": "お疲れ様でした。"
				  },
				  {
				    "answer_id": "000000000000S",
				    "survey_id": "0000000000002",
				    "question_group_id": "0000000000003",
				    "question_id": "0000000000006",
				    "respondent_id": "demo3",
				    "answer_text": "次回も期待しています。"
				  }
				]
				""");
	}

}
