package org.kuneiphorm.runtime.tokenflow;

import static org.junit.jupiter.api.Assertions.*;

import java.io.StringReader;
import org.junit.jupiter.api.Test;
import org.kuneiphorm.runtime.charflow.CharFlow;
import org.kuneiphorm.runtime.exception.UnexpectedEndOfInputException;
import org.kuneiphorm.runtime.exception.UnexpectedTokenException;
import org.kuneiphorm.runtime.token.Eof;
import org.kuneiphorm.runtime.token.Token;
import org.kuneiphorm.runtime.token.TokenResult;
import org.kuneiphorm.runtime.token.Tokenizer;

class PushbackTokenFlowTest {

  private static final Tokenizer<String> CHAR_TOKENIZER =
      flow -> {
        int line = flow.getLine();
        int col = flow.getColumn();
        char c = (char) flow.next();
        return new Token<>(String.valueOf(c), line, col, 1, String.valueOf(c));
      };

  private static PushbackTokenFlow<String> flow(String input) {
    return new PushbackTokenFlow<>(
        new SimpleTokenFlow<>(new CharFlow(new StringReader(input)), CHAR_TOKENIZER));
  }

  private static PushbackTokenFlow<String> namedFlow(String input, String name) {
    return new PushbackTokenFlow<>(
        new SimpleTokenFlow<>(new CharFlow(new StringReader(input), name), CHAR_TOKENIZER));
  }

