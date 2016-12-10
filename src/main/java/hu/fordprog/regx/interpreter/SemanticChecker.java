package hu.fordprog.regx.interpreter;

import static hu.fordprog.regx.interpreter.CodePosition.fromContext;
import static hu.fordprog.regx.interpreter.symbol.Function.ReturnType.VOID;
import static hu.fordprog.regx.interpreter.symbol.SymbolType.FUNCTION;
import static hu.fordprog.regx.interpreter.symbol.SymbolType.LIST;
import static hu.fordprog.regx.interpreter.symbol.SymbolType.REGEX;
import static hu.fordprog.regx.interpreter.symbol.SymbolType.STRING;
import static hu.fordprog.regx.interpreter.symbol.SymbolValue.from;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import hu.fordprog.regx.grammar.RegxBaseListener;
import hu.fordprog.regx.grammar.RegxParser;
import hu.fordprog.regx.interpreter.error.FunctionCallExpectedError;
import hu.fordprog.regx.interpreter.error.IdentifierAlreadyDeclaredError;
import hu.fordprog.regx.interpreter.error.ReturnFromVoidFunctionError;
import hu.fordprog.regx.interpreter.error.SemanticError;
import hu.fordprog.regx.interpreter.error.UndeclaredIdentifierError;
import hu.fordprog.regx.interpreter.symbol.Function;
import hu.fordprog.regx.interpreter.symbol.Function.ReturnType;
import hu.fordprog.regx.interpreter.symbol.SymbolTable;
import hu.fordprog.regx.interpreter.symbol.Symbol;
import hu.fordprog.regx.interpreter.symbol.SymbolType;
import hu.fordprog.regx.interpreter.symbol.UserDefinedFunction;

final class SemanticChecker extends RegxBaseListener {
  private final SymbolTable symbolTable;

  private final List<SemanticError> errors;

  private final ParseTreeProperty<Function> functions;

  private final ParseTreeProperty<SymbolType> expressionTypes;

