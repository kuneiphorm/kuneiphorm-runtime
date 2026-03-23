package org.kuneiphorm.runtime.token;

/**
 * Represents a recognized token produced by a {@link Tokenizer}.
 *
 * @param <L> the label type used to classify tokens
 * @param label the classification of this token
 * @param line the line at which the token starts (0-based)
 * @param column the column at which the token starts (0-based)
 * @param size the number of characters consumed
 * @param data the raw text of the token
 * @author Florent Guille
 * @since 0.1.0
 */
public record Token<L>(L label, int line, int column, int size, String data)
    implements TokenResult<L> {}
