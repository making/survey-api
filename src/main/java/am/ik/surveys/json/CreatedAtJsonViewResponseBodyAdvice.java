package am.ik.surveys.json;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMappingJacksonResponseBodyAdvice;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@ControllerAdvice
public class CreatedAtJsonViewResponseBodyAdvice extends AbstractMappingJacksonResponseBodyAdvice {

	@Override
	protected void beforeBodyWriteInternal(MappingJacksonValue bodyContainer, MediaType contentType,
			MethodParameter returnType, ServerHttpRequest request, ServerHttpResponse response) {
		final UriComponents uri = UriComponentsBuilder.fromUri(request.getURI()).build();
		final String includeCreatedAt = uri.getQueryParams().getFirst("include_created_at");
		if (Boolean.parseBoolean(includeCreatedAt)) {
			bodyContainer.setSerializationView(IncludeCreatedAt.class);
		}
		else {
			bodyContainer.setSerializationView(DefaultView.class);
		}
	}

}
