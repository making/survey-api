package am.ik.surveys.organization;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import am.ik.surveys.role.RoleId;
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
