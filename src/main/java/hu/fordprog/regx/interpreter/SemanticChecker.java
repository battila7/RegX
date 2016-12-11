package hu.fordprog.regx.interpreter;

import static hu.fordprog.regx.interpreter.CodePosition.fromContext;
import static hu.fordprog.regx.interpreter.symbol.Type.FUNCTION;
import static hu.fordprog.regx.interpreter.symbol.Type.LIST;
import static hu.fordprog.regx.interpreter.symbol.Type.REGEX;
import static hu.fordprog.regx.interpreter.symbol.Type.STRING;
import static hu.fordprog.regx.interpreter.symbol.SymbolValue.from;
import static hu.fordprog.regx.interpreter.symbol.Type.VOID;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import hu.fordprog.regx.grammar.RegxBaseListener;
import hu.fordprog.regx.grammar.RegxParser;
import hu.fordprog.regx.grammar.RegxParser.FunctionCallContext;
import hu.fordprog.regx.grammar.RegxParser.FunctionDeclarationContext;
import hu.fordprog.regx.interpreter.error.*;
import hu.fordprog.regx.interpreter.symbol.*;

final class SemanticChecker extends RegxBaseListener {
  private final SymbolTable symbolTable;

  private final List<SemanticError> errors;

  private final ParseTreeProperty<Function> functions;

  private final ParseTreeProperty<Type> expressionTypes;

  private final ParseTreeProperty<Boolean> hasReturnStatement;

