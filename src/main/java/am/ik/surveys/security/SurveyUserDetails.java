package am.ik.surveys.security;

import java.util.Collection;
import java.util.stream.Collectors;

import am.ik.surveys.organization.OrganizationUserDetail;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class SurveyUserDetails implements UserDetails {

	private final OrganizationUserDetail userDetail;

	private final Collection<? extends GrantedAuthority> authorities;

	public SurveyUserDetails(OrganizationUserDetail userDetail) {
		this.userDetail = userDetail;
		this.authorities = userDetail.permissions()
			.entrySet()
			.stream()
			.flatMap(
					e -> e.getValue().stream().map(p -> e.getKey().organizationId().asString() + "|" + p.toAuthority()))
			.map(SimpleGrantedAuthority::new)
			.collect(Collectors.toUnmodifiableSet());
	}

	public OrganizationUserDetail getUserDetail() {
		return userDetail;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getPassword() {
		return this.userDetail.user().password();
	}

	@Override
	public String getUsername() {
		return this.userDetail.user().userId().asString();
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
