package hu.fordprog.regx.interpreter.stdlib;

import static hu.fordprog.regx.interpreter.symbol.Symbol.nativeArgument;
import static hu.fordprog.regx.interpreter.symbol.Symbol.nativeFunction;
import static hu.fordprog.regx.interpreter.symbol.Type.LIST;
import static hu.fordprog.regx.interpreter.symbol.Type.STRING;
import static hu.fordprog.regx.interpreter.symbol.Type.VOID;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;


import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import hu.fordprog.regx.grammar.RegxLexer;
import hu.fordprog.regx.grammar.RegxParser;
import hu.fordprog.regx.grammar.RegxParser.StringListLiteralContext;
import hu.fordprog.regx.interpreter.SyntaxErrorListener;
import hu.fordprog.regx.interpreter.symbol.NativeFunction;
import hu.fordprog.regx.interpreter.symbol.Symbol;

public final class IO implements ImplicitDeclarationSource {
  private static final Object VOID_RETURN_VALUE = null;

  private final Scanner scanner = new Scanner(System.in);

  private final ListReader listReader;

  public IO() {
    this.listReader = new ListReader();
  }

  private Pattern createListPattern() {


    return null;
  }

  @Override
  public List<Symbol> getDeclarations() {
    List<Symbol> declarations = new ArrayList<>();

    NativeFunction printFn =
        new NativeFunction(singletonList(nativeArgument("str", STRING)), VOID, this::print);

    declarations.add(nativeFunction("print", printFn));

    NativeFunction readStrFn =
        new NativeFunction(emptyList(), STRING, this::readStr);

    declarations.add(nativeFunction("read_str", readStrFn));

    NativeFunction readListFn =
        new NativeFunction(emptyList(), LIST, this::readList);

    declarations.add(nativeFunction("read_list", readListFn));

    return declarations;
  }

  private Object print(List<Object> arguments) {
    System.out.println(arguments.get(0));

    return VOID_RETURN_VALUE;
  }

  private Object readStr(List<Object> arguments) {
    return scanner.nextLine();
  }

  private Object readList(List<Object> arguments) {
    return listReader.readList(scanner.nextLine());
  }

  private final class ListReader {
    private RegxLexer lexer;

    private RegxParser parser;

    private SyntaxErrorListener errorListener;

    private ListReader() {
      this.errorListener = new SyntaxErrorListener();

      this.lexer = new RegxLexer(new ANTLRInputStream(""));

      this.lexer.removeErrorListeners();
      this.lexer.addErrorListener(errorListener);

      this.parser = new RegxParser(new CommonTokenStream(lexer));

      this.parser.removeErrorListeners();
      this.parser.addErrorListener(errorListener);
    }

    private RegXList readList(String str) {
      errorListener.clearErrors();

      lexer.setInputStream(new ANTLRInputStream(str));

      parser.setTokenStream(new CommonTokenStream(lexer));

      StringListLiteralContext ctx = parser.stringListLiteral();

      if (!errorListener.getSyntaxErrors().isEmpty()) {
        return new RegXList();
      }

      return processLiteral(ctx);
    }

    private RegXList processLiteral(StringListLiteralContext ctx) {
      RegXList list = new RegXList();

      if (ctx.stringLiteralList() != null) {
        ctx.stringLiteralList().StringLiteral()
            .stream()
            .map(TerminalNode::getText)
            .map(s -> s.substring(1, s.length() - 1))
            .forEach(list::pushBack);
      }

      return list;
    }
  }
}
