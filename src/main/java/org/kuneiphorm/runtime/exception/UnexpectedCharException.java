package org.kuneiphorm.runtime.exception;

/**
 * Thrown when a specific character was expected but a different one was found.
 *
 * @author Florent Guille
 * @since 0.0.0
 */
public class UnexpectedCharException extends SyntaxException {

  /** The character that was expected. */
  private final char expected;

  /** The character that was actually found. */
  private final char found;

  /**
   * Creates a new unexpected character exception without a source name.
   *
   * @param line the line at which the error occurred (0-based)
   * @param column the column at which the error occurred (0-based)
   * @param expected the character that was expected
   * @param found the character that was actually found
   */
  public UnexpectedCharException(int line, int column, char expected, char found) {
    this(null, line, column, expected, found);
  }

  /**
   * Creates a new unexpected character exception with a source name.
   *
   * @param source the source name, or {@code null} if unknown
   * @param line the line at which the error occurred (0-based)
   * @param column the column at which the error occurred (0-based)
   * @param expected the character that was expected
   * @param found the character that was actually found
   */
  public UnexpectedCharException(String source, int line, int column, char expected, char found) {
    super(source, line, column, 1);
    this.expected = expected;
    this.found = found;
  }

  /**
   * Returns the character that was expected.
   *
   * @return the expected character
   */
  public char getExpected() {
    return expected;
  }

  /**
   * Returns the character that was actually found.
   *
   * @return the found character
   */
  public char getFound() {
    return found;
  }

  @Override
  protected String getSyntaxMessage() {
    return String.format("expected '%c' but found '%c'", expected, found);
  }
}
