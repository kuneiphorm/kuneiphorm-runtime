package org.kuneiphorm.runtime.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UnexpectedCharExceptionTest {

  @Test
  void getExpected_returnsExpectedChar() {
    var ex = new UnexpectedCharException(1, 2, 'a', 'b');
    assertEquals('a', ex.getExpected());
  }

  @Test
  void getFound_returnsFoundChar() {
    var ex = new UnexpectedCharException(1, 2, 'a', 'b');
    assertEquals('b', ex.getFound());
  }

  @Test
  void getSize_isOne() {
    var ex = new UnexpectedCharException(1, 2, 'a', 'b');
    assertEquals(1, ex.getSize());
  }

  @Test
  void getMessage_formatsExpectedAndFound() {
    var ex = new UnexpectedCharException(0, 3, 'x', 'y');
    assertEquals("At line 0, column 3 (size: 1), expected 'x' but found 'y'", ex.getMessage());
  }

  @Test
  void getMessage_includesSourceWhenPresent() {
    var ex = new UnexpectedCharException("test.lang", 0, 3, 'x', 'y');
    assertEquals(
        "test.lang: line 0, column 3 (size: 1), expected 'x' but found 'y'", ex.getMessage());
  }

  @Test
  void extendsSyntaxException() {
    assertInstanceOf(SyntaxException.class, new UnexpectedCharException(0, 0, 'a', 'b'));
  }
}
