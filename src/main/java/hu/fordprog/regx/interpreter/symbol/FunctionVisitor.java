package hu.fordprog.regx.interpreter.symbol;

public interface FunctionVisitor {
  void visit(UserDefinedFunction function);

  void visit(NativeFunction function);
}
