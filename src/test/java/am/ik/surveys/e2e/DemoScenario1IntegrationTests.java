package am.ik.surveys.e2e;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
public class DemoScenario1IntegrationTests {

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

	Map<String, String> tokens = new ConcurrentHashMap<>();

	@BeforeEach
	void setup() {
		this.restClient = RestClient.builder().baseUrl("http://localhost:%d".formatted(this.port)).build();
	}

	@Test
	@Order(1)
	void createUser() throws Exception {
		given(this.tsidGenerator.generate()).willReturn(TSID.from(0), TSID.from(1), TSID.from(2), TSID.from(3),
				TSID.from(4), TSID.from(5), TSID.from(6));
		{
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
		for (int i = 1; i <= 6; i++) {
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.body("""
						{
						  "email": "voter%d@example.com",
						  "password":  "Voter%d23!"
						}
						""".formatted(i, i))
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "user_id": "000000000000%d",
					  "email": "voter%d@example.com"
					}
					""".formatted(i, i));
		}
	}

	String retrieveToken(String email, String password) {
		return this.tokens.computeIfAbsent(email,
				s -> this.restClient.post()
					.uri("/token")
					.headers(headers -> headers.setBasicAuth(email, password))
					.retrieve()
					.body(String.class));
	}

	Consumer<HttpHeaders> configureAdminAuth() {
		final String token = this.retrieveToken("admin@example.com", "Admin123!");
		return headers -> headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
	}

	Consumer<HttpHeaders> configureVoterAuth(int voterNo) {
		final String token = this.retrieveToken("voter%d@example.com".formatted(voterNo),
				"Voter%d23!".formatted(voterNo));
		return headers -> headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
	}

	@Test
	@Order(2)
	void createOrganization() throws Exception {
		given(this.tsidGenerator.generate()).willReturn(TSID.from(1));
		final ResponseEntity<JsonNode> response = this.restClient.post()
			.uri("/organizations")
			.contentType(MediaType.APPLICATION_JSON)
			.headers(configureAdminAuth())
			.body("""
					{
					  "organization_name": "Test Org"
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
				  "organization_name": "Test Org",
				  "users": [
				    {
				      "user_id": "0000000000000",
				      "role_id": "0DHYC9EAMX7EG"
				    }
				  ]
				}
				""");
	}

	@Test
	@Order(3)
	void putOrganizationUser() throws Exception {
		for (int i = 1; i <= 6; i++) {

			final ResponseEntity<JsonNode> response = this.restClient.put()
				.uri("/organizations/0000000000001/organization_users")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAdminAuth())
				.body("""
						{
						  "email": "voter%d@example.com",
						  "role_name": "voter"
						}
						""".formatted(i))
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(this.json.write(response.getBody())).isEqualToJson("""
					{
					  "organization_id": "0000000000001",
					  "organization_name": "Test Org"
					}
					""");
		}
	}

	@Test
	@Order(3)
	void createSurvey() throws Exception {
		given(this.tsidGenerator.generate()).willReturn(TSID.from(2));
		final ResponseEntity<JsonNode> response = this.restClient.post()
			.uri("/organizations/0000000000001/surveys")
			.contentType(MediaType.APPLICATION_JSON)
			.headers(configureAdminAuth())
			.body("""
					{
					  "survey_title":"テストアンケート",
					  "start_date_time":"2023-10-01T00:00:00.000+09:00",
					  "end_date_time":"2024-10-01T00:00:00.000+09:00",
					  "is_public": false
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
				  "survey_title": "テストアンケート",
				  "start_date_time": "2023-09-30T15:00:00Z",
				  "end_date_time": "2024-09-30T15:00:00Z",
				  "organization_id": "0000000000001",
				  "is_public": false
				}
				""");
	}

	@Test
	@Order(3)
	void createQuestionGroup() throws Exception {
		given(this.tsidGenerator.generate()).willReturn(TSID.from(3));
		final ResponseEntity<JsonNode> response = this.restClient.post()
			.uri("/organizations/0000000000001/question_groups")
			.contentType(MediaType.APPLICATION_JSON)
			.headers(configureAdminAuth())
			.body("""
					{
					  "question_group_title": "設計に関するアンケート",
					  "question_group_type": "default"
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
				  "question_group_title": "設計に関するアンケート",
				  "question_group_type": "default"
				}
				""");
	}

	@Test
	@Order(3)
	void createQuestions() throws Exception {
		given(this.tsidGenerator.generate()).willReturn(TSID.from(4), TSID.from(5), TSID.from(6));
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/organizations/0000000000001/questions")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAdminAuth())
				.body("""
						{
						  "question_text": "この設計はいけてますか?",
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
					  "question_text": "この設計はいけてますか?",
					  "question_choices": [],
					  "max_choices": 1
					}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/organizations/0000000000001/questions")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAdminAuth())
				.body("""
						{
						  "question_text": "どういうところがいけてますか?"
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
					  "question_text": "どういうところがいけてますか?"
					}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/organizations/0000000000001/questions")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAdminAuth())
				.body("""
						{
						  "question_text": "他にも取り上げて欲しい設計がありますか?",
						  "max_choices": 3
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
					  "question_text": "他にも取り上げて欲しい設計がありますか?",
					  "question_choices": [],
					  "max_choices": 3
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
				.headers(configureAdminAuth())
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
				.headers(configureAdminAuth())
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
				.headers(configureAdminAuth())
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
					  "question_id": "0000000000006",
					  "required": true
					}
					""");
		}
	}

	@Test
	@Order(4)
	void mapQuestionGroupToSurvey() throws Exception {
		final ResponseEntity<JsonNode> response = this.restClient.put()
			.uri("/surveys/0000000000002/survey_question_groups/0000000000003")
			.contentType(MediaType.APPLICATION_JSON)
			.headers(configureAdminAuth())
			.retrieve()
			.toEntity(JsonNode.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
		assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
				{
				  "survey_id": "0000000000002",
				  "question_group_id": "0000000000003"
				}
				""");

	}

	@Test
	@Order(4)
	void addQuestionChoices() throws Exception {
		given(this.tsidGenerator.generate()).willReturn(TSID.from(7), TSID.from(8), TSID.from(9), TSID.from(10),
				TSID.from(11), TSID.from(12), TSID.from(13));
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/questions/0000000000004/question_choices")
				.body("""
						{
						  "question_choice_text": "はい",
						  "score": 1,
						  "allow_free_text": false
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAdminAuth())
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create(
					"http://localhost:%d/questions/0000000000004/question_choices/0000000000007".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
							{
							  "question_choice_id":"0000000000007",
							  "question_choice_text": "はい",
							  "score": 1,
							  "allow_free_text": false
							}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/questions/0000000000004/question_choices")
				.body("""
						{
						  "question_choice_text": "いいえ",
						  "score": 0,
						  "allow_free_text": false
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAdminAuth())
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create(
					"http://localhost:%d/questions/0000000000004/question_choices/0000000000008".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
							{
							  "question_choice_id":"0000000000008",
							  "question_choice_text": "いいえ",
							  "score": 0,
							  "allow_free_text": false
							}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/questions/0000000000006/question_choices")
				.body("""
						{
						  "question_choice_text": "在庫",
						  "score": 0,
						  "allow_free_text": false
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAdminAuth())
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create(
					"http://localhost:%d/questions/0000000000006/question_choices/0000000000009".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
							{
							  "question_choice_id":"0000000000009",
							  "question_choice_text": "在庫",
							  "score": 0,
							  "allow_free_text": false
							}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/questions/0000000000006/question_choices")
				.body("""
						{
						  "question_choice_text": "カート",
						  "score": 0,
						  "allow_free_text": false
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAdminAuth())
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create(
					"http://localhost:%d/questions/0000000000006/question_choices/000000000000A".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
							{
							  "question_choice_id":"000000000000A",
							  "question_choice_text": "カート",
							  "score": 0,
							  "allow_free_text": false
							}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/questions/0000000000006/question_choices")
				.body("""
						{
						  "question_choice_text": "お気に入り",
						  "score": 0,
						  "allow_free_text": false
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAdminAuth())
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create(
					"http://localhost:%d/questions/0000000000006/question_choices/000000000000B".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
							{
							  "question_choice_id":"000000000000B",
							  "question_choice_text": "お気に入り",
							  "score": 0,
							  "allow_free_text": false
							}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/questions/0000000000006/question_choices")
				.body("""
						{
						  "question_choice_text": "リコメンド",
						  "score": 0,
						  "allow_free_text": false
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAdminAuth())
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create(
					"http://localhost:%d/questions/0000000000006/question_choices/000000000000C".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
							{
							  "question_choice_id":"000000000000C",
							  "question_choice_text": "リコメンド",
							  "score": 0,
							  "allow_free_text": false
							}
					""");
		}
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/questions/0000000000006/question_choices")
				.body("""
						{
						  "question_choice_text": "その他",
						  "score": 0,
						  "allow_free_text": true
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureAdminAuth())
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create(
					"http://localhost:%d/questions/0000000000006/question_choices/000000000000D".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
							{
							  "question_choice_id":"000000000000D",
							  "question_choice_text": "その他",
							  "score": 0,
							  "allow_free_text": true
							}
					""");
		}
	}

	@Test
	@Order(5)
	void viewSurvey() throws Exception {
		final ResponseEntity<JsonNode> response = this.restClient.get()
			.uri("/surveys/0000000000002?include_questions=true")
			.headers(configureVoterAuth(1))
			.retrieve()
			.toEntity(JsonNode.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
		assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
				{
				  "survey_id": "0000000000002",
				  "survey_title": "テストアンケート",
				  "start_date_time": "2023-09-30T15:00:00Z",
				  "end_date_time": "2024-09-30T15:00:00Z",
				  "organization_id": "0000000000001",
				  "is_public": false,
				  "question_groups": [
				    {
				      "question_group_id": "0000000000003",
				      "organization_id": "0000000000001",
				      "question_group_title": "設計に関するアンケート",
				      "question_group_type": "default",
				      "questions": [
				        {
				          "question_id": "0000000000004",
				          "organization_id": "0000000000001",
				          "question_text": "この設計はいけてますか?",
				          "question_choices": [
				            {
				              "question_choice_id": "0000000000007",
				              "question_choice_text": "はい",
				              "score": 1,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "0000000000008",
				              "question_choice_text": "いいえ",
				              "score": 0,
				              "allow_free_text": false
				            }
				          ],
				          "max_choices": 1,
				          "required": true
				        },
				        {
				          "question_id": "0000000000005",
				          "organization_id": "0000000000001",
				          "question_text": "どういうところがいけてますか?",
				          "required": false
				        },
				        {
				          "question_id": "0000000000006",
				          "organization_id": "0000000000001",
				          "question_text": "他にも取り上げて欲しい設計がありますか?",
				          "question_choices": [
				            {
				              "question_choice_id": "0000000000009",
				              "question_choice_text": "在庫",
				              "score": 0,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "000000000000A",
				              "question_choice_text": "カート",
				              "score": 0,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "000000000000B",
				              "question_choice_text": "お気に入り",
				              "score": 0,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "000000000000C",
				              "question_choice_text": "リコメンド",
				              "score": 0,
				              "allow_free_text": false
				            },
				            {
				              "question_choice_id": "000000000000D",
				              "question_choice_text": "その他",
				              "score": 0,
				              "allow_free_text": true
				            }
				          ],
				          "max_choices": 3,
				          "required": true
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
				TSID.from(25), TSID.from(26), TSID.from(27), TSID.from(28), TSID.from(29), TSID.from(30));
		{
			final ResponseEntity<JsonNode> response = this.restClient.post()
				.uri("/surveys/0000000000002/answers")
				.body("""
						{
						  "question_group_id": "0000000000003",
						  "question_id": "0000000000004",
						  "respondent_id": "0000000000001",
						  "choices": [
						    {
						      "question_choice_id": "0000000000007"
						    }
						  ]
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureVoterAuth(1))
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
					  "respondent_id": "0000000000001",
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
						  "respondent_id": "0000000000002",
						  "choices": [
						    {
						      "question_choice_id": "0000000000007"
						    }
						  ]
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureVoterAuth(2))
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
					  "respondent_id": "0000000000002",
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
						  "respondent_id": "0000000000003",
						  "choices": [
						    {
						      "question_choice_id": "0000000000007"
						    }
						  ]
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureVoterAuth(3))
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
					  "respondent_id": "0000000000003",
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
						  "respondent_id": "0000000000004",
						  "choices": [
						    {
						      "question_choice_id": "0000000000007"
						    }
						  ]
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureVoterAuth(4))
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
					  "respondent_id": "0000000000004",
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
						  "respondent_id": "0000000000005",
						  "choices": [
						    {
						      "question_choice_id": "0000000000008"
						    }
						  ]
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureVoterAuth(5))
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
					  "respondent_id": "0000000000005",
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
						  "respondent_id": "0000000000006",
						  "choices": [
						    {
						      "question_choice_id": "0000000000008"
						    }
						  ]
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureVoterAuth(6))
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
					  "respondent_id": "0000000000006",
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
						  "question_id": "0000000000005",
						  "respondent_id": "0000000000001",
						  "answer_text": "具体的なデータがあってわかりやすい"
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureVoterAuth(1))
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
					  "respondent_id": "0000000000001",
					  "answer_text": "具体的なデータがあってわかりやすい"
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
						  "respondent_id": "0000000000002",
						  "answer_text": "ER図がわかりやすい"
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureVoterAuth(2))
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
					  "respondent_id": "0000000000002",
					  "answer_text": "ER図がわかりやすい"
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
						  "respondent_id": "0000000000003",
						  "answer_text": "ここまで複雑なモデルが必要なの?"
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureVoterAuth(3))
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
					  "respondent_id": "0000000000003",
					  "answer_text": "ここまで複雑なモデルが必要なの?"
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
						  "respondent_id": "0000000000001",
						  "choices": [
						    {
						      "question_choice_id": "0000000000009"
						    }
						  ]
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureVoterAuth(1))
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
					  "respondent_id": "0000000000001",
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
						  "question_id": "0000000000006",
						  "respondent_id": "0000000000002",
						  "choices": [
						    {
						      "question_choice_id": "0000000000009"
						    },
						    {
						      "question_choice_id": "000000000000A"
						    }
						  ]
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureVoterAuth(2))
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
					  "respondent_id": "0000000000002",
					  "chosen_items": [
					    {
					      "question_choice_id": "0000000000009"
					    },
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
						  "question_id": "0000000000006",
						  "respondent_id": "0000000000003",
						  "choices": [
						    {
						      "question_choice_id": "000000000000A"
						    },
						    {
						      "question_choice_id": "000000000000B"
						    }
						  ]
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureVoterAuth(3))
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
					  "respondent_id": "0000000000003",
					  "chosen_items": [
					    {
					      "question_choice_id": "000000000000A"
					    },
					    {
					      "question_choice_id": "000000000000B"
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
						  "question_id": "0000000000006",
						  "respondent_id": "0000000000004",
						  "choices": [
						    {
						      "question_choice_id": "000000000000C"
						    }
						  ]
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureVoterAuth(4))
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:%d/answers/000000000000T".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "answer_id": "000000000000T",
					  "survey_id": "0000000000002",
					  "question_group_id": "0000000000003",
					  "question_id": "0000000000006",
					  "respondent_id": "0000000000004",
					  "chosen_items": [
					    {
					      "question_choice_id": "000000000000C"
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
						  "question_id": "0000000000006",
						  "respondent_id": "0000000000005",
						  "choices": [
						    {
						      "question_choice_id": "000000000000C"
						    }
						  ]
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureVoterAuth(5))
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:%d/answers/000000000000V".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "answer_id": "000000000000V",
					  "survey_id": "0000000000002",
					  "question_group_id": "0000000000003",
					  "question_id": "0000000000006",
					  "respondent_id": "0000000000005",
					  "chosen_items": [
					    {
					      "question_choice_id": "000000000000C"
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
						  "question_id": "0000000000006",
						  "respondent_id": "0000000000006",
						  "choices": [
						    {
						      "question_choice_id": "000000000000D",
						      "answer_text": "検索"
						    }
						  ]
						}
						""")
				.contentType(MediaType.APPLICATION_JSON)
				.headers(configureVoterAuth(6))
				.retrieve()
				.toEntity(JsonNode.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
			assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
			assertThat(response.getHeaders().getLocation())
				.isEqualTo(URI.create("http://localhost:%d/answers/000000000000W".formatted(this.port)));
			assertThat(this.json.write(response.getBody())).isStrictlyEqualToJson("""
					{
					  "answer_id": "000000000000W",
					  "survey_id": "0000000000002",
					  "question_group_id": "0000000000003",
					  "question_id": "0000000000006",
					  "respondent_id": "0000000000006",
					  "chosen_items": [
					    {
					      "question_choice_id": "000000000000D",
					      "answer_text": "検索"
					    }
					  ]
					}
					""");
		}
	}

	@Test
	@Order(6)
	void viewAnswer() throws Exception {
		final ResponseEntity<JsonNode> response = this.restClient.get()
			.uri("/surveys/0000000000002/answers")
			.headers(configureAdminAuth())
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
				    "respondent_id": "0000000000001",
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
				    "respondent_id": "0000000000002",
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
				    "respondent_id": "0000000000003",
				    "chosen_items": [
				      {
				        "question_choice_id": "0000000000007"
				      }
				    ]
				  },
				  {
				    "answer_id": "000000000000H",
				    "survey_id": "0000000000002",
				    "question_group_id": "0000000000003",
				    "question_id": "0000000000004",
				    "respondent_id": "0000000000004",
				    "chosen_items": [
				      {
				        "question_choice_id": "0000000000007"
				      }
				    ]
				  },
				  {
				    "answer_id": "000000000000J",
				    "survey_id": "0000000000002",
				    "question_group_id": "0000000000003",
				    "question_id": "0000000000004",
				    "respondent_id": "0000000000005",
				    "chosen_items": [
				      {
				        "question_choice_id": "0000000000008"
				      }
				    ]
				  },
				  {
				    "answer_id": "000000000000K",
				    "survey_id": "0000000000002",
				    "question_group_id": "0000000000003",
				    "question_id": "0000000000004",
				    "respondent_id": "0000000000006",
				    "chosen_items": [
				      {
				        "question_choice_id": "0000000000008"
				      }
				    ]
				  },
				  {
				    "answer_id": "000000000000M",
				    "survey_id": "0000000000002",
				    "question_group_id": "0000000000003",
				    "question_id": "0000000000005",
				    "respondent_id": "0000000000001",
				    "answer_text": "具体的なデータがあってわかりやすい"
				  },
				  {
				    "answer_id": "000000000000N",
				    "survey_id": "0000000000002",
				    "question_group_id": "0000000000003",
				    "question_id": "0000000000005",
				    "respondent_id": "0000000000002",
				    "answer_text": "ER図がわかりやすい"
				  },
				  {
				    "answer_id": "000000000000P",
				    "survey_id": "0000000000002",
				    "question_group_id": "0000000000003",
				    "question_id": "0000000000005",
				    "respondent_id": "0000000000003",
				    "answer_text": "ここまで複雑なモデルが必要なの?"
				  },
				  {
				    "answer_id": "000000000000Q",
				    "survey_id": "0000000000002",
				    "question_group_id": "0000000000003",
				    "question_id": "0000000000006",
				    "respondent_id": "0000000000001",
				    "chosen_items": [
				      {
				        "question_choice_id": "0000000000009"
				      }
				    ]
				  },
				  {
				    "answer_id": "000000000000R",
				    "survey_id": "0000000000002",
				    "question_group_id": "0000000000003",
				    "question_id": "0000000000006",
				    "respondent_id": "0000000000002",
				    "chosen_items": [
				      {
				        "question_choice_id": "0000000000009"
				      },
				      {
				        "question_choice_id": "000000000000A"
				      }
				    ]
				  },
				  {
				    "answer_id": "000000000000S",
				    "survey_id": "0000000000002",
				    "question_group_id": "0000000000003",
				    "question_id": "0000000000006",
				    "respondent_id": "0000000000003",
				    "chosen_items": [
				      {
				        "question_choice_id": "000000000000A"
				      },
				      {
				        "question_choice_id": "000000000000B"
				      }
				    ]
				  },
				  {
				    "answer_id": "000000000000T",
				    "survey_id": "0000000000002",
				    "question_group_id": "0000000000003",
				    "question_id": "0000000000006",
				    "respondent_id": "0000000000004",
				    "chosen_items": [
				      {
				        "question_choice_id": "000000000000C"
				      }
				    ]
				  },
				  {
				    "answer_id": "000000000000V",
				    "survey_id": "0000000000002",
				    "question_group_id": "0000000000003",
				    "question_id": "0000000000006",
				    "respondent_id": "0000000000005",
				    "chosen_items": [
				      {
				        "question_choice_id": "000000000000C"
				      }
				    ]
				  },
				  {
				    "answer_id": "000000000000W",
				    "survey_id": "0000000000002",
				    "question_group_id": "0000000000003",
				    "question_id": "0000000000006",
				    "respondent_id": "0000000000006",
				    "chosen_items": [
				      {
				        "question_choice_id": "000000000000D",
				        "answer_text": "検索"
				      }
				    ]
				  }
				]
				""");
	}

}
