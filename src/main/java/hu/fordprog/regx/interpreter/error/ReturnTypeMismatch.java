package hu.fordprog.regx.interpreter.error;

import hu.fordprog.regx.interpreter.CodePosition;
import hu.fordprog.regx.interpreter.symbol.Type;

public class ReturnTypeMismatch extends TypeMismatchError {
  private final String identifier;

  public ReturnTypeMismatch(String identifier,
                            Type expected, Type actual,
                            CodePosition codePosition) {
    super(expected, actual, codePosition);

    this.identifier = identifier;
  }

  @Override
  public String getMessage() {
    return "Type mismatch when returning from non-void function \""
        + identifier + "\" at " + getCodePosition()
        + ". Expected " + getExpected() + " but got " + getActual();
  }
}
