package am.ik.surveys.user.web;

import am.ik.surveys.user.User;
import am.ik.surveys.user.UserId;

public record UserRequest(String email, String password) {

	public User toUser(UserId userId) {
		return new User(userId, email, "{noop}%s".formatted(password));
	}
}
