package pl.amerevent.amer.model;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class RoleDeserializer extends JsonDeserializer<ERole> {

	@Override
	public ERole deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		String value = p.getText().toUpperCase();
		return ERole.valueOf(value);
	}
}
