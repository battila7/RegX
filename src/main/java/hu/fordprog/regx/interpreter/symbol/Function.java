package hu.fordprog.regx.interpreter.symbol;

import java.util.Collections;
import java.util.List;

public abstract class Function {
  protected final List<Symbol> arguments;

  protected final Type returnType;

  public Function(List<Symbol> arguments, Type returnType) {
    this.arguments = arguments;

    this.returnType = returnType;
  }

  public List<Symbol> getArguments() {
    return Collections.unmodifiableList(arguments);
  }

  public Type getReturnType() {
    return returnType;
  }

  public abstract void accept(FunctionVisitor visitor);
}
