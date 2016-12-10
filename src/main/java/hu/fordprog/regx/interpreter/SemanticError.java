package hu.fordprog.regx.interpreter;

public abstract class SemanticError {
  private final CodePosition codePosition;

  public SemanticError(CodePosition codePosition) {
    this.codePosition = codePosition;
  }

  public CodePosition getCodePosition() {
    return codePosition;
  }

  public abstract String getMessage();
}
