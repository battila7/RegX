package hu.fordprog.regx.interpreter.error;

import hu.fordprog.regx.interpreter.CodePosition;

public class MissingReturnInFunctionError extends SemanticError {
  private final String identifier;

  public MissingReturnInFunctionError(String identifier,
                                      CodePosition codePosition) {
    super(codePosition);

    this.identifier = identifier;
  }

  @Override
  public String getMessage() {
    return "Missing return statement in non-void function \"" + identifier + "\", declared at "
        + getCodePosition();
  }

  public String getIdentifier() {
    return identifier;
  }
}
