package org.kuneiphorm.runtime.token;

/**
 * Represents the result of a tokenization step: either a {@link Token} or an {@link Eof}.
 *
 * @param <L> the label type used to classify tokens
 * @author Florent Guille
 * @since 0.1.0
 */
public sealed interface TokenResult<L> permits Token, Eof {}
