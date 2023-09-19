package am.ik.surveys.user;

import java.util.Optional;

import am.ik.surveys.util.FileLoader;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class UserRepository {

	private final JdbcClient jdbcClient;

	final RowMapper<User> rowMapper = (rs, rowNum) -> {
		final UserId userId = UserId.valueOf(rs.getLong("user_id"));
		final String email = rs.getString("email");
		final String password = rs.getString("password");
		return new User(userId, email, password);
	};

	public UserRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	@Transactional(readOnly = true)
	public Optional<User> findByUserId(UserId userId) {
		return this.jdbcClient.sql(FileLoader.loadSqlAsString("sql/user/findByUserId.sql"))
			.param("userId", userId.asLong())
			.query(this.rowMapper)
			.optional();
	}

	@Transactional(readOnly = true)
	public Optional<User> findByEmail(String email) {
		return this.jdbcClient.sql(FileLoader.loadSqlAsString("sql/user/findByEmail.sql"))
			.param("email", email)
			.query(this.rowMapper)
			.optional();
	}

	public int insert(User user) {
		return this.jdbcClient.sql(FileLoader.loadSqlAsString("sql/user/insert.sql"))
			.param("userId", user.userId().asLong())
			.param("email", user.email())
			.param("password", user.password())
			.update();
	}

}
