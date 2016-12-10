package hu.fordprog.regx.interpreter.symbol;

import java.util.List;

public abstract class Function {
  private final List<Symbol> arguments;

  private final ReturnType returnType;

  public Function(List<Symbol> arguments, ReturnType returnType) {
    this.arguments = arguments;
    this.returnType = returnType;
  }

  public List<Symbol> getArguments() {
    return arguments;
  }

  public ReturnType getReturnType() {
    return returnType;
  }

  public abstract void accept(FunctionVisitor visitor);

  public enum ReturnType {
    STRING,

    LIST,

    REGEX,

    VOID
  }
}
