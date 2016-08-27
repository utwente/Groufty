package nl.javalon.groufty.util;

import lombok.RequiredArgsConstructor;
import nl.javalon.groufty.resource.NotAcceptableException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Detector for testing a a certain input stream is a pdf or not.
 * @author Lukas Miedema
 */
@RequiredArgsConstructor
public class PdfDetector {

	/**
	 * Array of identifiers to look for
	 */
	private static final byte[][] IDENTIFIERS = {
			"%PDF".getBytes(StandardCharsets.US_ASCII)
	};

	private final byte[] bytes;

	/**
	 * Tests if the provided input stream is a pdf.
	 * @return
	 */
	public boolean isPdf() throws IOException {
		boolean isPdf = true;

		// Process
		ID:
		for (byte[] identifier : IDENTIFIERS) {
			for (int i = 0; i < identifier.length; i++) {
				if (identifier[i] != bytes[i]) {
					isPdf = false;
					break ID;
				}
			}
		}
		return isPdf;
	}

	/**
	 * Calls {@link #isPdf()} and throws a {@link NotAcceptableException} if the outcome is false.
	 * @throws IOException
	 * @throws NotAcceptableException
	 */
	public void validate() throws IOException, NotAcceptableException {
		if (!isPdf())
			throw new NotAcceptableException();
	}
}
