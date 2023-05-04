package am.ik.surveys.tsid;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
public class TsidHolderJsonSerializer extends JsonSerializer<TsidHolder> {

	@Override
	public void serialize(TsidHolder tsidHolder, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException {
		jsonGenerator.writeString(tsidHolder.asString());
	}

}
