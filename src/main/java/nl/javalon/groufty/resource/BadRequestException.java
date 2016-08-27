package nl.javalon.groufty.resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Triggers a BAD REQUEST (400) response in case of malformed requests. 
 * Note that often malformed requests are already caught by Spring itself.
 * @author Melcher
 *
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

	public BadRequestException(String string) {
		super(string);
	}

	private static final long serialVersionUID = 1L;

}
