package org.kuneiphorm.runtime.tokenflow;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import org.kuneiphorm.runtime.exception.SyntaxException;
import org.kuneiphorm.runtime.exception.UnexpectedEndOfInputException;
import org.kuneiphorm.runtime.exception.UnexpectedTokenException;
import org.kuneiphorm.runtime.token.Eof;
import org.kuneiphorm.runtime.token.Token;
import org.kuneiphorm.runtime.token.TokenResult;

/**
 * A {@link TokenFlow} wrapper that allows previously consumed tokens to be pushed back onto the
 * stream.
 *
 * <p>Pushed-back tokens are returned in LIFO order before the delegate flow is consulted. This is
 * useful for parsers that need multi-token lookahead or speculative parsing without modifying the
 * underlying token source.
 *
 * @param <L> the label type used to classify tokens
 * @author Florent Guille
 * @since 0.1.0
 */
public class PushbackTokenFlow<L> implements TokenFlow<L> {

  private final TokenFlow<L> delegate;
  private final Deque<TokenResult<L>> stack;

  /**
   * Creates a new pushback token flow wrapping the given delegate.
   *
   * @param delegate the underlying token flow
   */
  public PushbackTokenFlow(TokenFlow<L> delegate) {
    this.delegate = Objects.requireNonNull(delegate, "delegate");
    this.stack = new ArrayDeque<>();
  }

  /**
   * Pushes a token result back onto the stream. It will be the next result returned by {@link
   * #peek()}.
   *
   * @param token the token result to push back
   */
  public void pushBack(TokenResult<L> token) {
    stack.push(Objects.requireNonNull(token, "token"));
  }

  @Override
  public TokenResult<L> peek() throws IOException, SyntaxException {
    return stack.isEmpty() ? delegate.peek() : stack.peek();
  }

  @Override
  public boolean hasMore() throws IOException, SyntaxException {
    return peek() instanceof Token<L>;
  }

  @Override
  public TokenResult<L> next() throws IOException, SyntaxException {
    if (!stack.isEmpty()) {
      TokenResult<L> result = stack.pop();
      if (result instanceof Eof<L> eof) {
        throw new UnexpectedEndOfInputException(getSource(), eof.line(), eof.column());
      }
      return result;
    }
    return delegate.next();
  }

  @Override
  public Token<L> expect(L expected) throws IOException, SyntaxException {
    if (!stack.isEmpty()) {
      TokenResult<L> result = stack.pop();
      if (result instanceof Eof<L> eof) {
        throw new UnexpectedEndOfInputException(getSource(), eof.line(), eof.column());
      }
      Token<L> token = (Token<L>) result;
      if (!expected.equals(token.label())) {
        throw new UnexpectedTokenException(
            getSource(), token.line(), token.column(), expected, token.label());
      }
      return token;
    }
    return delegate.expect(expected);
  }

  @Override
  public boolean accept(L expected) throws IOException, SyntaxException {
    if (!stack.isEmpty()) {
      TokenResult<L> result = stack.peek();
      if (result instanceof Token<L> token && expected.equals(token.label())) {
        stack.pop();
        return true;
      }
      return false;
    }
    return delegate.accept(expected);
  }

  @Override
  public String getSource() {
    return delegate.getSource();
  }
}
