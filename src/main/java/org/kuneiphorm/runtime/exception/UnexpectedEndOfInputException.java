package org.kuneiphorm.runtime.exception;

/**
 * Thrown when more input was expected but the end of the stream was reached.
 *
 * @author Florent Guille
 * @since 0.0.0
 */
public class UnexpectedEndOfInputException extends SyntaxException {

  /**
   * Creates a new unexpected end-of-input exception without a source name.
   *
   * @param line the line at which EOF was reached (0-based)
   * @param column the column at which EOF was reached (0-based)
   */
  public UnexpectedEndOfInputException(int line, int column) {
    this(null, line, column);
  }

  /**
   * Creates a new unexpected end-of-input exception with a source name.
   *
   * @param source the source name, or {@code null} if unknown
   * @param line the line at which EOF was reached (0-based)
   * @param column the column at which EOF was reached (0-based)
   */
  public UnexpectedEndOfInputException(String source, int line, int column) {
    super(source, line, column, 0);
  }

  @Override
  protected String getSyntaxMessage() {
    return "unexpected end of input";
  }
}
