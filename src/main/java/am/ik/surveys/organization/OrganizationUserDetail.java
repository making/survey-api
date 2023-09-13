package am.ik.surveys.organization;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import am.ik.surveys.role.Permission;
import am.ik.surveys.user.User;

public record OrganizationUserDetail(User user, Map<Organization, Set<Permission>> permissions) {

	public OrganizationUserDetail freeze() {
		return new OrganizationUserDetail(this.user, Collections.unmodifiableMap(this.permissions));
	}
}
