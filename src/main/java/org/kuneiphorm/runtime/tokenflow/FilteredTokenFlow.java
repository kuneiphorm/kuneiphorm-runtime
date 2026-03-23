package org.kuneiphorm.runtime.tokenflow;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Predicate;
import org.kuneiphorm.runtime.exception.SyntaxException;
import org.kuneiphorm.runtime.token.Token;
import org.kuneiphorm.runtime.token.TokenResult;

/**
 * A {@link TokenFlow} wrapper that silently skips tokens matching a predicate.
 *
 * <p>This is useful for filtering out insignificant tokens (whitespace, comments) so that the
 * parser only sees meaningful tokens. Filtered tokens are consumed from the delegate and discarded.
 *
 * @param <L> the label type used to classify tokens
 * @author Florent Guille
 * @since 0.1.0
 */
public class FilteredTokenFlow<L> implements TokenFlow<L> {

  private final TokenFlow<L> delegate;
  private final Predicate<Token<L>> skipped;

  /**
   * Creates a new filtered token flow.
   *
   * @param delegate the underlying token flow
   * @param skipped a predicate that returns {@code true} for tokens to skip
   */
  public FilteredTokenFlow(TokenFlow<L> delegate, Predicate<Token<L>> skipped) {
    this.delegate = Objects.requireNonNull(delegate, "delegate");
    this.skipped = Objects.requireNonNull(skipped, "skipped");
  }

  private void skipFiltered() throws IOException, SyntaxException {
    TokenResult<L> result = delegate.peek();
    while (result instanceof Token<L> token && skipped.test(token)) {
      delegate.next();
      result = delegate.peek();
    }
  }

  @Override
  public TokenResult<L> peek() throws IOException, SyntaxException {
    skipFiltered();
    return delegate.peek();
  }

  @Override
  public boolean hasMore() throws IOException, SyntaxException {
    return peek() instanceof Token<L>;
  }

  @Override
  public TokenResult<L> next() throws IOException, SyntaxException {
    skipFiltered();
    return delegate.next();
  }

  @Override
  public Token<L> expect(L expected) throws IOException, SyntaxException {
    skipFiltered();
    return delegate.expect(expected);
  }

  @Override
  public boolean accept(L expected) throws IOException, SyntaxException {
    skipFiltered();
    return delegate.accept(expected);
  }

  @Override
  public String getSource() {
    return delegate.getSource();
  }
}
