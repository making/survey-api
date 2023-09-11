package am.ik.surveys.organization;

import java.util.LinkedHashSet;
import java.util.Set;

import am.ik.surveys.TestConfig;
import am.ik.surveys.user.Role;
import am.ik.surveys.user.RoleRepository;
import am.ik.surveys.user.User;
import am.ik.surveys.user.UserId;
import am.ik.surveys.user.UserRepository;
import io.hypersistence.tsid.TSID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
@Import({ TestConfig.class, OrganizationRepository.class, RoleRepository.class, UserRepository.class })
class OrganizationRepositoryTest {

	@Autowired
	OrganizationRepository organizationRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	UserRepository userRepository;

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

	Role admin;

	Role voter;

	User user1;

	User user2;

	User user3;

	@BeforeEach
	void setup() {
		if (admin == null) {
			admin = this.roleRepository.admin();
		}
		if (voter == null) {
			voter = this.roleRepository.voter();
		}
		if (user1 == null) {
			user1 = new User(new UserId(TSID.fast()), "user1@example.com", "{noop}password");
			this.userRepository.insert(user1);
		}
		if (user2 == null) {
			user2 = new User(new UserId(TSID.fast()), "user2@example.com", "{noop}password");
			this.userRepository.insert(user2);
		}
		if (user3 == null) {
			user3 = new User(new UserId(TSID.fast()), "user3@example.com", "{noop}password");
			this.userRepository.insert(user3);
		}
	}

	@Test
	void saveAndFindAndModifyAndFind() {
		OrganizationId organizationId = new OrganizationId(TSID.fast());
		Organization organization = new Organization(organizationId, "test",
				new LinkedHashSet<>(Set.of(new OrganizationUser(this.user1.userId(), this.admin.roleId()))));
		this.organizationRepository.save(organization);
		Organization found = this.organizationRepository.findByOrganizationId(organizationId)
			.orElseThrow(() -> new IllegalStateException("not found: " + organizationId));
		assertThat(found).isEqualTo(organization);
		Organization added = found.bind(this.user2.userId(), this.voter.roleId())
			.bind(this.user3.userId(), this.voter.roleId())
			.withOrganizationName("test2");
		this.organizationRepository.save(added);
		Organization found2 = this.organizationRepository.findByOrganizationId(organizationId)
			.orElseThrow(() -> new IllegalStateException("not found: " + organizationId));
		assertThat(found2).isEqualTo(added);
		Organization deleted = found2.unbind(this.user2.userId(), this.voter.roleId()).withOrganizationName("test3");
		this.organizationRepository.save(deleted);
		Organization found3 = this.organizationRepository.findByOrganizationId(organizationId)
			.orElseThrow(() -> new IllegalStateException("not found: " + organizationId));
		assertThat(found3).isEqualTo(deleted);

		System.out.println("found2=" + found2);
		System.out.println("found3=" + found3);
	}

}