package am.ik.surveys.organization.web;

import java.util.Optional;
import java.util.Set;

import am.ik.surveys.organization.Organization;
import am.ik.surveys.organization.OrganizationId;
import am.ik.surveys.organization.OrganizationRepository;
import am.ik.surveys.organization.OrganizationUser;
import am.ik.surveys.tsid.TsidGenerator;
import am.ik.surveys.user.RoleRepository;
import am.ik.surveys.user.User;
import am.ik.surveys.user.UserId;
import am.ik.surveys.user.UserRepository;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
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
	public Organization createOrganization(OrganizationRequest request) {
		final Optional<User> userOptional = this.userRepository.findByEmail(request.adminEmail());
		User user;
		if (StringUtils.hasText(request.adminPassword())) {
			if (userOptional.isPresent()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"The given email is already registered. Please don't include admin password in the request to skip account creation.");
			}
			final UserId userId = new UserId(this.tsidGenerator.generate());
			// CREATE USER
			user = new User(userId, request.adminEmail(), "{noop}%s".formatted(request.adminPassword()));
			this.userRepository.insert(user);
		}
		else {
			user = userOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"The given email is not found. Please include admin password in the request to create the account."));
		}
		final OrganizationId organizationId = new OrganizationId(this.tsidGenerator.generate());
		final OrganizationUser organizationUser = new OrganizationUser(user.userId(),
				this.roleRepository.admin().roleId());
		final Organization organization = new Organization(organizationId, request.organizationName(),
				Set.of(organizationUser));
		try {
			this.organizationRepository.save(organization);
		}
		catch (DuplicateKeyException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The given organization is already taken.", e);
		}
		return organization;
	}

}