package hu.fordprog.regx.interpreter.error;

import hu.fordprog.regx.interpreter.CodePosition;

public class InvalidMainFunctionSignatureError extends SemanticError {
  public InvalidMainFunctionSignatureError(CodePosition codePosition) {
    super(codePosition);
  }

  @Override
  public String getMessage() {
    return "Invalid \"main\" function signature at " + getCodePosition()
        + ". The \"main\" function must be void and must not take any arguments!";
  }
}
