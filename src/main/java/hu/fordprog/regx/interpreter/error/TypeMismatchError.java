package hu.fordprog.regx.interpreter.error;

import hu.fordprog.regx.interpreter.CodePosition;
import hu.fordprog.regx.interpreter.symbol.Type;

public class TypeMismatchError extends SemanticError {
  private final Type expected;

  private final Type actual;

  public TypeMismatchError(Type expected, Type actual, CodePosition codePosition) {
    super(codePosition);

    this.expected = expected;

    this.actual = actual;
  }

  @Override
  public String getMessage() {
    return "Type mismatch at " + getCodePosition()
        + ". Expected " + expected + " but got " + actual;
  }

  public Type getExpected() {
    return expected;
  }

  public Type getActual() {
    return actual;
  }
}
