package am.ik.surveys.organization;

import am.ik.surveys.role.RoleId;
import am.ik.surveys.user.UserId;

public record OrganizationUser(UserId userId, RoleId roleId) {
}
