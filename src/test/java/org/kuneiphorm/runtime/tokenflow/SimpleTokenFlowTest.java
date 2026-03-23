package org.kuneiphorm.runtime.tokenflow;

import static org.junit.jupiter.api.Assertions.*;

import java.io.StringReader;
import org.junit.jupiter.api.Test;
import org.kuneiphorm.runtime.charflow.CharFlow;
import org.kuneiphorm.runtime.exception.UnexpectedEndOfInputException;
import org.kuneiphorm.runtime.exception.UnexpectedTokenException;
import org.kuneiphorm.runtime.token.Eof;
import org.kuneiphorm.runtime.token.Token;
import org.kuneiphorm.runtime.token.Tokenizer;

class SimpleTokenFlowTest {

  private static final Tokenizer<String> CHAR_TOKENIZER =
      flow -> {
        int line = flow.getLine();
        int col = flow.getColumn();
        char c = (char) flow.next();
        return new Token<>(String.valueOf(c), line, col, 1, String.valueOf(c));
      };

  private static SimpleTokenFlow<String> flow(String input) {
    return new SimpleTokenFlow<>(new CharFlow(new StringReader(input)), CHAR_TOKENIZER);
  }

  private static SimpleTokenFlow<String> namedFlow(String input, String name) {
    return new SimpleTokenFlow<>(new CharFlow(new StringReader(input), name), CHAR_TOKENIZER);
  }

  // -- peek --

  @Test
  void peek_returnsFirstToken() throws Exception {
    var token = (Token<String>) flow("ab").peek();
    assertEquals("a", token.label());
  }

  @Test
  void peek_doesNotConsume() throws Exception {
    var f = flow("ab");
    f.peek();
    var token = (Token<String>) f.peek();
    assertEquals("a", token.label());
  }

  @Test
  void peek_returnsEofOnEmpty() throws Exception {
    assertInstanceOf(Eof.class, flow("").peek());
  }

  // -- hasMore --

  @Test
  void hasMore_trueWhenTokensRemain() throws Exception {
    assertTrue(flow("a").hasMore());
  }

  @Test
  void hasMore_falseOnEmpty() throws Exception {
    assertFalse(flow("").hasMore());
  }

  // -- next --

  @Test
  void next_returnsAndConsumesToken() throws Exception {
    var f = flow("ab");
    var first = (Token<String>) f.next();
    var second = (Token<String>) f.next();
    assertEquals("a", first.label());
    assertEquals("b", second.label());
  }

  @Test
  void next_throwsOnEof() {
    assertThrows(UnexpectedEndOfInputException.class, () -> flow("").next());
  }

  @Test
  void next_throwsWithSourceName() {
    var ex =
        assertThrows(UnexpectedEndOfInputException.class, () -> namedFlow("", "test.lang").next());
    assertEquals("test.lang", ex.getSource());
  }

  // -- expect --

  @Test
  void expect_returnsTokenOnMatch() throws Exception {
    var token = flow("ab").expect("a");
    assertEquals("a", token.label());
  }

  @Test
  void expect_consumesToken() throws Exception {
    var f = flow("ab");
    f.expect("a");
    var token = (Token<String>) f.peek();
    assertEquals("b", token.label());
  }

  @Test
  void expect_throwsOnMismatch() {
    assertThrows(UnexpectedTokenException.class, () -> flow("a").expect("b"));
  }

  @Test
  void expect_throwsOnEof() {
    assertThrows(UnexpectedEndOfInputException.class, () -> flow("").expect("a"));
  }

  @Test
  void expect_throwsWithSourceNameOnMismatch() {
    var ex =
        assertThrows(UnexpectedTokenException.class, () -> namedFlow("a", "test.lang").expect("b"));
    assertEquals("test.lang", ex.getSource());
  }

  // -- accept --

  @Test
  void accept_returnsTrueAndConsumesOnMatch() throws Exception {
    var f = flow("ab");
    assertTrue(f.accept("a"));
    var token = (Token<String>) f.peek();
    assertEquals("b", token.label());
  }

  @Test
  void accept_returnsFalseAndDoesNotConsumeOnMismatch() throws Exception {
    var f = flow("ab");
    assertFalse(f.accept("b"));
    var token = (Token<String>) f.peek();
    assertEquals("a", token.label());
  }

  @Test
  void accept_returnsFalseOnEof() throws Exception {
    assertFalse(flow("").accept("a"));
  }
}
