package org.kuneiphorm.runtime.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UnexpectedEndOfInputExceptionTest {

  @Test
  void getSize_isZero() {
    var ex = new UnexpectedEndOfInputException(1, 2);
    assertEquals(0, ex.getSize());
  }

  @Test
  void getMessage_formatsPosition() {
    var ex = new UnexpectedEndOfInputException(3, 7);
    assertEquals("At line 3, column 7 (size: 0), unexpected end of input", ex.getMessage());
  }

  @Test
  void getMessage_includesSourceWhenPresent() {
    var ex = new UnexpectedEndOfInputException("test.lang", 3, 7);
    assertEquals("test.lang: line 3, column 7 (size: 0), unexpected end of input", ex.getMessage());
  }

  @Test
  void extendsSyntaxException() {
    assertInstanceOf(SyntaxException.class, new UnexpectedEndOfInputException(0, 0));
  }
}
