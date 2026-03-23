package org.kuneiphorm.runtime.token;

/**
 * Represents the end of the character stream, produced by {@link
 * org.kuneiphorm.runtime.tokenflow.SimpleTokenFlow} when the underlying {@link
 * org.kuneiphorm.runtime.charflow.CharFlow} is exhausted.
 *
 * @param <L> the label type used to classify tokens
 * @param line the line at which EOF was reached (0-based)
 * @param column the column at which EOF was reached (0-based)
 * @author Florent Guille
 * @since 0.0.0
 */
public record Eof<L>(int line, int column) implements TokenResult<L> {}
