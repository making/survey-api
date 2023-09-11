package am.ik.surveys.user;

public record Permission(PermissionId permissionId, Resource resource, Verb verb) {
}
