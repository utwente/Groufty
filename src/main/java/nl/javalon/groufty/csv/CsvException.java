package nl.javalon.groufty.csv;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Lukas Miedema
 */
@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class CsvException extends RuntimeException {

	public CsvException() {
	}

	public CsvException(String s) {
		super(s);
	}

	public CsvException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public CsvException(Throwable throwable) {
		super(throwable);
	}

	public CsvException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
}
