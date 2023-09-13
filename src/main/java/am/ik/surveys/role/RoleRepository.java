package am.ik.surveys.role;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import am.ik.surveys.util.FileLoader;
import jakarta.annotation.PostConstruct;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class RoleRepository {

	private final JdbcClient jdbcClient;

	private Map<String, Role> roleMap;

	private final ResultSetExtractor<List<Role>> resultSetExtractor = rs -> {
		final List<Role> roles = new ArrayList<>();
		Role role = null;
		while (rs.next()) {
			final RoleId roleId = RoleId.valueOf(rs.getBytes("role_id"));
			if (role == null || !Objects.equals(roleId, role.roleId())) {
				final String roleName = rs.getString("role_name");
				role = new Role(roleId, roleName, new LinkedHashSet<>());
				roles.add(role);
			}
			final PermissionId permissionId = PermissionId.valueOf(rs.getBytes("permission_id"));
			role.permissions().add(permissionId);
		}
		return roles;
	};

	public RoleRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public Role getByRoleName(String roleName) {
		return this.roleMap.get(roleName);
	}

	public List<Role> findAll() {
		return this.jdbcClient.sql(FileLoader.loadSqlAsString("sql/role/findAll.sql")).query(this.resultSetExtractor);
	}

	@PostConstruct
	void init() {
		this.roleMap = this.findAll().stream().collect(Collectors.toUnmodifiableMap(Role::roleName, r -> r));
	}

}
