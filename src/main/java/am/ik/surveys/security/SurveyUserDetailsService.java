package am.ik.surveys.security;

import am.ik.surveys.organization.OrganizationRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class SurveyUserDetailsService implements UserDetailsService {

	private final OrganizationRepository organizationRepository;

	public SurveyUserDetailsService(OrganizationRepository organizationRepository) {
		this.organizationRepository = organizationRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return this.organizationRepository.findDetailByEmail(username)
			.map(SurveyUserDetails::new)
			.orElseThrow(() -> new UsernameNotFoundException(username + " is not found."));
	}

}
