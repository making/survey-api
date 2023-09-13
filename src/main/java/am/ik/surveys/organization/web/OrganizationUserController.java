package am.ik.surveys.organization.web;

import am.ik.surveys.organization.Organization;
import am.ik.surveys.organization.OrganizationId;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/organizations/{organizationId}")
public class OrganizationUserController {

	private final OrganizationHandler organizationHandler;

	public OrganizationUserController(OrganizationHandler organizationHandler) {
		this.organizationHandler = organizationHandler;
	}

	@PutMapping(path = "/organization_users")
	public Organization putOrganizationUser(@PathVariable OrganizationId organizationId,
			@RequestBody OrganizationUserRequest request) {
		return this.organizationHandler.bindUser(organizationId, request);
	}

	@DeleteMapping(path = "/organization_users")
	public Organization deleteOrganizationUser(@PathVariable OrganizationId organizationId,
			@RequestBody OrganizationUserRequest request) {
		return this.organizationHandler.unbindUser(organizationId, request);
	}

}
