package am.ik.surveys.question.web;

import am.ik.surveys.Fixtures;
import am.ik.surveys.question.DescriptiveQuestion;
import am.ik.surveys.question.MockQuestionRepository;
import am.ik.surveys.question.QuestionId;
import am.ik.surveys.question.QuestionRepository;
import am.ik.surveys.question.SelectiveQuestion;
import am.ik.surveys.tsid.TsidGenerator;
import com.github.f4b6a3.tsid.Tsid;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = QuestionController.class)
class QuestionControllerTest {

	@Autowired
	MockMvc mockMvc;

	@BeforeEach
	void clear() {
		MockQuestionRepository.clear();
	}

	@Test
	void getQuestion_200_selective() throws Exception {
		this.mockMvc.perform(get("/questions/{questionId}", Fixtures.q1.questionId().asString()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.question_id").value(Fixtures.q1.questionId().asString()))
			.andExpect(jsonPath("$.question_text").value(Fixtures.q1.questionText()))
			.andExpect(jsonPath("$.max_choices").value(1))
			.andExpect(jsonPath("$.question_choices.length()").value(2))
			.andExpect(jsonPath("$.question_choices[0].question_choice_id")
				.value(Fixtures.q1.questionChoices().get(0).questionChoiceId().asString()))
			.andExpect(jsonPath("$.question_choices[0].question_choice_text")
				.value(Fixtures.q1.questionChoices().get(0).questionChoiceText()))
			.andExpect(jsonPath("$.question_choices[0].allow_free_text")
				.value(Fixtures.q1.questionChoices().get(0).allowFreeText()))
			.andExpect(jsonPath("$.question_choices[1].question_choice_id")
				.value(Fixtures.q1.questionChoices().get(1).questionChoiceId().asString()))
			.andExpect(jsonPath("$.question_choices[1].question_choice_text")
				.value(Fixtures.q1.questionChoices().get(1).questionChoiceText()))
			.andExpect(jsonPath("$.question_choices[1].allow_free_text")
				.value(Fixtures.q1.questionChoices().get(1).allowFreeText()))
			.andExpect(jsonPath("$.created_at").value(Fixtures.q1.createdAt().toString()));
	}

	@Test
	void getQuestion_200_default() throws Exception {
		this.mockMvc.perform(get("/questions/{questionId}", Fixtures.q2.questionId().asString()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.question_id").value(Fixtures.q2.questionId().asString()))
			.andExpect(jsonPath("$.question_text").value(Fixtures.q2.questionText()))
			.andExpect(jsonPath("$.max_choices").doesNotExist())
			.andExpect(jsonPath("$.created_at").value(Fixtures.q2.createdAt().toString()));
	}

	@Test
	void getQuestion_404() throws Exception {
		this.mockMvc.perform(get("/questions/0C6VAWEVB33AC"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.detail").value("The given question id is not found (0C6VAWEVB33AC)"));
	}

	@Test
	@Disabled
	void getQuestions_200() throws Exception {
	}

	@Test
	void postQuestions_201_default() throws Exception {
		this.mockMvc.perform(post("/questions").contentType(MediaType.APPLICATION_JSON).content("""
				{"question_text": "How are you?"}
				"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.question_id").value("0C6VAWEVB33AD"))
			.andExpect(jsonPath("$.question_text").value("How are you?"))
			.andExpect(jsonPath("$.created_at").value("2023-04-29T08:12:54.874Z"))
			.andExpect(jsonPath("$.max_choices").doesNotExist())
			.andExpect(jsonPath("$.question_choices").doesNotExist())
			.andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/questions/0C6VAWEVB33AD"));

		assertThat(MockQuestionRepository.inserted).isInstanceOf(DescriptiveQuestion.class);
		assertThat(MockQuestionRepository.inserted.questionId()).isEqualTo(QuestionId.valueOf("0C6VAWEVB33AD"));
		assertThat(MockQuestionRepository.inserted.questionText()).isEqualTo("How are you?");
	}

	@Test
	void postQuestions_201_selective() throws Exception {
		this.mockMvc.perform(post("/questions").contentType(MediaType.APPLICATION_JSON).content("""
				{"question_text": "How are you?", "max_choices": 1}
				"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.question_id").value("0C6VAWEVB33AD"))
			.andExpect(jsonPath("$.question_text").value("How are you?"))
			.andExpect(jsonPath("$.created_at").value("2023-04-29T08:12:54.874Z"))
			.andExpect(jsonPath("$.max_choices").value(1))
			.andExpect(jsonPath("$.question_choices.length()").value(0))
			.andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/questions/0C6VAWEVB33AD"));

		assertThat(MockQuestionRepository.inserted).isInstanceOf(SelectiveQuestion.class);
		assertThat(MockQuestionRepository.inserted.questionId()).isEqualTo(QuestionId.valueOf("0C6VAWEVB33AD"));
		assertThat(MockQuestionRepository.inserted.questionText()).isEqualTo("How are you?");
		assertThat(((SelectiveQuestion) MockQuestionRepository.inserted).maxChoices()).isEqualTo(1);
		assertThat(((SelectiveQuestion) MockQuestionRepository.inserted).questionChoices()).isEmpty();
	}

	@TestConfiguration
	public static class Config {

		@Bean
		public TsidGenerator tsidGenerator() {
			return () -> Tsid.from("0C6VAWEVB33AD");
		}

		@Bean
		public QuestionRepository questionRepository() {
			return new MockQuestionRepository();
		}

	}

}