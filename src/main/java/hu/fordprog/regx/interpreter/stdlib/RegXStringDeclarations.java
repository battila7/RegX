package hu.fordprog.regx.interpreter.stdlib;

import static hu.fordprog.regx.interpreter.symbol.Symbol.nativeArgument;
import static hu.fordprog.regx.interpreter.symbol.Symbol.nativeFunction;
import static hu.fordprog.regx.interpreter.symbol.Type.LIST;
import static hu.fordprog.regx.interpreter.symbol.Type.STRING;
import static hu.fordprog.regx.interpreter.symbol.Type.VOID;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import hu.fordprog.regx.interpreter.symbol.NativeFunction;
import hu.fordprog.regx.interpreter.symbol.Symbol;

public class RegXStringDeclarations implements ImplicitDeclarationSource {
  @Override
  public List<Symbol> getDeclarations() {
    List<Symbol> declarations = new ArrayList<>();

    NativeFunction concatFn =
        new NativeFunction(asList(nativeArgument("str1", STRING), nativeArgument("str2", STRING)),
            STRING, RegXStringDeclarations::concat);

    declarations.add(nativeFunction("concat", concatFn));

    NativeFunction explodeFn =
        new NativeFunction(asList(nativeArgument("str1", STRING), nativeArgument("str2", STRING)),
            LIST, RegXStringDeclarations::explode);

    declarations.add(nativeFunction("explode", explodeFn));

    return declarations;
  }

  private static Object concat(List<Object> arguments) {
    return (String)arguments.get(0) + (String)arguments.get(1);
  }

  private static Object explode(List<Object> arguments) {
    String elements[] = ((String)arguments.get(0)).split((String)arguments.get(1));

    RegXList list = new RegXList();

    for (String s : elements) {
      list.pushBack(s);
    }

    return list;
  }
}
