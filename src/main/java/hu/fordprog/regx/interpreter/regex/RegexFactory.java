package hu.fordprog.regx.interpreter.regex;

import static hu.fordprog.regx.grammar.RegularExpressionParser.*;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import hu.fordprog.regx.grammar.RegularExpressionLexer;
import hu.fordprog.regx.grammar.RegularExpressionParser;
import hu.fordprog.regx.grammar.RegularExpressionParser.ClosureTermContext;
import hu.fordprog.regx.grammar.RegularExpressionParser.ConcatenationContext;
import hu.fordprog.regx.grammar.RegularExpressionParser.LiteralTermContext;
import hu.fordprog.regx.grammar.RegularExpressionParser.RegexContext;
import hu.fordprog.regx.grammar.RegularExpressionParser.TermContext;
import hu.fordprog.regx.interpreter.SyntaxErrorListener;

public class RegexFactory {
  private final RegexReader regexReader;

  public RegexFactory() {
    this.regexReader = new RegexReader();
  }

  public static Union createEmptyRegex() {
    Union union = new Union();

    union.getChildren().add(new Concatenation());

    return union;
  }

  public Union createRegex(RegexContext ctx) {
    return processUnion(ctx);
  }

  public Union createRegex(String str) {
    return regexReader.readRegex(str);
  }

  private Union processUnion(RegexContext ctx) {
    Union union = new Union();

    for (ConcatenationContext concat : ctx.concatenation()) {
      union.getChildren().add(processConcatenation(concat));
    }

    return union;
  }

  private Concatenation processConcatenation(ConcatenationContext ctx) {
    Concatenation concatenation = new Concatenation();

    for (TermContext term : ctx.term()) {
      concatenation.getChildren().add(processTerm(term));
    }

    return concatenation;
  }

  private Term processTerm(TermContext term) {
    if (term instanceof LiteralTermContext) {
      Atom atom = processAtom(((LiteralTermContext) term).atom());

      return new Term(atom);
    }

    Atom atom = processAtom(((ClosureTermContext) term).atom());

    return new ClosureTerm(atom);
  }

  private Atom processAtom(AtomContext atom) {
    if (atom.any() != null) {
      return new Any();
    } else if (atom.regexCharacter() != null) {
      return new RegexCharacter(atom.regexCharacter().getText());
    } else {
      return processGroup(atom.group());
    }
  }

  private Group processGroup(GroupContext group) {
    return new Group(createRegex(group.regex()));
  }

  private final class RegexReader {
    private final RegularExpressionLexer lexer;

    private final RegularExpressionParser parser;

    private final SyntaxErrorListener errorListener;

    private RegexReader() {
      this.errorListener = new SyntaxErrorListener();

      this.lexer = new RegularExpressionLexer(new ANTLRInputStream(""));

      this.lexer.removeErrorListeners();
      this.lexer.addErrorListener(errorListener);

      this.parser = new RegularExpressionParser(new CommonTokenStream(lexer));

      this.parser.removeErrorListeners();
      this.parser.addErrorListener(errorListener);
    }

    private Union readRegex(String str) {
      errorListener.clearErrors();

      lexer.setInputStream(new ANTLRInputStream(str));

      parser.setTokenStream(new CommonTokenStream(lexer));

      RegularExpressionParser.RegexContext ctx = parser.regex();

      if (!errorListener.getSyntaxErrors().isEmpty()) {
        return RegexFactory.createEmptyRegex();
      }

      return createRegex(ctx);
    }
  }
}
