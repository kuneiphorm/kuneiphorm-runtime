package org.kuneiphorm.runtime.tokenflow;

import java.io.IOException;
import java.util.Objects;
import org.kuneiphorm.runtime.charflow.CharFlow;
import org.kuneiphorm.runtime.exception.SyntaxException;
import org.kuneiphorm.runtime.exception.UnexpectedEndOfInputException;
import org.kuneiphorm.runtime.exception.UnexpectedTokenException;
import org.kuneiphorm.runtime.token.Eof;
import org.kuneiphorm.runtime.token.Token;
import org.kuneiphorm.runtime.token.TokenResult;
import org.kuneiphorm.runtime.token.Tokenizer;

/**
 * A single-token lookahead reader backed by a {@link CharFlow} and a {@link Tokenizer}.
 *
 * <p>The tokenizer is called only when the underlying character flow has more characters. When the
 * flow is exhausted, {@link SimpleTokenFlow} produces an {@link Eof} automatically. This design
 * relies on the contract that the tokenizer must never skip characters — every character belongs to
 * exactly one token, so {@link CharFlow#hasMore()} is a reliable signal for EOF.
 *
 * @param <L> the label type used to classify tokens
 * @author Florent Guille
 * @since 0.1.0
 */
public class SimpleTokenFlow<L> implements TokenFlow<L> {

  private final CharFlow chars;
  private final Tokenizer<L> tokenizer;
  private TokenResult<L> current;

  /**
   * Creates a new simple token flow.
   *
   * @param chars the underlying character flow
   * @param tokenizer the tokenizer used to produce tokens from the character flow
   */
  public SimpleTokenFlow(CharFlow chars, Tokenizer<L> tokenizer) {
    this.chars = Objects.requireNonNull(chars, "chars");
    this.tokenizer = Objects.requireNonNull(tokenizer, "tokenizer");
    this.current = null;
  }

  @Override
  public TokenResult<L> peek() throws IOException, SyntaxException {
    if (current == null) {
      current =
          chars.hasMore()
              ? tokenizer.nextToken(chars)
              : new Eof<>(chars.getLine(), chars.getColumn());
    }
    return current;
  }

  @Override
  public boolean hasMore() throws IOException, SyntaxException {
    return peek() instanceof Token<L>;
  }

  @Override
  public TokenResult<L> next() throws IOException, SyntaxException {
    TokenResult<L> result = peek();
    if (result instanceof Eof<L> eof) {
      throw new UnexpectedEndOfInputException(getSource(), eof.line(), eof.column());
    }
    current = null;
    return result;
  }

  @Override
  public Token<L> expect(L expected) throws IOException, SyntaxException {
    TokenResult<L> result = peek();
    if (result instanceof Eof<L> eof) {
      throw new UnexpectedEndOfInputException(getSource(), eof.line(), eof.column());
    }
    Token<L> token = (Token<L>) result;
    if (!expected.equals(token.label())) {
      throw new UnexpectedTokenException(
          getSource(), token.line(), token.column(), expected, token.label());
    }
    current = null;
    return token;
  }

  @Override
  public boolean accept(L expected) throws IOException, SyntaxException {
    TokenResult<L> result = peek();
    if (result instanceof Token<L> token && expected.equals(token.label())) {
      current = null;
      return true;
    }
    return false;
  }

  @Override
  public String getSource() {
    return chars.getName();
  }
}
