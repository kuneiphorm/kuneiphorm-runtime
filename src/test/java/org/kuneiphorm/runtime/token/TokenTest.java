package org.kuneiphorm.runtime.token;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TokenTest {

  @Test
  void accessors_returnConstructorValues() {
    var token = new Token<>("NUM", 1, 2, 3, "123");
    assertEquals("NUM", token.label());
    assertEquals(1, token.line());
    assertEquals(2, token.column());
    assertEquals(3, token.size());
    assertEquals("123", token.data());
  }

  @Test
  void implementsTokenResult() {
    assertInstanceOf(TokenResult.class, new Token<>("ID", 0, 0, 1, "x"));
  }

  @Test
  void equals_sameValues() {
    var a = new Token<>("ID", 0, 0, 1, "x");
    var b = new Token<>("ID", 0, 0, 1, "x");
    assertEquals(a, b);
  }

  @Test
  void equals_differentValues() {
    var a = new Token<>("ID", 0, 0, 1, "x");
    var b = new Token<>("NUM", 0, 0, 1, "x");
    assertNotEquals(a, b);
  }
}
