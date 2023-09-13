package am.ik.surveys.organization;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import am.ik.surveys.role.Permission;
import am.ik.surveys.role.PermissionId;
import am.ik.surveys.role.Resource;
import am.ik.surveys.role.RoleId;
import am.ik.surveys.role.Verb;
import am.ik.surveys.user.User;
import am.ik.surveys.user.UserId;
import am.ik.surveys.util.FileLoader;
import am.ik.surveys.util.SetDiff;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class OrganizationRepository {

	private final JdbcClient jdbcClient;

	private final NamedParameterJdbcTemplate jdbcTemplate;

	private final ResultSetExtractor<Optional<Organization>> resultSetExtractor = rs -> {
		Organization organization = null;
		while (rs.next()) {
			if (organization == null) {
				final OrganizationId organizationId = OrganizationId.valueOf(rs.getBytes("organization_id"));
				final String organizationName = rs.getString("organization_name");
				organization = new Organization(organizationId, organizationName, new LinkedHashSet<>());
			}
			final UserId userId = UserId.valueOf(rs.getBytes("user_id"));
			final RoleId roleId = RoleId.valueOf(rs.getBytes("role_id"));
			organization.users().add(new OrganizationUser(userId, roleId));
		}
		return Optional.ofNullable(organization);
	};

	private final ResultSetExtractor<Optional<OrganizationUserDetail>> detailsResultSetExtractor = rs -> {
		OrganizationUserDetail userDetail = null;
		Organization previousOrganization = null;
		final Map<PermissionId, Permission> permissionCache = new HashMap<>();
		while (rs.next()) {
			final UserId userId = UserId.valueOf(rs.getBytes("user_id"));
			final String email = rs.getString("email");
			final String password = rs.getString("password");
			if (userDetail == null) {
				userDetail = new OrganizationUserDetail(new User(userId, email, password), new LinkedHashMap<>());
			}
			final byte[] organizationIdBytes = rs.getBytes("organization_id");
			if (organizationIdBytes == null) {
				break;
			}
			final OrganizationId organizationId = OrganizationId.valueOf(organizationIdBytes);
			final String organizationName = rs.getString("organization_name");
			if (previousOrganization == null || !previousOrganization.organizationId().equals(organizationId)) {
				previousOrganization = new Organization(organizationId, organizationName, Set.of());
			}
			final byte[] permissionIdBytes = rs.getBytes("permission_id");
			if (permissionIdBytes == null) {
				continue;
			}
			final PermissionId permissionId = PermissionId.valueOf(permissionIdBytes);
			final Permission permission = permissionCache.computeIfAbsent(permissionId, id -> {
				try {
					final Resource resource = Resource.valueFrom(rs.getString("resource"));
					final Verb verb = Verb.valueFrom(rs.getString("verb"));
					return new Permission(permissionId, resource, verb);
				}
				catch (SQLException e) {
					throw new IllegalStateException(e);
				}
			});
			userDetail.permissions()
				.computeIfAbsent(previousOrganization, organization -> new LinkedHashSet<>())
				.add(permission);
		}
		return Optional.ofNullable(userDetail).map(OrganizationUserDetail::freeze);
	};

	public OrganizationRepository(JdbcClient jdbcClient, NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcClient = jdbcClient;
		this.jdbcTemplate = jdbcTemplate;
	}

	@Transactional(readOnly = true)
	public Optional<Organization> findByOrganizationId(OrganizationId organizationId) {
		return this.jdbcClient.sql(FileLoader.loadSqlAsString("sql/organization/findByOrganizationId.sql"))
			.param("organizationId", organizationId.asBytes())
			.query(this.resultSetExtractor);
	}

	@Transactional(readOnly = true)
	public Optional<OrganizationUserDetail> findDetailByEmail(String email) {
		return this.jdbcClient.sql(FileLoader.loadSqlAsString("sql/organizationuser/findDetailsByEmail.sql"))
			.param("email", email)
			.query(this.detailsResultSetExtractor);
	}

	public void save(Organization organization) {
		final Optional<Organization> beforeOptional = this.findByOrganizationId(organization.organizationId());
		if (beforeOptional.isPresent()) {
			final Organization before = beforeOptional.get();
			// UPDATE organization
			this.jdbcClient.sql(FileLoader.loadSqlAsString("sql/organization/update.sql"))
				.param("organizationId", organization.organizationId().asBytes())
				.param("organizationName", organization.organizationName())
				.update();
			final SetDiff<OrganizationUser> setDiff = SetDiff.before(before.users()).after(organization.users());
			// INSERT organization_user
			final Set<OrganizationUser> added = setDiff.added();
			if (!added.isEmpty()) {
				this.jdbcTemplate.batchUpdate(FileLoader.loadSqlAsString("sql/organizationuser/insert.sql"),
						organizationUsersToParams(organization.organizationId(), added));
			}
			// DELETE organization_user
			final Set<OrganizationUser> deleted = setDiff.deleted();
			if (!deleted.isEmpty()) {
				this.jdbcTemplate.batchUpdate(FileLoader.loadSqlAsString("sql/organizationuser/delete.sql"),
						organizationUsersToParams(organization.organizationId(), deleted));
			}
		}
		else {
			// INSERT organization
			this.jdbcClient.sql(FileLoader.loadSqlAsString("sql/organization/insert.sql"))
				.param("organizationId", organization.organizationId().asBytes())
				.param("organizationName", organization.organizationName())
				.update();
			// INSERT organization_user
			this.jdbcTemplate.batchUpdate(FileLoader.loadSqlAsString("sql/organizationuser/insert.sql"),
					organizationUsersToParams(organization.organizationId(), organization.users()));
		}
	}

	static SqlParameterSource[] organizationUsersToParams(OrganizationId organizationId,
			Collection<OrganizationUser> organizationUsers) {
		return organizationUsers.stream()
			.map(ou -> new MapSqlParameterSource().addValue("organizationId", organizationId.asBytes())
				.addValue("userId", ou.userId().asBytes())
				.addValue("roleId", ou.roleId().asBytes()))
			.toArray(SqlParameterSource[]::new);
	}

}
