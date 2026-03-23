package org.kuneiphorm.runtime.token;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EofTest {

  @Test
  void accessors_returnConstructorValues() {
    var eof = new Eof<String>(3, 7);
    assertEquals(3, eof.line());
    assertEquals(7, eof.column());
  }

  @Test
  void implementsTokenResult() {
    assertInstanceOf(TokenResult.class, new Eof<String>(0, 0));
  }

  @Test
  void equals_sameValues() {
    var a = new Eof<String>(1, 2);
    var b = new Eof<String>(1, 2);
    assertEquals(a, b);
  }

  @Test
  void equals_differentValues() {
    var a = new Eof<String>(1, 2);
    var b = new Eof<String>(3, 4);
    assertNotEquals(a, b);
  }
}
