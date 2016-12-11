package hu.fordprog.regx.interpreter.stdlib;

import static hu.fordprog.regx.interpreter.symbol.Type.FUNCTION;
import static hu.fordprog.regx.interpreter.symbol.Type.STRING;
import static hu.fordprog.regx.interpreter.symbol.Type.VOID;
import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import hu.fordprog.regx.interpreter.CodePosition;
import hu.fordprog.regx.interpreter.symbol.NativeFunction;
import hu.fordprog.regx.interpreter.symbol.Symbol;
import hu.fordprog.regx.interpreter.symbol.SymbolValue;

public final class IO implements ImplicitDeclarationSource {
  private static final Object VOID_RETURN_VALUE = null;

  @Override
  public List<Symbol> getDeclarations() {
    List<Symbol> declarations = new ArrayList<>();

    NativeFunction printFn =
        new NativeFunction(singletonList(new Symbol("str", STRING, new CodePosition(0, 0), SymbolValue.from(null))),
            VOID, IO::print);

    declarations.add(new Symbol("print", FUNCTION, new CodePosition(0, 0), SymbolValue.from(printFn)));

    return declarations;
  }

  private static Object print(List<Symbol> arguments) {
    System.out.println(arguments.get(0).getSymbolValue().getValue());

    return VOID_RETURN_VALUE;
  }
}
