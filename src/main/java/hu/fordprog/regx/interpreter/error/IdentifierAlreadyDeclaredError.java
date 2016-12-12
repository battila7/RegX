package hu.fordprog.regx.interpreter.error;

import hu.fordprog.regx.interpreter.CodePosition;

public class IdentifierAlreadyDeclaredError extends SemanticError {
  private final String identifier;

  private final CodePosition originalDeclaration;

  public IdentifierAlreadyDeclaredError(String identifier,
                                        CodePosition codePosition,
                                        CodePosition originalDeclaration) {
    super(codePosition);

    this.identifier = identifier;

    this.originalDeclaration = originalDeclaration;
  }

  @Override
  public String getMessage() {
    return "Identifier \"" + identifier + "\" already declared in current scope at "
        + originalDeclaration;
  }
}
