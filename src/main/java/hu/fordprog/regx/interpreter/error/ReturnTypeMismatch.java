package hu.fordprog.regx.interpreter.error;

import hu.fordprog.regx.interpreter.CodePosition;

public class ReturnTypeMismatch extends SemanticError {
  public ReturnTypeMismatch(CodePosition codePosition) {
    super(codePosition);
  }

  @Override
  public String getMessage() {
    return null;
  }
}
