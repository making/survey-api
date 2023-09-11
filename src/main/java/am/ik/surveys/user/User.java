package am.ik.surveys.user;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record User(UserId userId, String email, @JsonIgnore String password) {
}
