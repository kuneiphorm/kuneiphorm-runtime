package org.kuneiphorm.runtime.charflow;

import static org.junit.jupiter.api.Assertions.*;

import java.io.StringReader;
import org.junit.jupiter.api.Test;

class CharFlowUtilsTest {

  @Test
  void constructor_isPackagePrivate() {
    assertNotNull(new CharFlowUtils());
  }

  private static CharFlow flow(String input) {
    return new CharFlow(new StringReader(input));
  }

  // -- skipBlanks --

  @Test
  void skipBlanks_skipsWhitespace() throws Exception {
    var f = flow("  \t\nabc");
    CharFlowUtils.skipBlanks(f);
    assertEquals('a', f.peek());
  }

  @Test
  void skipBlanks_stopsAtNonWhitespace() throws Exception {
    var f = flow("abc");
    CharFlowUtils.skipBlanks(f);
    assertEquals('a', f.peek());
  }

  @Test
  void skipBlanks_noopOnEmpty() throws Exception {
    var f = flow("");
    CharFlowUtils.skipBlanks(f);
    assertFalse(f.hasMore());
  }

  // -- skipComment --

  @Test
  void skipComment_skipsLineComment() throws Exception {
    var f = flow("// hello\nabc");
    assertTrue(CharFlowUtils.skipComment(f));
    assertEquals('\n', f.peek());
  }

  @Test
  void skipComment_skipsBlockComment() throws Exception {
    var f = flow("/* hello */abc");
    assertTrue(CharFlowUtils.skipComment(f));
    assertEquals('a', f.peek());
  }

  @Test
  void skipComment_returnsFalseWhenNoSlash() throws Exception {
    var f = flow("abc");
    assertFalse(CharFlowUtils.skipComment(f));
    assertEquals('a', f.peek());
  }

  @Test
  void skipComment_consumesLoneSlash() throws Exception {
    var f = flow("/abc");
    assertTrue(CharFlowUtils.skipComment(f));
    assertEquals('a', f.peek());
  }

  @Test
  void skipComment_lineCommentEndsAtEof() throws Exception {
    var f = flow("// no newline");
    assertTrue(CharFlowUtils.skipComment(f));
    assertFalse(f.hasMore());
  }

  @Test
  void skipComment_blockCommentStarNotFollowedBySlash() throws Exception {
    var f = flow("/* a * b */c");
    assertTrue(CharFlowUtils.skipComment(f));
    assertEquals('c', f.peek());
  }

  @Test
  void skipComment_blockCommentStarAtEof() throws Exception {
    var f = flow("/* unclosed *");
    assertTrue(CharFlowUtils.skipComment(f));
    assertFalse(f.hasMore());
  }

  // -- skipTrivia --

  @Test
  void skipTrivia_skipsBlanksAndComments() throws Exception {
    var f = flow("  // comment\n  /* block */  abc");
    CharFlowUtils.skipTrivia(f);
    assertEquals('a', f.peek());
  }

  @Test
  void skipTrivia_stopsAtNonTrivia() throws Exception {
    var f = flow("abc");
    CharFlowUtils.skipTrivia(f);
    assertEquals('a', f.peek());
  }

  @Test
  void skipTrivia_noopOnEmpty() throws Exception {
    var f = flow("");
    CharFlowUtils.skipTrivia(f);
    assertFalse(f.hasMore());
  }

  // -- readWhile --

  @Test
  void readWhile_consumesMatchingChars() throws Exception {
    var f = flow("abc123");
    var result = CharFlowUtils.readWhile(f, Character::isLetter);
    assertEquals("abc", result);
    assertEquals('1', f.peek());
  }

  @Test
  void readWhile_returnsEmptyWhenNoMatch() throws Exception {
    var f = flow("123abc");
    var result = CharFlowUtils.readWhile(f, Character::isLetter);
    assertEquals("", result);
    assertEquals('1', f.peek());
  }

  @Test
  void readWhile_consumesAllOnFullMatch() throws Exception {
    var f = flow("abcdef");
    var result = CharFlowUtils.readWhile(f, Character::isLetter);
    assertEquals("abcdef", result);
    assertFalse(f.hasMore());
  }

  @Test
  void readWhile_returnsEmptyOnEmptyInput() throws Exception {
    var f = flow("");
    var result = CharFlowUtils.readWhile(f, Character::isLetter);
    assertEquals("", result);
  }

  @Test
  void readWhile_readsDigits() throws Exception {
    var f = flow("42abc");
    var result = CharFlowUtils.readWhile(f, Character::isDigit);
    assertEquals("42", result);
    assertEquals('a', f.peek());
  }
}
