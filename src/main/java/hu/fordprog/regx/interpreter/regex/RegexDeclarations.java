package hu.fordprog.regx.interpreter.regex;

import static hu.fordprog.regx.interpreter.symbol.Symbol.nativeArgument;
import static hu.fordprog.regx.interpreter.symbol.Symbol.nativeFunction;
import static hu.fordprog.regx.interpreter.symbol.Type.REGEX;
import static hu.fordprog.regx.interpreter.symbol.Type.STRING;
import static hu.fordprog.regx.interpreter.symbol.Type.VOID;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import hu.fordprog.regx.interpreter.stdlib.ImplicitDeclarationSource;
import hu.fordprog.regx.interpreter.symbol.NativeFunction;
import hu.fordprog.regx.interpreter.symbol.Symbol;

public class RegexDeclarations implements ImplicitDeclarationSource {
  private static final Object VOID_RETURN_VALUE = null;

  private final RegexFactory regexFactory;

  public RegexDeclarations() {
    this.regexFactory = new RegexFactory();
  }

  @Override
  public List<Symbol> getDeclarations() {
    List<Symbol> declarations = new ArrayList<>();

    NativeFunction matchFn =
        new NativeFunction(asList(nativeArgument("rx", REGEX), nativeArgument("str", STRING)),
            STRING, this::match);

    declarations.add(nativeFunction("match", matchFn));

    NativeFunction printAuFn =
        new NativeFunction(singletonList(nativeArgument("rx", REGEX)),
            VOID, this::printAutomaton);

    declarations.add(nativeFunction("print_automaton", printAuFn));

    NativeFunction normalizeFn =
        new NativeFunction(singletonList(nativeArgument("rx", REGEX)),
            REGEX, this::normalizeRegex);

    declarations.add(nativeFunction("normalize", normalizeFn));

    NativeFunction simplifyFn =
        new NativeFunction(singletonList(nativeArgument("rx", REGEX)),
            REGEX, this::simplifyRegex);

    declarations.add(nativeFunction("simplify", simplifyFn));

    NativeFunction asTextFn =
        new NativeFunction(singletonList(nativeArgument("rx", REGEX)),
            STRING, this::regexToString);

    declarations.add(nativeFunction("as_text", asTextFn));

    NativeFunction substituteFn =
        new NativeFunction(asList(nativeArgument("target", REGEX), nativeArgument("str", STRING),
            nativeArgument("replacement", REGEX)), REGEX, this::substitute);

    declarations.add(nativeFunction("substitute", substituteFn));

    return declarations;
  }

  private Object match(List<Object> arguments) {
    String regex = ((Regex)arguments.get(0)).asText();

    String s = "\\";

    regex = regex.replaceAll("\\+", "|");

    System.out.println(Pattern.compile(regex).matcher((String) arguments.get(1)).matches());

    return VOID_RETURN_VALUE;
  }

  private Object printAutomaton(List<Object> arguments) {
    Union regex = ((Union)arguments.get(0));

    System.out.println(regex.makeAutomaton());

    return VOID_RETURN_VALUE;
  }

  private Object normalizeRegex(List<Object> arguments){
    Regex regex = (Regex)arguments.get(0);

    return regex.normalize();
  }


  private Object simplifyRegex(List<Object> arguments){
    Regex regex = (Regex)arguments.get(0);

    return regex.simplify();
  }

  private Object regexToString(List<Object> arguments) {
    Regex regex = (Regex) arguments.get(0);

    return regex.asText();
  }

  private Object substitute(List<Object> arguments) {
    Regex targetRegex = (Regex)arguments.get(0);

    String target = targetRegex.asText();

    String replacement = ((Regex)arguments.get(2)).asText();

    String result = target.replace((String)arguments.get(1), replacement);

    return regexFactory.createRegex(result);
  }
}
