package org.kuneiphorm.runtime.tokenflow;

import java.io.IOException;
import org.kuneiphorm.runtime.exception.SyntaxException;
import org.kuneiphorm.runtime.exception.UnexpectedEndOfInputException;
import org.kuneiphorm.runtime.exception.UnexpectedTokenException;
import org.kuneiphorm.runtime.token.Token;
import org.kuneiphorm.runtime.token.TokenResult;

/**
 * A read-only view of a token stream with single-token lookahead.
 *
 * <p>Implementations produce {@link TokenResult} instances — either a {@link Token} or an {@link
 * org.kuneiphorm.runtime.token.Eof} — and allow consumers to peek ahead without consuming.
 *
 * @param <L> the label type used to classify tokens
 * @author Florent Guille
 * @since 0.0.0
 */
public interface TokenFlow<L> {

  /**
   * Returns the next token result without consuming it. Subsequent calls return the same result
   * until {@link #next()} or {@link #expect(Object)} advances the flow.
   *
   * @return the next token result
   * @throws IOException if an I/O error occurs
   * @throws SyntaxException if the tokenizer encounters a syntax error
   */
  TokenResult<L> peek() throws IOException, SyntaxException;

  /**
   * Returns {@code true} if the next result is a {@link Token} (not {@link
   * org.kuneiphorm.runtime.token.Eof}).
   *
   * @return whether there are more tokens
   * @throws IOException if an I/O error occurs
   * @throws SyntaxException if the tokenizer encounters a syntax error
   */
  boolean hasMore() throws IOException, SyntaxException;

  /**
   * Consumes and returns the next token result.
   *
   * @return the consumed token
   * @throws IOException if an I/O error occurs
   * @throws SyntaxException if the tokenizer encounters a syntax error
   * @throws UnexpectedEndOfInputException if the flow is at EOF
   */
  TokenResult<L> next() throws IOException, SyntaxException;

  /**
   * Consumes and returns the next token, asserting that its label matches {@code expected}.
   *
   * @param expected the expected token label
   * @return the consumed token
   * @throws IOException if an I/O error occurs
   * @throws SyntaxException if the tokenizer encounters a syntax error
   * @throws UnexpectedEndOfInputException if the flow is at EOF
   * @throws UnexpectedTokenException if the token label does not match
   */
  Token<L> expect(L expected) throws IOException, SyntaxException;

  /**
   * Consumes the next token if its label matches {@code expected}.
   *
   * @param expected the token label to match
   * @return {@code true} if the token was consumed, {@code false} otherwise (including at EOF)
   * @throws IOException if an I/O error occurs
   * @throws SyntaxException if the tokenizer encounters a syntax error
   */
  boolean accept(L expected) throws IOException, SyntaxException;

  /**
   * Returns the source name associated with this token flow, or {@code null} if unknown.
   *
   * @return the source name
   */
  default String getSource() {
    return null;
  }
}
