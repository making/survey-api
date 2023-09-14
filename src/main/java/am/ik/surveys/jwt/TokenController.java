package am.ik.surveys.jwt;

import java.time.Clock;
import java.time.Instant;
import java.util.stream.Collectors;

import am.ik.surveys.security.SurveyUserDetails;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class TokenController {

	private final JwtEncoder encoder;

	private final Clock clock;

	public TokenController(JwtEncoder encoder, Clock clock) {
		this.encoder = encoder;
		this.clock = clock;
	}

	@PostMapping("/token")
	public String token(Authentication authentication) {
		final Instant now = Instant.now(this.clock);
		final long expiry = 3600L;
		final String scope = authentication.getAuthorities()
			.stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(" "));
		final JwtClaimsSet claims = JwtClaimsSet.builder()
			.issuer(ServletUriComponentsBuilder.fromCurrentRequest().build().toString())
			.issuedAt(now)
			.expiresAt(now.plusSeconds(expiry))
			.subject(authentication.getName())
			.claim("scope", scope)
			.claim("email", ((SurveyUserDetails) authentication.getPrincipal()).getUserDetail().user().email())
			.build();
		return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}

}
