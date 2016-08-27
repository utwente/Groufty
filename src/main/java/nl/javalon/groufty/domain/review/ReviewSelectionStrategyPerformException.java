package nl.javalon.groufty.domain.review;

/**
 * Exception that can occur during {@link ReviewerSelectionStrategy#performSelection()}
 * Only the {@link Exception#Exception(String)} constructor is supported. The message will be stored in the
 * {@link ReviewerSelectionStrategy#errorMessage} field.
 * @author Lukas Miedema
 */
public class ReviewSelectionStrategyPerformException extends Exception {

	/**
	 * Constructs a new exception with the specified detail message.  The
	 * cause is not initialized, and may subsequently be initialized by
	 * a call to {@link #initCause}.
	 *
	 * The message will be stored in the {@link ReviewerSelectionStrategy#errorMessage} field and the
	 * {@link ReviewerSelectionStrategy} will be disabled.
	 *
	 * @param message the detail message. The detail message is saved for
	 *                later retrieval by the {@link #getMessage()} method.
	 */
	public ReviewSelectionStrategyPerformException(String message) {
		super(message);
	}
}
