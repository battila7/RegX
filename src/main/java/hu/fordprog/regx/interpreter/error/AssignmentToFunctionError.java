package hu.fordprog.regx.interpreter.error;

import hu.fordprog.regx.interpreter.CodePosition;

public class AssignmentToFunctionError extends SemanticError {
  public AssignmentToFunctionError(CodePosition codePosition) {
    super(codePosition);
  }

  @Override
  public String getMessage() {
    return "Assignment to function is not possible at " + getCodePosition();
  }
}