  public SemanticChecker() {
    this.symbolTable = new SymbolTable();

    this.errors = new ArrayList<>();

    this.functions = new ParseTreeProperty<>();

    this.expressionTypes = new ParseTreeProperty<>();
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
      Symbol symbol = new Symbol(ctx.identifier().getText(), STRING, fromContext(ctx), from(null));

      symbolTable.addEntry(symbol);
    }
  }

  @Override
  public void exitListDeclaration(RegxParser.ListDeclarationContext ctx) {
    if (checkIfDeclarationIsUnique(ctx.identifier())) {
      Symbol symbol = new Symbol(ctx.identifier().getText(), LIST, fromContext(ctx), from(null));

      symbolTable.addEntry(symbol);
    }
  }

  @Override
  public void exitRegexDeclaration(RegxParser.RegexDeclarationContext ctx) {
    if (checkIfDeclarationIsUnique(ctx.identifier())) {
      Symbol symbol = new Symbol(ctx.identifier().getText(), REGEX, fromContext(ctx), from(null));

      symbolTable.addEntry(symbol);
    }
  }

  @Override
  public void enterFunctionDeclaration(RegxParser.FunctionDeclarationContext ctx) {
    if (checkIfDeclarationIsUnique(ctx.identifier())) {
      addFunctionSymbol(ctx);
    }

    symbolTable.newScope();
  }

  private void addFunctionSymbol(RegxParser.FunctionDeclarationContext ctx) {
    ReturnType returnType = ReturnType.valueOf(ctx.returnType().getText().toUpperCase());

    UserDefinedFunction function =
        new UserDefinedFunction(parseFunctionArguments(ctx), returnType, ctx);

    functions.put(ctx, function);

    symbolTable.addEntry(
        new Symbol(ctx.identifier().getText(), FUNCTION, fromContext(ctx), from(function)));
  }

  private List<Symbol> parseFunctionArguments(RegxParser.FunctionDeclarationContext ctx) {
    if (ctx.formalParameterList() == null) {
      return Collections.emptyList();
    }

    List<Symbol> symbols = new ArrayList<>();

    for (RegxParser.FormalParameterContext paramCtx : ctx.formalParameterList().formalParameter()) {
      Symbol s = new Symbol(paramCtx.identifier().getText(),
          SymbolType.valueOf(paramCtx.typeName().getText().toUpperCase()),
          fromContext(paramCtx), from(null));

      symbols.add(s);
    }

    return symbols;
  }

  @Override
  public void enterReturnStatement(RegxParser.ReturnStatementContext ctx) {
    Function function = findParentFunction(ctx);

    if (function.getReturnType() == VOID) {
      errors.add(new ReturnFromVoidFunctionError(fromContext(ctx)));
    }
  }

  @Override
  public void exitReturnStatement(RegxParser.ReturnStatementContext ctx) {
    super.exitReturnStatement(ctx);
  }

  private Function findParentFunction(RegxParser.ReturnStatementContext ctx) {
    ParserRuleContext parent = ctx;

    do {
      parent = parent.getParent();
    } while (!(parent instanceof RegxParser.FunctionDeclarationContext));

    return functions.get(parent);
  }

  @Override
  public void exitFunctionDeclaration(RegxParser.FunctionDeclarationContext ctx) {
    symbolTable.destroyScope();
  }

  @Override
  public void enterIdentifierExpression(RegxParser.IdentifierExpressionContext ctx) {
    Optional<Symbol> identifier = symbolTable.getEntry(ctx.identifier().getText());

    if (!identifier.isPresent()) {
      errors.add(new UndeclaredIdentifierError(ctx.identifier().getText(), fromContext(ctx)));

      return;
    }

    if (identifier.get().getSymbolType() == FUNCTION) {
      errors.add(new FunctionCallExpectedError(ctx.identifier().getText(), fromContext(ctx)));
    }

    expressionTypes.put(ctx, identifier.get().getSymbolType());
  }

  @Override
  public void exitIdentifierExpression(RegxParser.IdentifierExpressionContext ctx) {
    super.exitIdentifierExpression(ctx);
  }

  @Override
  public void enterLiteralExpression(RegxParser.LiteralExpressionContext ctx) {
    super.enterLiteralExpression(ctx);
  }

  @Override
  public void exitLiteralExpression(RegxParser.LiteralExpressionContext ctx) {
    super.exitLiteralExpression(ctx);
  }

  @Override
  public void enterFunctionCallExpression(RegxParser.FunctionCallExpressionContext ctx) {
    super.enterFunctionCallExpression(ctx);
  }

  @Override
  public void exitFunctionCallExpression(RegxParser.FunctionCallExpressionContext ctx) {
    super.exitFunctionCallExpression(ctx);
  }

  @Override
  public void enterAssignmentExpression(RegxParser.AssignmentExpressionContext ctx) {
    super.enterAssignmentExpression(ctx);
  }

  @Override
  public void exitAssignmentExpression(RegxParser.AssignmentExpressionContext ctx) {
    super.exitAssignmentExpression(ctx);
  }

  @Override
  public void enterForLoop(RegxParser.ForLoopContext ctx) {
    symbolTable.newScope();

    Symbol symbol = new Symbol(ctx.identifier().getText(), STRING, fromContext(ctx), from(null));

    symbolTable.addEntry(symbol);
  }

  @Override
  public void exitForLoop(RegxParser.ForLoopContext ctx) {
    symbolTable.destroyScope();
  }

  public List<SemanticError> getErrors() {
    return Collections.unmodifiableList(errors);
  }

  private boolean checkIfDeclarationIsUnique(RegxParser.IdentifierContext ctx) {
    Optional<Symbol> originalEntry = symbolTable.getEntryFromCurrentScope(ctx.getText());

    if (originalEntry.isPresent()) {
      errors.add(new IdentifierAlreadyDeclaredError(ctx.getText(),
          fromContext(ctx), originalEntry.get().getFirstOccurrence()));

      return false;
    }

    return true;
  }
}
