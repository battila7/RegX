package hu.fordprog.regx.interpreter.regex;

import static hu.fordprog.regx.interpreter.symbol.Symbol.nativeArgument;
import static hu.fordprog.regx.interpreter.symbol.Symbol.nativeFunction;
import static hu.fordprog.regx.interpreter.symbol.Type.REGEX;
import static hu.fordprog.regx.interpreter.symbol.Type.STRING;
import static hu.fordprog.regx.interpreter.symbol.Type.VOID;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import hu.fordprog.regx.interpreter.stdlib.ImplicitDeclarationSource;
import hu.fordprog.regx.interpreter.symbol.NativeFunction;
import hu.fordprog.regx.interpreter.symbol.Symbol;

public class RegexDeclarations implements ImplicitDeclarationSource {
  private static final Object VOID_RETURN_VALUE = null;

  @Override
  public List<Symbol> getDeclarations() {
    List<Symbol> declarations = new ArrayList<>();

    NativeFunction matchFn =
        new NativeFunction(asList(nativeArgument("rx", REGEX), nativeArgument("str", STRING)),
            STRING, RegexDeclarations::match);

    declarations.add(nativeFunction("match", matchFn));

    return declarations;
  }

  private static Object match(List<Object> arguments) {
    String regex = ((Regex)arguments.get(0)).asText();

    String s = "\\";

    regex = regex.replaceAll("\\+", "|");

    System.out.println(Pattern.compile(regex).matcher((String) arguments.get(1)).matches());

    return VOID_RETURN_VALUE;
  }
}