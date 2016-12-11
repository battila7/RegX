package hu.fordprog.regx.interpreter.error;

import hu.fordprog.regx.interpreter.CodePosition;

public class InvalidRegularExpressionError extends SemanticError {
  private final String originalMessage;

  public InvalidRegularExpressionError(String originalMessage, CodePosition codePosition) {
    super(codePosition);

    this.originalMessage = originalMessage;
  }

  @Override
  public String getMessage() {
    return "Invalid regular expression found at " + getCodePosition()
        + ". Original error message is: " + originalMessage;
  }

  public String getOriginalMessage() {
    return originalMessage;
  }
}
