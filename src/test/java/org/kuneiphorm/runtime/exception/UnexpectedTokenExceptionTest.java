package org.kuneiphorm.runtime.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UnexpectedTokenExceptionTest {

  @Test
  void getExpected_returnsExpectedLabel() {
    var ex = new UnexpectedTokenException(1, 2, "NUM", "ID");
    assertEquals("NUM", ex.getExpected());
  }

  @Test
  void getFound_returnsFoundLabel() {
    var ex = new UnexpectedTokenException(1, 2, "NUM", "ID");
    assertEquals("ID", ex.getFound());
  }

  @Test
  void getSize_isOne() {
    var ex = new UnexpectedTokenException(1, 2, "NUM", "ID");
    assertEquals(1, ex.getSize());
  }

  @Test
  void getMessage_formatsExpectedAndFound() {
    var ex = new UnexpectedTokenException(0, 5, "NUM", "ID");
    assertEquals(
        "At line 0, column 5 (size: 1), expected token 'NUM' but found 'ID'", ex.getMessage());
  }

  @Test
  void getMessage_includesSourceWhenPresent() {
    var ex = new UnexpectedTokenException("test.lang", 0, 5, "NUM", "ID");
    assertEquals(
        "test.lang: line 0, column 5 (size: 1), expected token 'NUM' but found 'ID'",
        ex.getMessage());
  }

  @Test
  void extendsSyntaxException() {
    assertInstanceOf(SyntaxException.class, new UnexpectedTokenException(0, 0, "A", "B"));
  }
}
