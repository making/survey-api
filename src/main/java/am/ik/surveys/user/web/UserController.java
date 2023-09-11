package am.ik.surveys.user.web;

import am.ik.surveys.user.User;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/users")
public class UserController {

	private final UserHandler userHandler;

	public UserController(UserHandler userHandler) {
		this.userHandler = userHandler;
	}

	@PostMapping(path = "")
	@ResponseStatus(HttpStatus.CREATED)
	public User postUsers(@RequestBody UserRequest userRequest) {
		return this.userHandler.createUser(userRequest);
	}

}
