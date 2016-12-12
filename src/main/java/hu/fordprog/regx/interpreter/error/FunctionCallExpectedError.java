package hu.fordprog.regx.interpreter.error;

import hu.fordprog.regx.interpreter.CodePosition;

public class FunctionCallExpectedError extends SemanticError {
  private final String functionIdentifier;

  public FunctionCallExpectedError(String functionIdentifier, CodePosition codePosition) {
    super(codePosition);

    this.functionIdentifier = functionIdentifier;
  }

  @Override
  public String getMessage() {
    return "Expected function call of \"" + functionIdentifier + "\" at " + getCodePosition()
        + ". Are you missing the parentheses?";
  }

  public String getFunctionIdentifier() {
    return functionIdentifier;
  }
}
