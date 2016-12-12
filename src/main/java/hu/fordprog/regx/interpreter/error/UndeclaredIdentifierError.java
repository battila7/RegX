package hu.fordprog.regx.interpreter.error;

import hu.fordprog.regx.interpreter.CodePosition;

public class UndeclaredIdentifierError extends SemanticError {
  private final String identifier;

  public UndeclaredIdentifierError(String identifier, CodePosition codePosition) {
    super(codePosition);

    this.identifier = identifier;
  }

  @Override
  public String getMessage() {
    return "Identifier \"" + identifier + "\" is not declared at " + getCodePosition();
  }

  public String getIdentifier() {
    return identifier;
  }
}
