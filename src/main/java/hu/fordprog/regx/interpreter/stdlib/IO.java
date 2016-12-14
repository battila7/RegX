package hu.fordprog.regx.interpreter.stdlib;

import static hu.fordprog.regx.interpreter.symbol.Symbol.nativeArgument;
import static hu.fordprog.regx.interpreter.symbol.Symbol.nativeFunction;
import static hu.fordprog.regx.interpreter.symbol.Type.FUNCTION;
import static hu.fordprog.regx.interpreter.symbol.Type.STRING;
import static hu.fordprog.regx.interpreter.symbol.Type.VOID;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import hu.fordprog.regx.interpreter.CodePosition;
import hu.fordprog.regx.interpreter.symbol.NativeFunction;
import hu.fordprog.regx.interpreter.symbol.Symbol;
import hu.fordprog.regx.interpreter.symbol.SymbolValue;

public final class IO implements ImplicitDeclarationSource {
  private static final Object VOID_RETURN_VALUE = null;

  private static final Scanner scanner = new Scanner(System.in);

  @Override
  public List<Symbol> getDeclarations() {
    List<Symbol> declarations = new ArrayList<>();

    NativeFunction printFn =
        new NativeFunction(singletonList(nativeArgument("str", STRING)), VOID, IO::print);

    declarations.add(nativeFunction("print", printFn));

    NativeFunction readStrFn =
        new NativeFunction(emptyList(), STRING, IO::readStr);

    declarations.add(nativeFunction("read_str", readStrFn));

    return declarations;
  }

  private static Object print(List<Object> arguments) {
    System.out.println(arguments.get(0));

    return VOID_RETURN_VALUE;
  }

  private static Object readStr(List<Object> arguments) {
    String str = scanner.nextLine();

    return str;
  }
}
