package am.ik.surveys.organization.web;

import java.util.Set;

import am.ik.surveys.organization.Organization;
import am.ik.surveys.organization.OrganizationId;
import am.ik.surveys.organization.OrganizationRepository;
import am.ik.surveys.organization.OrganizationUser;
import am.ik.surveys.role.Role;
import am.ik.surveys.role.RoleId;
import am.ik.surveys.role.RoleRepository;
import am.ik.surveys.role.SystemRoleName;
import am.ik.surveys.tsid.TsidGenerator;
import am.ik.surveys.user.User;
import am.ik.surveys.user.UserId;
import am.ik.surveys.user.UserRepository;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Component
public class OrganizationHandler {

	private final OrganizationRepository organizationRepository;

	private final RoleRepository roleRepository;

	private final UserRepository userRepository;

	private final TsidGenerator tsidGenerator;

	public OrganizationHandler(OrganizationRepository organizationRepository, RoleRepository roleRepository,
			UserRepository userRepository, TsidGenerator tsidGenerator) {
		this.organizationRepository = organizationRepository;
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
		this.tsidGenerator = tsidGenerator;
	}

	@Transactional
	public Organization createOrganization(String organizationName, UserId userId) {
		final OrganizationId organizationId = new OrganizationId(this.tsidGenerator.generate());
		final OrganizationUser organizationUser = new OrganizationUser(userId,
				this.roleRepository.getByRoleName(SystemRoleName.ADMIN).roleId());
		final Organization organization = new Organization(organizationId, organizationName, Set.of(organizationUser));
		try {
			this.organizationRepository.save(organization);
		}
		catch (DuplicateKeyException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The given organization name is already taken.",
					e);
		}
		return organization;
	}

	private interface OrganizationUpdater {

		Organization update(Organization organization, UserId userId, RoleId roleId);

	}

	private Organization mutate(OrganizationId organizationId, OrganizationUserRequest request,
			OrganizationUpdater updater) {
		final Role role = this.roleRepository.getByRoleName(request.roleName());
		if (role == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role name: " + request.roleName());
		}
		final User user = this.userRepository.findByEmail(request.email())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"The requested user is not found: " + request.roleName()));
		final Organization organization = this.organizationRepository.findByOrganizationId(organizationId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"The requested organizationId is not found: " + organizationId));
		final Organization updated = updater.update(organization, user.userId(), role.roleId());
		this.organizationRepository.save(updated);
		return updated;
	}

	@Transactional
	public Organization bindUser(OrganizationId organizationId, OrganizationUserRequest request) {
		return this.mutate(organizationId, request, Organization::bind);
	}

	@Transactional
	public Organization unbindUser(OrganizationId organizationId, OrganizationUserRequest request) {
		return this.mutate(organizationId, request, Organization::unbind);
	}

}
