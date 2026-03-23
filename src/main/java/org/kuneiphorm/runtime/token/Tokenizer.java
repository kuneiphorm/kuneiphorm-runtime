package org.kuneiphorm.runtime.token;

import java.io.IOException;
import org.kuneiphorm.runtime.charflow.CharFlow;
import org.kuneiphorm.runtime.exception.SyntaxException;

/**
 * Produces the next {@link Token} from a {@link CharFlow}.
 *
 * <p>A tokenizer must consume at least one character on every call and must never skip characters:
 * every character in the flow must belong to exactly one token. {@link
 * org.kuneiphorm.runtime.tokenflow.SimpleTokenFlow} guarantees this contract by only calling the
 * tokenizer when {@link CharFlow#hasMore()} is {@code true}.
 *
 * @param <L> the label type used to classify tokens
 * @author Florent Guille
 * @since 0.1.0
 */
@FunctionalInterface
public interface Tokenizer<L> {

  /**
   * Reads the next token from {@code flow}.
   *
   * @param flow the character flow to read from; guaranteed to have at least one character
   * @return the next token
   * @throws IOException if an I/O error occurs
   * @throws SyntaxException if the input does not form a valid token
   */
  Token<L> nextToken(CharFlow flow) throws IOException, SyntaxException;
}
