package hu.fordprog.regx.interpreter.error;

import hu.fordprog.regx.interpreter.CodePosition;

public abstract class SemanticError {
  private final CodePosition codePosition;

  SemanticError(CodePosition codePosition) {
    this.codePosition = codePosition;
  }

  public CodePosition getCodePosition() {
    return codePosition;
  }

  public abstract String getMessage();

  @Override
  public String toString() {
    return getMessage();
  }
}
