package am.ik.surveys.user;

import java.util.Set;

public record Role(RoleId roleId, String roleName, Set<PermissionId> permissions) {

}
