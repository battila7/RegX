package hu.fordprog.regx.interpreter;

import static hu.fordprog.regx.interpreter.CodePosition.fromContext;
import static hu.fordprog.regx.interpreter.symbol.SymbolType.LIST;
import static hu.fordprog.regx.interpreter.symbol.SymbolType.REGEX;
import static hu.fordprog.regx.interpreter.symbol.SymbolType.STRING;
import static hu.fordprog.regx.interpreter.symbol.SymbolValue.from;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import hu.fordprog.regx.grammar.RegxBaseListener;
import hu.fordprog.regx.grammar.RegxParser;
import hu.fordprog.regx.interpreter.error.IdentifierAlreadyDeclaredError;
import hu.fordprog.regx.interpreter.error.SemanticError;
import hu.fordprog.regx.interpreter.symbol.SymbolTable;
import hu.fordprog.regx.interpreter.symbol.SymbolTable.Entry;

final class SemanticChecker extends RegxBaseListener {
  private final SymbolTable symbolTable;

  private final List<SemanticError> errors;

  public SemanticChecker() {
    this.symbolTable = new SymbolTable();

    this.errors = new ArrayList<>();
  }

  @Override
  public void enterProgram(RegxParser.ProgramContext ctx) {
    symbolTable.newScope();
  }

  @Override
  public void exitProgram(RegxParser.ProgramContext ctx) {
    symbolTable.destroyScope();
  }

  @Override
  public void exitStringDeclaration(RegxParser.StringDeclarationContext ctx) {
    if (checkIfDeclarationIsUnique(ctx.identifier())) {
      Entry entry = new Entry(ctx.identifier().getText(), STRING, fromContext(ctx), from(null));

      symbolTable.addEntry(entry);
    }
  }

  @Override
  public void exitListDeclaration(RegxParser.ListDeclarationContext ctx) {
    if (checkIfDeclarationIsUnique(ctx.identifier())) {
      Entry entry = new Entry(ctx.identifier().getText(), LIST, fromContext(ctx), from(null));

      symbolTable.addEntry(entry);
    }
  }

  @Override
  public void exitRegexDeclaration(RegxParser.RegexDeclarationContext ctx) {
    if (checkIfDeclarationIsUnique(ctx.identifier())) {
      Entry entry = new Entry(ctx.identifier().getText(), REGEX, fromContext(ctx), from(null));

      symbolTable.addEntry(entry);
    }
  }

  public List<SemanticError> getErrors() {
    return Collections.unmodifiableList(errors);
  }

  private boolean checkIfDeclarationIsUnique(RegxParser.IdentifierContext ctx) {
    Optional<Entry> originalEntry = symbolTable.getEntryFromCurrentScope(ctx.getText());

    if (originalEntry.isPresent()) {
      errors.add(new IdentifierAlreadyDeclaredError(ctx.getText(),
          fromContext(ctx), originalEntry.get().getFirstOccurrence()));

      return false;
    }

    return true;
  }
}
