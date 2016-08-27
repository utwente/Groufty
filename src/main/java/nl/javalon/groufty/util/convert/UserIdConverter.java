package nl.javalon.groufty.util.convert;

import nl.javalon.groufty.domain.user.UserId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts Strings to {@link nl.javalon.groufty.domain.user.UserId}
 * @author Lukas Miedema
 */
@Component
public class UserIdConverter implements Converter<String, UserId> {

	@Override
	public UserId convert(String source) {
		return UserId.fromString(source);
	}
}
