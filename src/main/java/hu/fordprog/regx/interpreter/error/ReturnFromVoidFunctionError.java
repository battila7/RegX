package hu.fordprog.regx.interpreter.error;

import hu.fordprog.regx.interpreter.CodePosition;

public class ReturnFromVoidFunctionError extends SemanticError {
  private static final String MESSAGE = "Return statement in void function not allowed at ";

  public ReturnFromVoidFunctionError(CodePosition codePosition) {
    super(codePosition);
  }

  @Override
  public String getMessage() {
    return MESSAGE + getCodePosition();
  }
}
