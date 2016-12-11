package hu.fordprog.regx.interpreter.symbol;

import java.util.List;
import java.util.function.BiConsumer;

public class NativeFunction extends Function {
  private final java.util.function.Function<List<Symbol>, Object> implementation;

  public NativeFunction(List<Symbol> arguments,
                        Type returnType,
                        java.util.function.Function<List<Symbol>, Object> implementation) {
    super(arguments, returnType);

    this.implementation = implementation;
  }

  @Override
  public void accept(FunctionVisitor visitor) {
    visitor.visit(this);
  }

  public void call(SymbolValue target) {
    target.setValue(implementation.apply(getArguments()));
  }
}
