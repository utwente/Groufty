package nl.javalon.groufty.resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Triggers a NOT FOUND (404) response when the requested resource could not be found by the REST endpoint
 * or simply when an invalid identifier was supplied.
 * @author Melcher
 *
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException(String message) {
		super(message);
	}
}
