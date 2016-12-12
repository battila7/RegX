package hu.fordprog.regx.interpreter.error;

import hu.fordprog.regx.interpreter.CodePosition;

public class AssignmentFromVoidFunctionError extends SemanticError {
  private final String identifier;

  private final CodePosition declarationPosition;

  public AssignmentFromVoidFunctionError(String identifier,
                                         CodePosition declarationPosition,
                                         CodePosition codePosition) {
    super(codePosition);

    this.identifier = identifier;

    this.declarationPosition = declarationPosition;
  }

  @Override
  public String getMessage() {
    return "Assignment is not possible from \"" + identifier
        + "\" void function at " + getCodePosition()
        + ", declared at " + declarationPosition;
  }

  public String getIdentifier() {
    return identifier;
  }

  public CodePosition getDeclarationPosition() {
    return declarationPosition;
  }
}
