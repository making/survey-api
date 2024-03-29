package am.ik.surveys.user.web;

import am.ik.surveys.tsid.TsidGenerator;
import am.ik.surveys.user.User;
import am.ik.surveys.user.UserId;
import am.ik.surveys.user.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Component
public class UserHandler {

	private final UserRepository userRepository;

	private final TsidGenerator tsidGenerator;

	private final PasswordEncoder passwordEncoder;

	public UserHandler(UserRepository userRepository, TsidGenerator tsidGenerator, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.tsidGenerator = tsidGenerator;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public User createUser(UserRequest userRequest) {
		final UserId userId = new UserId(this.tsidGenerator.generate());
		if (this.userRepository.findByEmail(userRequest.email()).isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The given email is already used.");
		}
		final User user = userRequest.toUser(userId, this.passwordEncoder);
		this.userRepository.insert(user);
		return user;
	}

}
