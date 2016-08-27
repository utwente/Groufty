package nl.javalon.groufty.util.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.sql.Clob;

/**
 * Convert {@link Clob} to Strings. Some database implementations use CLOBs instead of Strings (H2) and some do not
 * (Postgres). This makes the same code work for both.
 * @author Lukas Miedema
 */
@Component
public class ClobConverter implements Converter<Clob, String> {

	@Override
	public String convert(Clob source) {
		try {
			return source.getSubString(1, (int) source.length());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
