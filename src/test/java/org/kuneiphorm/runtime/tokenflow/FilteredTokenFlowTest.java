package org.kuneiphorm.runtime.tokenflow;

import static org.junit.jupiter.api.Assertions.*;

import java.io.StringReader;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.kuneiphorm.runtime.charflow.CharFlow;
import org.kuneiphorm.runtime.exception.UnexpectedEndOfInputException;
import org.kuneiphorm.runtime.exception.UnexpectedTokenException;
import org.kuneiphorm.runtime.token.Token;
import org.kuneiphorm.runtime.token.Tokenizer;

class FilteredTokenFlowTest {

  private static final Tokenizer<String> LABELING_TOKENIZER =
      flow -> {
        int line = flow.getLine();
        int col = flow.getColumn();
        char c = (char) flow.next();
        String label = Character.isWhitespace(c) ? "WS" : String.valueOf(c);
        return new Token<>(label, line, col, 1, String.valueOf(c));
      };

  private static FilteredTokenFlow<String> flow(String input, String... skipped) {
    var labels = Set.of(skipped);
    return new FilteredTokenFlow<>(
        new SimpleTokenFlow<>(new CharFlow(new StringReader(input)), LABELING_TOKENIZER),
        token -> labels.contains(token.label()));
  }

  private static FilteredTokenFlow<String> namedFlow(String input, String name, String... skipped) {
    var labels = Set.of(skipped);
    return new FilteredTokenFlow<>(
        new SimpleTokenFlow<>(new CharFlow(new StringReader(input), name), LABELING_TOKENIZER),
        token -> labels.contains(token.label()));
  }

  // -- getSource --

  @Test
  void getSource_delegatesToDelegate() {
    assertEquals("test.lang", namedFlow("a", "test.lang").getSource());
  }

  @Test
  void getSource_returnsNullWhenDelegateHasNoSource() {
    assertNull(flow("a").getSource());
  }

  // -- peek --

  @Test
  void peek_skipsFilteredTokens() throws Exception {
    var token = (Token<String>) flow("  a", "WS").peek();
    assertEquals("a", token.label());
  }

  @Test
  void peek_returnsEofWhenAllFiltered() throws Exception {
    assertFalse(flow("   ", "WS").hasMore());
  }

  @Test
  void peek_returnsFirstTokenWhenNoneFiltered() throws Exception {
    var token = (Token<String>) flow("ab").peek();
    assertEquals("a", token.label());
  }

  // -- hasMore --

  @Test
  void hasMore_trueWhenNonFilteredTokensRemain() throws Exception {
    assertTrue(flow("  a", "WS").hasMore());
  }

  @Test
  void hasMore_falseWhenAllFiltered() throws Exception {
    assertFalse(flow("   ", "WS").hasMore());
  }

  // -- next --

  @Test
  void next_skipsFilteredAndReturnsNext() throws Exception {
    var f = flow(" a b ", "WS");
    var first = (Token<String>) f.next();
    var second = (Token<String>) f.next();
    assertEquals("a", first.label());
    assertEquals("b", second.label());
  }

  @Test
  void next_throwsOnEofAfterFiltering() {
    assertThrows(UnexpectedEndOfInputException.class, () -> flow("   ", "WS").next());
  }

  @Test
  void next_throwsWithSourceName() {
    var ex =
        assertThrows(
            UnexpectedEndOfInputException.class, () -> namedFlow("   ", "test.lang", "WS").next());
    assertEquals("test.lang", ex.getSource());
  }

  // -- expect --

  @Test
  void expect_skipsFilteredAndMatchesNext() throws Exception {
    var token = flow("  a", "WS").expect("a");
    assertEquals("a", token.label());
  }

  @Test
  void expect_throwsOnMismatchAfterFiltering() {
    assertThrows(UnexpectedTokenException.class, () -> flow("  a", "WS").expect("b"));
  }

  @Test
  void expect_throwsOnEofAfterFiltering() {
    assertThrows(UnexpectedEndOfInputException.class, () -> flow("   ", "WS").expect("a"));
  }

  @Test
  void expect_throwsWithSourceNameOnMismatch() {
    var ex =
        assertThrows(UnexpectedTokenException.class, () -> namedFlow("a", "test.lang").expect("b"));
    assertEquals("test.lang", ex.getSource());
  }

  // -- accept --

  @Test
  void accept_skipsFilteredAndAcceptsMatch() throws Exception {
    var f = flow("  a", "WS");
    assertTrue(f.accept("a"));
  }

  @Test
  void accept_skipsFilteredAndRejectsMismatch() throws Exception {
    var f = flow("  a", "WS");
    assertFalse(f.accept("b"));
    assertEquals("a", ((Token<String>) f.peek()).label());
  }

  @Test
  void accept_returnsFalseOnEofAfterFiltering() throws Exception {
    assertFalse(flow("   ", "WS").accept("a"));
  }
}
