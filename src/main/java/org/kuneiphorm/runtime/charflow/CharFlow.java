package org.kuneiphorm.runtime.charflow;

import java.io.IOException;
import java.io.Reader;
import java.util.Objects;
import org.kuneiphorm.runtime.exception.UnexpectedCharException;
import org.kuneiphorm.runtime.exception.UnexpectedEndOfInputException;

/**
 * A single-character lookahead reader that tracks line and column position.
 *
 * <p>Characters are read lazily from the underlying {@link Reader}: the first call to {@link
 * #peek()} triggers the initial read. Line and column positions are 0-based and updated after each
 * character is consumed.
 *
 * <p>An optional source name (file path, URI, etc.) can be provided for inclusion in error
 * messages.
 *
 * @author Florent Guille
 * @since 0.1.0
 */
public class CharFlow {

  private final Reader reader;
  private final String name;
  private int current;
  private int line, column;

  /**
   * Creates a new character flow reading from the given reader, with no source name.
   *
   * @param reader the underlying character source
   */
  public CharFlow(Reader reader) {
    this(reader, null);
  }

  /**
   * Creates a new character flow reading from the given reader, with a source name.
   *
   * @param reader the underlying character source
   * @param name the source name (file path, URI, etc.), or {@code null} if unknown
   */
  public CharFlow(Reader reader, String name) {
    this.reader = Objects.requireNonNull(reader, "reader");
    this.name = name;
    this.current = -2;
    this.line = 0;
    this.column = 0;
  }

  /**
   * Returns the next character without consuming it.
   *
   * <p>Returns {@code -1} at end of stream. Subsequent calls return the same value until the
   * character is consumed by {@link #next()}, {@link #expect(int)}, or {@link #accept(int)}.
   *
   * @return the next character, or {@code -1} at end of stream
   * @throws IOException if an I/O error occurs
   */
  public int peek() throws IOException {
    if (current == -2) {
      current = reader.read();
    }
    return current;
  }

  private int step() {
    int c = current;
    current = -2;
    if (c == '\n') {
      line++;
      column = 0;
    } else {
      column++;
    }
    return c;
  }

  /**
   * Returns {@code true} if the stream has more characters.
   *
   * @return whether there are more characters to read
   * @throws IOException if an I/O error occurs
   */
  public boolean hasMore() throws IOException {
    return peek() != -1;
  }

  /**
   * Consumes the next character, asserting that it matches {@code target}.
   *
   * @param target the expected character
   * @return the consumed character (always equal to {@code target})
   * @throws IOException if an I/O error occurs
   * @throws UnexpectedEndOfInputException if the stream is exhausted
   * @throws UnexpectedCharException if the next character does not match {@code target}
   */
  public int expect(int target)
      throws IOException, UnexpectedEndOfInputException, UnexpectedCharException {
    if (!hasMore()) throw new UnexpectedEndOfInputException(name, line, column);
    if (peek() != target)
      throw new UnexpectedCharException(name, line, column, (char) target, (char) peek());
    return step();
  }

  /**
   * Consumes and returns the next character.
   *
   * @return the consumed character
   * @throws IOException if an I/O error occurs
   * @throws UnexpectedEndOfInputException if the stream is exhausted
   */
  public int next() throws IOException, UnexpectedEndOfInputException {
    if (!hasMore()) throw new UnexpectedEndOfInputException(name, line, column);
    return step();
  }

  /**
   * Consumes the next character if it matches {@code target}.
   *
   * @param target the character to match
   * @return {@code true} if the character was consumed, {@code false} otherwise
   * @throws IOException if an I/O error occurs
   */
  public boolean accept(int target) throws IOException {
    if (peek() == target) {
      step();
      return true;
    }
    return false;
  }

  /**
   * Returns the source name, or {@code null} if no name was provided.
   *
   * @return the source name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the current line position (0-based).
   *
   * @return the current line
   */
  public int getLine() {
    return line;
  }

  /**
   * Returns the current column position (0-based).
   *
   * @return the current column
   */
  public int getColumn() {
    return column;
  }
}
