package hu.fordprog.regx.interpreter.regex;

import static hu.fordprog.regx.grammar.RegularExpressionParser.*;

import hu.fordprog.regx.grammar.RegularExpressionParser;
import hu.fordprog.regx.grammar.RegularExpressionParser.ClosureTermContext;
import hu.fordprog.regx.grammar.RegularExpressionParser.ConcatenationContext;
import hu.fordprog.regx.grammar.RegularExpressionParser.LiteralTermContext;
import hu.fordprog.regx.grammar.RegularExpressionParser.RegexContext;
import hu.fordprog.regx.grammar.RegularExpressionParser.TermContext;

public class RegexFactory {
  public static Union createEmptyRegex() {
    Union union = new Union();

    union.getChildren().add(new Concatenation());

    return union;
  }

  public Union createRegex(RegexContext ctx) {
    return processUnion(ctx);
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
}
