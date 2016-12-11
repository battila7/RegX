package hu.fordprog.regx.interpreter.error;

import hu.fordprog.regx.interpreter.CodePosition;

public class MissingMainFunctionError extends SemanticError {
  public MissingMainFunctionError(CodePosition codePosition) {
    super(codePosition);
  }

  @Override
  public String getMessage() {
    return "Function named \"main\" is missing from program.";
  }
}
