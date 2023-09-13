package am.ik.surveys.role;

public record Permission(PermissionId permissionId, Resource resource, Verb verb) {
	public String toAuthority() {
		return "%s|%s".formatted(this.resource, this.verb);
	}
}
