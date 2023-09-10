package am.ik.surveys;

import am.ik.surveys.config.MyBatisThymeleafConfig;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;

@TestConfiguration
@Import(MyBatisThymeleafConfig.class)
public class TestConfig {

	@Bean
	// TODO
	public JdbcClient jdbcClient(JdbcTemplate jdbcTemplate) {
		return JdbcClient.create(jdbcTemplate);
	}

}
