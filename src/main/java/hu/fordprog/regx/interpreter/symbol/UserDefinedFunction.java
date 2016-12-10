package hu.fordprog.regx.interpreter.symbol;

import java.util.List;
import hu.fordprog.regx.grammar.RegxParser.FunctionDeclarationContext;

public class UserDefinedFunction extends Function {
  private final FunctionDeclarationContext context;

  public UserDefinedFunction(List<Symbol> arguments,
                             Type returnType,
                             FunctionDeclarationContext context) {
    super(arguments, returnType);

    this.context = context;
  }

  @Override
  public void accept(FunctionVisitor visitor) {
    visitor.visit(this);
  }

  public FunctionDeclarationContext getContext() {
    return context;
  }
}
