package am.ik.surveys.role;

import java.util.Set;

public record Role(RoleId roleId, String roleName, Set<PermissionId> permissions) {

}
