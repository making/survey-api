package am.ik.surveys.security;

import java.util.Collection;
import java.util.Set;

import am.ik.surveys.organization.OrganizationUserDetail;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class SurveyUserDetails implements UserDetails {

	private final OrganizationUserDetail userDetail;

	public SurveyUserDetails(OrganizationUserDetail userDetail) {
		this.userDetail = userDetail;
	}

	public OrganizationUserDetail getUserDetail() {
		return userDetail;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Set.of();
	}

	@Override
	public String getPassword() {
		return this.userDetail.user().password();
	}

	@Override
	public String getUsername() {
		return this.userDetail.user().email();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
