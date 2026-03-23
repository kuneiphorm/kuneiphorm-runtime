package org.kuneiphorm.runtime.charflow;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.Test;
import org.kuneiphorm.runtime.exception.UnexpectedCharException;
import org.kuneiphorm.runtime.exception.UnexpectedEndOfInputException;

class CharFlowTest {

  private static CharFlow flow(String input) {
    return new CharFlow(new StringReader(input));
  }

  // -- name --

  @Test
  void getName_returnsNullByDefault() {
    assertNull(flow("abc").getName());
  }

  @Test
  void getName_returnsProvidedName() {
    var f = new CharFlow(new StringReader("abc"), "test.lang");
    assertEquals("test.lang", f.getName());
  }

  @Test
  void name_includedInExceptionOnEof() {
    var f = new CharFlow(new StringReader(""), "test.lang");
    var ex = assertThrows(UnexpectedEndOfInputException.class, () -> f.next());
    assertEquals("test.lang", ex.getSource());
  }

  @Test
  void name_includedInExceptionOnMismatch() {
    var f = new CharFlow(new StringReader("b"), "test.lang");
    var ex = assertThrows(UnexpectedCharException.class, () -> f.expect('a'));
    assertEquals("test.lang", ex.getSource());
  }

  // -- peek --

  @Test
  void peek_returnsFirstChar() throws IOException {
    assertEquals('a', flow("abc").peek());
  }

  @Test
  void peek_doesNotConsume() throws IOException {
    var f = flow("abc");
    f.peek();
    assertEquals('a', f.peek());
  }

  @Test
  void peek_returnsMinusOneOnEmpty() throws IOException {
    assertEquals(-1, flow("").peek());
  }

  // -- hasMore --

  @Test
  void hasMore_trueWhenCharsRemain() throws IOException {
    assertTrue(flow("a").hasMore());
  }

  @Test
  void hasMore_falseOnEmpty() throws IOException {
    assertFalse(flow("").hasMore());
  }

  // -- expect --

  @Test
  void expect_returnsAndConsumesChar() throws Exception {
    var f = flow("ab");
    assertEquals('a', f.expect('a'));
    assertEquals('b', f.peek());
  }

  @Test
  void expect_incrementsColumn() throws Exception {
    var f = flow("ab");
    f.expect('a');
    assertEquals(1, f.getColumn());
  }

  @Test
  void expect_incrementsLineOnNewline() throws Exception {
    var f = flow("\nb");
    f.expect('\n');
    assertEquals(1, f.getLine());
    assertEquals(0, f.getColumn());
  }

  @Test
  void expect_throwsOnEof() {
    assertThrows(UnexpectedEndOfInputException.class, () -> flow("").expect('a'));
  }

  @Test
  void expect_throwsOnMismatch() {
    assertThrows(UnexpectedCharException.class, () -> flow("b").expect('a'));
  }

  // -- next --

  @Test
  void next_returnsChar() throws Exception {
    assertEquals('a', flow("a").next());
  }

  @Test
  void next_consumesChar() throws Exception {
    var f = flow("ab");
    f.next();
    assertEquals('b', f.peek());
  }

  @Test
  void next_incrementsColumn() throws Exception {
    var f = flow("ab");
    f.next();
    assertEquals(1, f.getColumn());
  }

  @Test
  void next_incrementsLineOnNewline() throws Exception {
    var f = flow("\nb");
    f.next();
    assertEquals(1, f.getLine());
    assertEquals(0, f.getColumn());
  }

  @Test
  void next_multipleCharsTrackPosition() throws Exception {
    var f = flow("ab\ncd");
    f.next(); // a -- col 1
    f.next(); // b -- col 2
    f.next(); // \n -- line 1, col 0
    f.next(); // c -- col 1
    assertEquals(1, f.getLine());
    assertEquals(1, f.getColumn());
  }

  @Test
  void next_throwsOnEof() {
    assertThrows(UnexpectedEndOfInputException.class, () -> flow("").next());
  }

  // -- accept --

  @Test
  void accept_returnsTrueAndConsumesOnMatch() throws IOException {
    var f = flow("ab");
    assertTrue(f.accept('a'));
    assertEquals('b', f.peek());
  }

  @Test
  void accept_returnsFalseAndDoesNotConsumeOnMismatch() throws IOException {
    var f = flow("ab");
    assertFalse(f.accept('b'));
    assertEquals('a', f.peek());
  }

  @Test
  void accept_returnsFalseOnEmpty() throws IOException {
    assertFalse(flow("").accept('a'));
  }
}
