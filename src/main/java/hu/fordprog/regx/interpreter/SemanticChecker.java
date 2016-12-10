package hu.fordprog.regx.interpreter;

import hu.fordprog.regx.grammar.RegxBaseListener;
import hu.fordprog.regx.interpreter.symbol.SymbolTable;

final class SemanticChecker extends RegxBaseListener {
  private final SymbolTable symbolTable;

  public SemanticChecker() {
    this.symbolTable = new SymbolTable();
  }
}
