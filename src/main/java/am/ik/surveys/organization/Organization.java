package am.ik.surveys.organization;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

import am.ik.surveys.role.RoleId;
import am.ik.surveys.user.UserId;

public record Organization(OrganizationId organizationId, String organizationName, Set<OrganizationUser> users) {

	public Organization withOrganizationName(String organizationName) {
		return new Organization(this.organizationId, organizationName, this.users);
	}

	private Organization mutateUsers(Consumer<Set<OrganizationUser>> mutation) {
		final Set<OrganizationUser> users = new LinkedHashSet<>(this.users);
		mutation.accept(users);
		return new Organization(this.organizationId, this.organizationName, Collections.unmodifiableSet(users));
	}

	public Organization bind(UserId userId, RoleId roleId) {
		return this.mutateUsers(users -> users.add(new OrganizationUser(userId, roleId)));
	}

	public Organization unbind(UserId userId, RoleId roleId) {
		return this
			.mutateUsers(users -> users.removeIf(ou -> ou.userId().equals(userId) && ou.roleId().equals(roleId)));
	}

	public Organization remove(UserId userId) {
		return this.mutateUsers(users -> users.removeIf(ou -> ou.userId().equals(userId)));
	}
}
