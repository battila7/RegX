package hu.fordprog.regx.interpreter.symbol;

interface FunctionVisitor {
  void visit(UserDefinedFunction function);
}
