package hu.fordprog.regx.interpreter.stdlib;

import java.util.List;
import hu.fordprog.regx.interpreter.symbol.Symbol;

public interface ImplicitDeclarationSource {
  List<Symbol> getDeclarations();
}
