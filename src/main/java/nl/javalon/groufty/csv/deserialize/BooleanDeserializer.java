package nl.javalon.groufty.csv.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Accepts any casing of {@link Boolean} values, unlike Jacksons default which wants lowercase. Many CSV editors
 * write booleans in uppercase.
 * @author Lukas Miedema
 */
public class BooleanDeserializer extends JsonDeserializer<Boolean> {

	@Override
	public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		return Boolean.valueOf(p.getValueAsString().toLowerCase());
	}
}
