package nl.javalon.groufty.resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Triggers an FORBIDDEN (403) response when the authenticated user has no permission to the requested resource.
 * @author Lukas Miedema
 *
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedException extends RuntimeException {
}