  public SemanticChecker() {
    this.symbolTable = new SymbolTable();

    this.errors = new ArrayList<>();

    this.functions = new ParseTreeProperty<>();

    this.expressionTypes = new ParseTreeProperty<>();

    this.hasReturnStatement = new ParseTreeProperty<>();
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
  public void enterFunctionDeclaration(FunctionDeclarationContext ctx) {
    if (checkIfDeclarationIsUnique(ctx.identifier())) {
      addFunctionSymbol(ctx);
    }

    symbolTable.newScope();
  }

  private void addFunctionSymbol(FunctionDeclarationContext ctx) {
    Type returnType = Type.valueOf(ctx.returnType().getText().toUpperCase());

    UserDefinedFunction function =
        new UserDefinedFunction(parseFunctionArguments(ctx), returnType, ctx);

    functions.put(ctx, function);

    symbolTable.addEntry(
        new Symbol(ctx.identifier().getText(), FUNCTION, fromContext(ctx), from(function)));
  }

  private List<Symbol> parseFunctionArguments(FunctionDeclarationContext ctx) {
    if (ctx.formalParameterList() == null) {
      return Collections.emptyList();
    }

    List<Symbol> symbols = new ArrayList<>();

    for (RegxParser.FormalParameterContext paramCtx : ctx.formalParameterList().formalParameter()) {
      Symbol s = new Symbol(paramCtx.identifier().getText(),
          Type.valueOf(paramCtx.typeName().getText().toUpperCase()),
          fromContext(paramCtx), from(null));

      symbols.add(s);
    }

    return symbols;
  }

  @Override
  public void enterReturnStatement(RegxParser.ReturnStatementContext ctx) {
    FunctionDeclarationContext functionCtx = findParentFunction(ctx);

    if (functions.get(functionCtx).getReturnType() == VOID) {
      errors.add(new ReturnFromVoidFunctionError(fromContext(ctx)));
    }

    hasReturnStatement.put(functionCtx, true);
  }

  @Override
  public void exitReturnStatement(RegxParser.ReturnStatementContext ctx) {
    FunctionDeclarationContext functionCtx = findParentFunction(ctx);

    Type targetType = functions.get(functionCtx).getReturnType();

    Type sourceType = expressionTypes.get(ctx.expression());

    if (targetType != VOID && targetType != sourceType) {
      errors.add(new ReturnTypeMismatchError(functionCtx.identifier().getText(),
          targetType, sourceType, fromContext(ctx)));
    }
  }

  private FunctionDeclarationContext findParentFunction(RegxParser.ReturnStatementContext ctx) {
    ParserRuleContext parent = ctx;

    do {
      parent = parent.getParent();
    } while (!(parent instanceof FunctionDeclarationContext));

    return (FunctionDeclarationContext)parent;
  }

  @Override
  public void exitFunctionDeclaration(FunctionDeclarationContext ctx) {
    if (functions.get(ctx).getReturnType() != VOID && hasReturnStatement.get(ctx) == null) {
      errors.add(new MissingReturnInFunctionError(ctx.identifier().getText(), fromContext(ctx)));
    }

    symbolTable.destroyScope();
  }

  @Override
  public void enterIdentifierExpression(RegxParser.IdentifierExpressionContext ctx) {
    Optional<Symbol> identifier = symbolTable.getEntry(ctx.identifier().getText());

    if (!identifier.isPresent()) {
      errors.add(new UndeclaredIdentifierError(ctx.identifier().getText(), fromContext(ctx)));

      return;
    }

    if (identifier.get().getType() == FUNCTION) {
      errors.add(new FunctionCallExpectedError(ctx.identifier().getText(), fromContext(ctx)));

      return;
    }

    expressionTypes.put(ctx, identifier.get().getType());
  }

  @Override
  public void enterLiteralExpression(RegxParser.LiteralExpressionContext ctx) {
    if (ctx.literal().stringLiteral() != null) {
      expressionTypes.put(ctx, STRING);
    } else if (ctx.literal().stringListLiteral() != null) {
      expressionTypes.put(ctx, LIST);
    } else {
      expressionTypes.put(ctx, REGEX);
    }
  }

  @Override
  public void enterFunctionCallExpression(RegxParser.FunctionCallExpressionContext ctx) {
    Optional<Symbol> symbol = symbolTable.getEntry(ctx.functionCall().identifier().getText());

    if (!symbol.isPresent()) {
      errors.add(new UndeclaredIdentifierError(ctx.functionCall().identifier().getText(),
                 fromContext(ctx)));

      return;
    }

    Function function = (Function)symbol.get().getSymbolValue().getValue();

    expressionTypes.put(ctx, function.getReturnType());
  }

  @Override
  public void exitFunctionCallExpression(RegxParser.FunctionCallExpressionContext ctx) {
    String functionIdentifier = ctx.functionCall().identifier().getText();

    Symbol functionSymbol = symbolTable.getEntry(functionIdentifier).get();

    checkFormalAndActualParameters(ctx, functionSymbol);
  }

  private void checkFormalAndActualParameters(RegxParser.FunctionCallExpressionContext ctx,
                                              Symbol functionSymbol) {
    RegxParser.ArgumentListContext argCtx = ctx.functionCall().argumentList();

    if (argCtx == null) {
      return;
    }

    Function function = (Function)functionSymbol.getSymbolValue().getValue();

    int expected = function.getArguments().size();

    int actual = argCtx.argument().size();

    if (expected != actual) {
      errors.add(new WrongNumberOfArgumentsError(expected, actual, fromContext(ctx)));
    }

    for (int i = 0; i < Math.min(expected, actual); ++i) {
      Type targetType = function.getReturnType();

      Type sourceType = expressionTypes.get(argCtx.argument(i).expression());

      if (targetType != sourceType) {
        errors.add(new ArgumentTypeMismatchError(ctx.functionCall().identifier().getText(),
            i + 1, targetType, sourceType, fromContext(argCtx.argument(i))));
      }
    }
  }


  @Override
  public void enterAssignmentExpression(RegxParser.AssignmentExpressionContext ctx) {
    Optional<Symbol> symbol = symbolTable.getEntry(ctx.assignment().identifier().getText());

    if (!symbol.isPresent()) {
      errors.add(new UndeclaredIdentifierError(ctx.assignment().identifier().getText(),
                 fromContext(ctx)));

      return;
    }

    if (symbol.get().getType() == FUNCTION) {
      errors.add(new AssignmentToFunctionError(fromContext(ctx)));

      return;
    }

    expressionTypes.put(ctx, symbol.get().getType());
  }

  @Override
  public void exitAssignmentExpression(RegxParser.AssignmentExpressionContext ctx) {
    Type targetType = expressionTypes.get(ctx);

    Type sourceType = expressionTypes.get(ctx.assignment().expression());

    if (sourceType == VOID) {
      FunctionCallContext functionCtx =
          (FunctionCallContext)ctx.assignment().expression().getRuleContext();

      Symbol functionSymbol = symbolTable.getEntry(functionCtx.identifier().getText()).get();

      errors.add(new AssignmentFromVoidFunctionError(functionSymbol.getIdentifier(),
          functionSymbol.getFirstOccurrence(), fromContext(ctx)));

      return;
    }

    if (targetType != sourceType) {
      errors.add(new TypeMismatchError(targetType, sourceType, fromContext(ctx)));
    }
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
