package org.kuneiphorm.runtime.parser;

import java.io.IOException;
import org.kuneiphorm.runtime.exception.SyntaxException;
import org.kuneiphorm.runtime.tokenflow.TokenFlow;

/**
 * Parses a stream of tokens into a result value.
 *
 * <p>Symmetric with {@link org.kuneiphorm.runtime.token.Tokenizer}: where a tokenizer converts
 * characters into tokens, a parser converts tokens into a structured result. The interface is
 * algorithm-agnostic — each parser algorithm module provides its own implementation.
 *
 * @param <L> the token label type
 * @param <R> the result type produced by the parser
 * @author Florent Guille
 * @since 0.1.0
 */
@FunctionalInterface
public interface Parser<L, R> {

  /**
   * Parses the token stream and returns the result.
   *
   * @param tokens the token flow to parse
   * @return the parse result
   * @throws IOException if an I/O error occurs while reading tokens
   * @throws SyntaxException if the input does not conform to the grammar
   */
  R parse(TokenFlow<L> tokens) throws IOException, SyntaxException;
}