  private static Token<String> token(String label) {
    return new Token<>(label, 0, 0, 1, label);
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

  @Test
  void getSource_defaultReturnsNull() {
    TokenFlow<String> minimal =
        new TokenFlow<>() {
          @Override
          public TokenResult<String> peek() {
            return null;
          }

          @Override
          public boolean hasMore() {
            return false;
          }

          @Override
          public TokenResult<String> next() {
            return null;
          }

          @Override
          public Token<String> expect(String expected) {
            return null;
          }

          @Override
          public boolean accept(String expected) {
            return false;
          }
        };
    assertNull(minimal.getSource());
  }

  // -- peek --

  @Test
  void peek_delegatesWhenStackEmpty() throws Exception {
    var token = (Token<String>) flow("a").peek();
    assertEquals("a", token.label());
  }

  @Test
  void peek_returnsEofOnEmpty() throws Exception {
    assertInstanceOf(Eof.class, flow("").peek());
  }

  @Test
  void peek_returnsPushedBackToken() throws Exception {
    var f = flow("a");
    f.pushBack(token("x"));
    var token = (Token<String>) f.peek();
    assertEquals("x", token.label());
  }

  // -- hasMore --

  @Test
  void hasMore_trueWhenDelegateHasTokens() throws Exception {
    assertTrue(flow("a").hasMore());
  }

  @Test
  void hasMore_falseOnEmpty() throws Exception {
    assertFalse(flow("").hasMore());
  }

  @Test
  void hasMore_trueWhenStackHasToken() throws Exception {
    var f = flow("");
    f.pushBack(token("x"));
    assertTrue(f.hasMore());
  }

  // -- pushBack --

  @Test
  void pushBack_lifoOrder() throws Exception {
    var f = flow("");
    f.pushBack(token("a"));
    f.pushBack(token("b"));
    assertEquals("b", ((Token<String>) f.next()).label());
    assertEquals("a", ((Token<String>) f.next()).label());
  }

  @Test
  void pushBack_thenFallsThroughToDelegate() throws Exception {
    var f = flow("b");
    f.pushBack(token("a"));
    assertEquals("a", ((Token<String>) f.next()).label());
    assertEquals("b", ((Token<String>) f.next()).label());
  }

  // -- next --

  @Test
  void next_consumesFromStack() throws Exception {
    var f = flow("b");
    f.pushBack(token("a"));
    var result = (Token<String>) f.next();
    assertEquals("a", result.label());
    assertEquals("b", ((Token<String>) f.peek()).label());
  }

  @Test
  void next_consumesFromDelegate() throws Exception {
    var f = flow("ab");
    var result = (Token<String>) f.next();
    assertEquals("a", result.label());
  }

  @Test
  void next_throwsOnEof() {
    assertThrows(UnexpectedEndOfInputException.class, () -> flow("").next());
  }

  @Test
  void next_throwsOnPushedBackEof() {
    var f = flow("");
    f.pushBack(new Eof<>(5, 10));
    assertThrows(UnexpectedEndOfInputException.class, () -> f.next());
  }

  @Test
  void next_throwsWithSourceNameOnPushedBackEof() {
    var f = namedFlow("", "test.lang");
    f.pushBack(new Eof<>(5, 10));
    var ex = assertThrows(UnexpectedEndOfInputException.class, () -> f.next());
    assertEquals("test.lang", ex.getSource());
  }

  @Test
  void next_throwsWithSourceNameFromDelegate() {
    var ex =
        assertThrows(UnexpectedEndOfInputException.class, () -> namedFlow("", "test.lang").next());
    assertEquals("test.lang", ex.getSource());
  }

  // -- expect --

  @Test
  void expect_consumesFromStackOnMatch() throws Exception {
    var f = flow("b");
    f.pushBack(token("a"));
    var result = f.expect("a");
    assertEquals("a", result.label());
    assertEquals("b", ((Token<String>) f.peek()).label());
  }

  @Test
  void expect_consumesFromDelegateOnMatch() throws Exception {
    var f = flow("ab");
    var result = f.expect("a");
    assertEquals("a", result.label());
  }

  @Test
  void expect_throwsOnPushedBackEof() {
    var f = flow("");
    f.pushBack(new Eof<>(5, 10));
    assertThrows(UnexpectedEndOfInputException.class, () -> f.expect("a"));
  }

  @Test
  void expect_throwsOnMismatchFromStack() {
    var f = flow("");
    f.pushBack(token("a"));
    assertThrows(UnexpectedTokenException.class, () -> f.expect("b"));
  }

  @Test
  void expect_throwsOnMismatchFromDelegate() {
    assertThrows(UnexpectedTokenException.class, () -> flow("a").expect("b"));
  }

  @Test
  void expect_throwsOnEof() {
    assertThrows(UnexpectedEndOfInputException.class, () -> flow("").expect("a"));
  }

  @Test
  void expect_throwsWithSourceNameOnMismatchFromStack() {
    var f = namedFlow("", "test.lang");
    f.pushBack(token("a"));
    var ex = assertThrows(UnexpectedTokenException.class, () -> f.expect("b"));
    assertEquals("test.lang", ex.getSource());
  }

  // -- accept --

  @Test
  void accept_consumesFromStackOnMatch() throws Exception {
    var f = flow("b");
    f.pushBack(token("a"));
    assertTrue(f.accept("a"));
    assertEquals("b", ((Token<String>) f.peek()).label());
  }

  @Test
  void accept_consumesFromDelegateOnMatch() throws Exception {
    var f = flow("ab");
    assertTrue(f.accept("a"));
    assertEquals("b", ((Token<String>) f.peek()).label());
  }

  @Test
  void accept_returnsFalseFromStackOnMismatch() throws Exception {
    var f = flow("b");
    f.pushBack(token("a"));
    assertFalse(f.accept("b"));
    assertEquals("a", ((Token<String>) f.peek()).label());
  }

  @Test
  void accept_returnsFalseFromDelegateOnMismatch() throws Exception {
    var f = flow("a");
    assertFalse(f.accept("b"));
    assertEquals("a", ((Token<String>) f.peek()).label());
  }

  @Test
  void accept_returnsFalseOnEof() throws Exception {
    assertFalse(flow("").accept("a"));
  }

  @Test
  void accept_returnsFalseOnPushedBackEof() throws Exception {
    var f = flow("");
    f.pushBack(new Eof<>(0, 0));
    assertFalse(f.accept("a"));
  }
}
