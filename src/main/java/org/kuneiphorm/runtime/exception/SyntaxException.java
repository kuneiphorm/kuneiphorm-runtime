package org.kuneiphorm.runtime.exception;

/**
 * Base class for syntax errors encountered during parsing.
 *
 * <p>Each syntax exception records the source name (if available), position (line, column) and
 * extent (size) of the erroneous input. Subclasses provide the human-readable message via {@link
 * #getSyntaxMessage()}.
 *
 * @author Florent Guille
 * @since 0.0.0
 */
public abstract class SyntaxException extends Exception {

  /** The source name, or {@code null} if unknown. */
  private final String source;

  /** The line at which the error occurred (0-based). */
  private final int line;

  /** The column at which the error occurred (0-based). */
  private final int column;

  /** The number of characters covered by the error. */
  private final int size;

  /**
   * Creates a new syntax exception without a source name.
   *
   * @param line the line at which the error occurred (0-based)
   * @param column the column at which the error occurred (0-based)
   * @param size the number of characters covered by the error
   */
  protected SyntaxException(int line, int column, int size) {
    this(null, line, column, size);
  }

  /**
   * Creates a new syntax exception with a source name.
   *
   * @param source the source name (file path, URI, etc.), or {@code null} if unknown
   * @param line the line at which the error occurred (0-based)
   * @param column the column at which the error occurred (0-based)
   * @param size the number of characters covered by the error
   */
  protected SyntaxException(String source, int line, int column, int size) {
    this.source = source;
    this.line = line;
    this.column = column;
    this.size = size;
  }

  /**
   * Returns the human-readable description of this syntax error, without the position prefix.
   *
   * @return the syntax error message
   */
  protected abstract String getSyntaxMessage();

  /**
   * Returns the source name, or {@code null} if unknown.
   *
   * @return the source name
   */
  public String getSource() {
    return source;
  }

  /**
   * Returns the line at which the error occurred (0-based).
   *
   * @return the error line
   */
  public int getLine() {
    return line;
  }

  /**
   * Returns the column at which the error occurred (0-based).
   *
   * @return the error column
   */
  public int getColumn() {
    return column;
  }

  /**
   * Returns the number of characters covered by the error.
   *
   * @return the error extent in characters
   */
  public int getSize() {
    return size;
  }

  @Override
  public String getMessage() {
    if (source != null) {
      return String.format(
          "%s: line %s, column %s (size: %s), %s", source, line, column, size, getSyntaxMessage());
    }
    return String.format(
        "At line %s, column %s (size: %s), %s", line, column, size, getSyntaxMessage());
  }
}
