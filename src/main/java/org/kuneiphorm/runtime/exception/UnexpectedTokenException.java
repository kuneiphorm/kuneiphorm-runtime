package org.kuneiphorm.runtime.exception;

/**
 * Thrown when a specific token label was expected but a different one was found.
 *
 * @author Florent Guille
 * @since 0.1.0
 */
public class UnexpectedTokenException extends SyntaxException {

  /** The token label that was expected. */
  private final Object expected;

  /** The token label that was actually found. */
  private final Object found;

  /**
   * Creates a new unexpected token exception without a source name.
   *
   * @param line the line at which the error occurred (0-based)
   * @param column the column at which the error occurred (0-based)
   * @param expected the token label that was expected
   * @param found the token label that was actually found
   */
  public UnexpectedTokenException(int line, int column, Object expected, Object found) {
    this(null, line, column, expected, found);
  }

  /**
   * Creates a new unexpected token exception with a source name.
   *
   * @param source the source name, or {@code null} if unknown
   * @param line the line at which the error occurred (0-based)
   * @param column the column at which the error occurred (0-based)
   * @param expected the token label that was expected
   * @param found the token label that was actually found
   */
  public UnexpectedTokenException(
      String source, int line, int column, Object expected, Object found) {
    super(source, line, column, 1);
    this.expected = expected;
    this.found = found;
  }

  /**
   * Returns the token label that was expected.
   *
   * @return the expected label
   */
  public Object getExpected() {
    return expected;
  }

  /**
   * Returns the token label that was actually found.
   *
   * @return the found label
   */
  public Object getFound() {
    return found;
  }

  @Override
  protected String getSyntaxMessage() {
    return String.format("expected token '%s' but found '%s'", expected, found);
  }
}
