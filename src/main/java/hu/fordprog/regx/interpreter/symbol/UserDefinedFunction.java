package hu.fordprog.regx.interpreter.symbol;

import java.util.ArrayList;
import java.util.List;
import hu.fordprog.regx.grammar.RegxParser.FunctionDeclarationContext;

public class UserDefinedFunction extends Function {
  private final FunctionDeclarationContext context;

  public UserDefinedFunction(Type returnType, FunctionDeclarationContext context) {
    super(new ArrayList<>(), returnType);

    this.context = context;
  }

  @Override
  public void accept(FunctionVisitor visitor) {
    visitor.visit(this);
  }

  public void addArgument(Symbol symbol) {
    arguments.add(symbol);
  }

  public FunctionDeclarationContext getContext() {
    return context;
  }
}
