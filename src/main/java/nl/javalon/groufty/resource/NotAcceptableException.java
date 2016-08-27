package nl.javalon.groufty.resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown for instance when uploading a non-pdf file to a pdf endpoint.
 * @author Lukas Miedema
 */
@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class NotAcceptableException extends RuntimeException {

	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 * The cause is not initialized, and may subsequently be initialized by a
	 * call to {@link #initCause}.
	 */
	public NotAcceptableException() {
		int i = 0;
	}

	/**
	 * Constructs a new exception with the specified detail message.  The
	 * cause is not initialized, and may subsequently be initialized by
	 * a call to {@link #initCause}.
	 *
	 * @param message the detail message. The detail message is saved for
	 *                later retrieval by the {@link #getMessage()} method.
	 */
	public NotAcceptableException(String message) {
		super(message);
	}
}
