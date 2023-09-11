package am.ik.surveys.organization.web;

import java.net.URI;

import am.ik.surveys.organization.Organization;
import am.ik.surveys.organization.OrganizationId;
import am.ik.surveys.organization.OrganizationRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping(path = "/organizations")
public class OrganizationController {

	private final OrganizationRepository organizationRepository;

	private final OrganizationHandler organizationHandler;

	public OrganizationController(OrganizationRepository organizationRepository,
			OrganizationHandler organizationHandler) {
		this.organizationRepository = organizationRepository;
		this.organizationHandler = organizationHandler;
	}

	@GetMapping(path = "/{organizationId}")
	public Organization getOrganization(@PathVariable OrganizationId organizationId) {
		return this.organizationRepository.findByOrganizationId(organizationId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
					"The given organization id is not found (%s)".formatted(organizationId)));
	}

	@PostMapping(path = "")
	public ResponseEntity<Organization> postOrganizations(@RequestBody OrganizationRequest request,
			UriComponentsBuilder builder) {
		final Organization organization = this.organizationHandler.createOrganization(request);
		final URI location = builder.replacePath("/organizations/{organizationId}")
			.build(organization.organizationId().asString());
		return ResponseEntity.created(location).body(organization);
	}

}
