package am.ik.surveys.user.web;

import am.ik.surveys.user.User;
import am.ik.surveys.user.UserId;

import org.springframework.security.crypto.password.PasswordEncoder;

public record UserRequest(String email, String password) {

	public User toUser(UserId userId, PasswordEncoder passwordEncoder) {
		return new User(userId, email, passwordEncoder.encode(password));
	}
}
