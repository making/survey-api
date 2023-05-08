package am.ik.surveys.json;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonView;

public interface IncludeCreatedAt {

	@JsonView(IncludeCreatedAt.class)
	Instant createdAt();

}
