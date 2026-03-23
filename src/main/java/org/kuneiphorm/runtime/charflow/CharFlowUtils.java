package org.kuneiphorm.runtime.charflow;

import java.io.IOException;
import java.util.Objects;
import java.util.function.IntPredicate;
import org.kuneiphorm.runtime.exception.UnexpectedEndOfInputException;

/**
 * Utility methods for common {@link CharFlow} scanning patterns.
 *
 * @author Florent Guille
 * @since 0.1.0
 */
public class CharFlowUtils {

  CharFlowUtils() {}

  /**
   * Advances the flow past any whitespace characters (spaces, tabs, newlines, etc.).
   *
   * <p>Stops at the first non-whitespace character or end of input.
   *
   * @param flow the character flow to advance
   * @throws IOException if an I/O error occurs
   * @throws UnexpectedEndOfInputException if the flow throws while advancing
   */
  public static void skipBlanks(CharFlow flow) throws IOException, UnexpectedEndOfInputException {
    Objects.requireNonNull(flow, "flow");
    while (flow.hasMore() && Character.isWhitespace(flow.peek())) {
      flow.next();
    }
  }

  /**
   * Advances the flow past a Java-style comment, if one is present.
   *
   * <p>Supports two comment forms:
   *
   * <ul>
   *   <li>{@code //} -- skips to the end of the current line (the newline itself is not consumed)
   *   <li>{@code /* … *}{@code /} -- skips until the closing {@code *}{@code /}, inclusive
   * </ul>
   *
   * <p><b>Note:</b> if the next character is {@code /} but is not followed by {@code /} or {@code
   * *}, the leading {@code /} is still consumed and {@code true} is returned. Only call this method
   * in contexts where a lone {@code /} cannot appear as a valid token.
   *
   * @param flow the character flow to advance
   * @return {@code true} if a comment was skipped, {@code false} if the next character was not
   *     {@code /}
   * @throws IOException if an I/O error occurs
   * @throws UnexpectedEndOfInputException if the flow throws while advancing
   */
  public static boolean skipComment(CharFlow flow)
      throws IOException, UnexpectedEndOfInputException {
    Objects.requireNonNull(flow, "flow");
    if (flow.peek() != '/') return false;
    flow.next();
    if (flow.accept('/')) {
      // Line comment: consume everything up to (but not including) the newline
      while (flow.hasMore() && flow.peek() != '\n') flow.next();
    } else if (flow.accept('*')) {
      // Block comment: consume until the closing */
      while (flow.hasMore()) {
        if (flow.next() == '*' && flow.hasMore() && flow.peek() == '/') {
          flow.next(); // consume closing '/'
          break;
        }
      }
    }
    return true;
  }

  /**
   * Advances the flow past any combination of whitespace and Java-style comments.
   *
   * <p>Equivalent to calling {@link #skipBlanks(CharFlow)} and {@link #skipComment(CharFlow)} in a
   * loop until neither makes progress. Useful as a single call to ignore all insignificant input
   * between tokens.
   *
   * @param flow the character flow to advance
   * @throws IOException if an I/O error occurs
   * @throws UnexpectedEndOfInputException if the flow throws while advancing
   */
  public static void skipTrivia(CharFlow flow) throws IOException, UnexpectedEndOfInputException {
    Objects.requireNonNull(flow, "flow");
    while (flow.hasMore()) {
      if (Character.isWhitespace((char) flow.peek())) {
        flow.next();
      } else if (flow.peek() == '/') {
        skipComment(flow);
      } else {
        break;
      }
    }
  }

  /**
   * Consumes characters from the flow while the predicate holds and returns them as a string.
   *
   * <p>Stops at the first character that does not satisfy the predicate or at end of input. If no
   * characters match, an empty string is returned.
   *
   * @param flow the character flow to read from
   * @param predicate the condition that characters must satisfy to be consumed
   * @return the accumulated characters
   * @throws IOException if an I/O error occurs
   * @throws UnexpectedEndOfInputException if the flow throws while advancing
   */
  public static String readWhile(CharFlow flow, IntPredicate predicate)
      throws IOException, UnexpectedEndOfInputException {
    Objects.requireNonNull(flow, "flow");
    Objects.requireNonNull(predicate, "predicate");
    var sb = new StringBuilder();
    while (flow.hasMore() && predicate.test(flow.peek())) {
      sb.append((char) flow.next());
    }
    return sb.toString();
  }
}
