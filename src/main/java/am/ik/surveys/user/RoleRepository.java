package am.ik.surveys.user;

import java.util.LinkedHashSet;

import am.ik.surveys.util.FileLoader;
import jakarta.annotation.PostConstruct;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class RoleRepository {

	private final JdbcClient jdbcClient;

	private Role admin;

	private Role voter;

	private final ResultSetExtractor<Role> resultSetExtractor = rs -> {
		Role role = null;
		while (rs.next()) {
			if (role == null) {
				final RoleId roleId = RoleId.valueOf(rs.getBytes("role_id"));
				final String roleName = rs.getString("role_name");
				role = new Role(roleId, roleName, new LinkedHashSet<>());
			}
			final PermissionId permissionId = PermissionId.valueOf(rs.getBytes("permission_id"));
			role.permissions().add(permissionId);
		}
		return role;
	};

	public RoleRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public Role admin() {
		return this.admin;
	}

	public Role voter() {
		return this.voter;
	}

	@Transactional(readOnly = true)
	public Role getByRoleName(String roleName) {
		return this.jdbcClient.sql(FileLoader.loadSqlAsString("sql/role/getByRoleName.sql"))
			.param("roleName", roleName)
			.query(this.resultSetExtractor);
	}

	@PostConstruct
	void init() {
		this.admin = this.getByRoleName(SystemRoleName.ADMIN);
		this.voter = this.getByRoleName(SystemRoleName.VOTER);
	}

}
