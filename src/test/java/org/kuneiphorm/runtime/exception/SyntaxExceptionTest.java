package org.kuneiphorm.runtime.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SyntaxExceptionTest {

  private static class TestSyntaxException extends SyntaxException {
    private final String syntaxMessage;

    TestSyntaxException(int line, int column, int size, String syntaxMessage) {
      super(line, column, size);
      this.syntaxMessage = syntaxMessage;
    }

    TestSyntaxException(String source, int line, int column, int size, String syntaxMessage) {
      super(source, line, column, size);
      this.syntaxMessage = syntaxMessage;
    }

    @Override
    protected String getSyntaxMessage() {
      return syntaxMessage;
    }
  }

  @Test
  void getLine_returnsLine() {
    assertEquals(3, new TestSyntaxException(3, 5, 2, "error").getLine());
  }

  @Test
  void getColumn_returnsColumn() {
    assertEquals(5, new TestSyntaxException(3, 5, 2, "error").getColumn());
  }

  @Test
  void getSize_returnsSize() {
    assertEquals(2, new TestSyntaxException(3, 5, 2, "error").getSize());
  }

  @Test
  void getSource_returnsNullByDefault() {
    assertNull(new TestSyntaxException(1, 1, 1, "error").getSource());
  }

  @Test
  void getSource_returnsSourceName() {
    assertEquals("foo.lang", new TestSyntaxException("foo.lang", 1, 1, 1, "error").getSource());
  }

  @Test
  void getMessage_formatsPositionAndMessage() {
    var ex = new TestSyntaxException(1, 4, 3, "unexpected token");
    assertEquals("At line 1, column 4 (size: 3), unexpected token", ex.getMessage());
  }

  @Test
  void getMessage_handlesZeroValues() {
    var ex = new TestSyntaxException(0, 0, 0, "empty input");
    assertEquals("At line 0, column 0 (size: 0), empty input", ex.getMessage());
  }

  @Test
  void getMessage_includesSourceWhenPresent() {
    var ex = new TestSyntaxException("test.lang", 1, 4, 3, "unexpected token");
    assertEquals("test.lang: line 1, column 4 (size: 3), unexpected token", ex.getMessage());
  }

  @Test
  void extendsException() {
    assertInstanceOf(Exception.class, new TestSyntaxException(1, 1, 1, "test"));
  }
}
