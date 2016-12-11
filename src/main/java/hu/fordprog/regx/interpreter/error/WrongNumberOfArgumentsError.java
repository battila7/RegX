package hu.fordprog.regx.interpreter.error;

import hu.fordprog.regx.interpreter.CodePosition;

public class WrongNumberOfArgumentsError extends SemanticError {
  private final int expected;

  private final int actual;

  public WrongNumberOfArgumentsError(int expected, int actual,
                                     CodePosition codePosition) {
    super(codePosition);

    this.expected = expected;

    this.actual = actual;
  }

  @Override
  public String getMessage() {
    return "Wrong number of arguments at " + getCodePosition()
        + ". Expected " + expected + " but got " + actual;
  }

  public int getExpected() {
    return expected;
  }

  public int getActual() {
    return actual;
  }
}
