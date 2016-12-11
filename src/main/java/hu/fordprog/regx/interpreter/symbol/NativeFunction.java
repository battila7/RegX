package hu.fordprog.regx.interpreter.symbol;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class NativeFunction extends Function {
  private final java.util.function.Function<List<Object>, Object> implementation;

  public NativeFunction(List<Symbol> arguments,
                        Type returnType,
                        java.util.function.Function<List<Object>, Object> implementation) {
    super(arguments, returnType);

    this.implementation = implementation;
  }

  @Override
  public void accept(FunctionVisitor visitor) {
    visitor.visit(this);
  }

  public void call(SymbolValue target) {
    List<Object> passedArgs = getArguments().stream()
        .map(s -> s.getSymbolValue().getValue()).collect(Collectors.toList());

    target.setValue(implementation.apply(passedArgs));
  }
}
