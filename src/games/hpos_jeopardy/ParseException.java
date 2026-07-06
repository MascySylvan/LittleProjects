package games.hpos_jeopardy;

/**
 * Exception thrown when a Question File cannot be parsed due to invalid format.
 */
public class ParseException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a ParseException with the given message.
	 *
	 * @param message the detail message describing the parse failure
	 */
	public ParseException(String message) {
		super(message);
	}

	/**
	 * Constructs a ParseException with the given message and cause.
	 *
	 * @param message the detail message describing the parse failure
	 * @param cause   the underlying cause of the parse failure
	 */
	public ParseException(String message, Throwable cause) {
		super(message, cause);
	}

}
