package hu.fordprog.regx.interpreter.symbol;

import java.util.List;

public abstract class Function {
  private final List<Symbol> arguments;

  private final Type returnType;

  public Function(List<Symbol> arguments, Type returnType) {
    this.arguments = arguments;

    this.returnType = returnType;
  }

  public List<Symbol> getArguments() {
    return arguments;
  }

  public Type getReturnType() {
    return returnType;
  }

  public abstract void accept(FunctionVisitor visitor);
}
