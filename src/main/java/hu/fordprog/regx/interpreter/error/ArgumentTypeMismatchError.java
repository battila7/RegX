package hu.fordprog.regx.interpreter.error;

import hu.fordprog.regx.interpreter.CodePosition;
import hu.fordprog.regx.interpreter.symbol.Type;

public class ArgumentTypeMismatchError extends TypeMismatchError {
  private final String identifier;

  private final int number;

  public ArgumentTypeMismatchError(String identifier, int number,
                                   Type expected, Type actual,
                                   CodePosition codePosition) {
    super(expected, actual, codePosition);

    this.identifier = identifier;

    this.number = number;
  }

  @Override
  public String getMessage() {
    return "Argument type mismatch in argument number " + number + " when calling function \""
        + identifier + "\" at " + getCodePosition()
        + ". Expected " + getExpected() + " but got " + getActual();
  }

  public String getIdentifier() {
    return identifier;
  }

  public int getNumber() {
    return number;
  }
}
