package hu.fordprog.regx.interpreter.error;

import hu.fordprog.regx.interpreter.CodePosition;
import hu.fordprog.regx.interpreter.symbol.Type;

public class InvalidForLoopExpression extends SemanticError {
  private final Type actual;

  public InvalidForLoopExpression(CodePosition codePosition,
                                  Type actual) {
    super(codePosition);
    this.actual = actual;
  }

  @Override
  public String getMessage() {
    return "Invalid for loop collection expression at " + getCodePosition()
        + ". Expected expression of type LIST got " + actual;
  }
}
