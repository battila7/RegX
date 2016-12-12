package hu.fordprog.regx.interpreter;

import static hu.fordprog.regx.interpreter.CodePosition.fromContext;
import static hu.fordprog.regx.interpreter.symbol.Type.FUNCTION;
import static hu.fordprog.regx.interpreter.symbol.Type.LIST;
import static hu.fordprog.regx.interpreter.symbol.Type.REGEX;
import static hu.fordprog.regx.interpreter.symbol.Type.STRING;
import static hu.fordprog.regx.interpreter.symbol.SymbolValue.from;
import static hu.fordprog.regx.interpreter.symbol.Type.VOID;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import hu.fordprog.regx.grammar.RegularExpressionLexer;
import hu.fordprog.regx.grammar.RegularExpressionParser;
import hu.fordprog.regx.grammar.RegxBaseListener;
import hu.fordprog.regx.grammar.RegxParser;
import hu.fordprog.regx.grammar.RegxParser.DeclarationInitializerContext;
import hu.fordprog.regx.grammar.RegxParser.FunctionDeclarationContext;
import hu.fordprog.regx.interpreter.error.*;
import hu.fordprog.regx.interpreter.regex.RegexFactory;
import hu.fordprog.regx.interpreter.symbol.*;

final class SemanticChecker extends RegxBaseListener {
  private SymbolTable symbolTable;

  private final List<SemanticError> errors;

  private final ParseTreeProperty<Function> functions;

  private final ParseTreeProperty<Type> expressionTypes;

  private final ParseTreeProperty<Boolean> hasReturnStatement;

  private final List<Symbol> implicitDeclarations;

  public SemanticChecker(List<Symbol> implicitDeclarations) {
    this.errors = new ArrayList<>();

    this.functions = new ParseTreeProperty<>();

    this.expressionTypes = new ParseTreeProperty<>();

    this.hasReturnStatement = new ParseTreeProperty<>();

    this.implicitDeclarations = implicitDeclarations;
  }

  @Override
  public void enterProgram(RegxParser.ProgramContext ctx) {
    symbolTable = new SymbolTable(ctx, implicitDeclarations);
  }

  @Override
  public void exitProgram(RegxParser.ProgramContext ctx) {
    Optional<Symbol> main = symbolTable.getEntry("main");

    if (!main.isPresent() || main.get().getType() != FUNCTION) {
      errors.add(new MissingMainFunctionError(fromContext(ctx)));

      return;
    }

    Function function = (Function)main.get().getSymbolValue().getValue();

    if (function.getReturnType() != VOID || !function.getArguments().isEmpty()) {
      errors.add(new InvalidMainFunctionSignatureError(main.get().getFirstOccurrence()));
    }
  }

  @Override
  public void exitStringDeclaration(RegxParser.StringDeclarationContext ctx) {
    if (checkIfDeclarationIsUnique(ctx.identifier())) {
      Symbol symbol = new Symbol(ctx.identifier().getText(), STRING, fromContext(ctx), from(null));

      symbolTable.addEntry(symbol);
    }

    checkDeclarationType(STRING, ctx.declarationInitializer());
  }

  @Override
  public void exitListDeclaration(RegxParser.ListDeclarationContext ctx) {
    if (checkIfDeclarationIsUnique(ctx.identifier())) {
      Symbol symbol = new Symbol(ctx.identifier().getText(), LIST, fromContext(ctx), from(null));

      symbolTable.addEntry(symbol);
    }

    checkDeclarationType(LIST, ctx.declarationInitializer());
  }

  @Override
  public void exitRegexDeclaration(RegxParser.RegexDeclarationContext ctx) {
    if (checkIfDeclarationIsUnique(ctx.identifier())) {
      Symbol symbol = new Symbol(ctx.identifier().getText(), REGEX, fromContext(ctx), from(null));

      symbolTable.addEntry(symbol);
    }

    checkDeclarationType(REGEX, ctx.declarationInitializer());
  }

  private void checkDeclarationType(Type expected, DeclarationInitializerContext declarationCtx) {
    if (declarationCtx != null) {
      Type actual = expressionTypes.get(declarationCtx.expression());

      if (actual != expected) {
        errors.add(new TypeMismatchError(expected, actual, fromContext(declarationCtx)));
      }
    }
  }

  @Override
  public void enterFunctionDeclaration(FunctionDeclarationContext ctx) {
    UserDefinedFunction function = null;

    if (checkIfDeclarationIsUnique(ctx.identifier())) {
      function = addFunctionSymbol(ctx);
    }

    symbolTable.enterScope(ctx);

    for (Symbol s : parseFunctionArguments(ctx)) {
      if (function != null) {
        function.addArgument(s);
      }

      symbolTable.addEntry(s);
    }
  }

  private UserDefinedFunction addFunctionSymbol(FunctionDeclarationContext ctx) {
    Type returnType = Type.valueOf(ctx.returnType().getText().toUpperCase());

    UserDefinedFunction function =
        new UserDefinedFunction(returnType, ctx);

    functions.put(ctx, function);

    symbolTable.addEntry(
        new Symbol(ctx.identifier().getText(), FUNCTION, fromContext(ctx), from(function)));

    return function;
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
    if (functions.get(ctx) == null) {
      return;
    }

    if (functions.get(ctx).getReturnType() != VOID && hasReturnStatement.get(ctx) == null) {
      errors.add(new MissingReturnInFunctionError(ctx.identifier().getText(), fromContext(ctx)));
    }

    symbolTable.exitScope();
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
  public void enterRegexLiteral(RegxParser.RegexLiteralContext ctx) {
    String regex = ctx.getText();

    // Trim leading and trailing /
    regex = regex.substring(1, regex.length() - 1);

    SyntaxErrorListener regexErrorListener = new SyntaxErrorListener();

    RegularExpressionLexer lexer = new RegularExpressionLexer(new ANTLRInputStream(regex));

    lexer.removeErrorListeners();
    lexer.addErrorListener(regexErrorListener);

    RegularExpressionParser parser = new RegularExpressionParser(new CommonTokenStream(lexer));

    parser.removeErrorListeners();
    parser.addErrorListener(regexErrorListener);

    RegularExpressionParser.RegexContext regexCtx = parser.start().regex();

    regexErrorListener.getSyntaxErrors().stream()
        .map(s -> new InvalidRegularExpressionError(s.toString(), fromContext(ctx)))
        .forEach(errors::add);

    if (regexErrorListener.getSyntaxErrors().isEmpty()) {
      RegexFactory factory = new RegexFactory();

      factory.createRegex(regexCtx);
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

    Optional<Symbol> functionSymbol = symbolTable.getEntry(functionIdentifier);

    if (functionSymbol.isPresent()) {
      checkFormalAndActualParameters(ctx, functionSymbol.get());
    }
  }

  private void checkFormalAndActualParameters(RegxParser.FunctionCallExpressionContext ctx,
                                              Symbol functionSymbol) {
    RegxParser.ArgumentListContext argCtx = ctx.functionCall().argumentList();

    Function function = (Function)functionSymbol.getSymbolValue().getValue();

    int expected = function.getArguments().size();

    int actual = (argCtx == null) ? 0 : argCtx.argument().size();

    if (expected != actual) {
      errors.add(new WrongNumberOfArgumentsError(expected, actual, fromContext(ctx)));
    }

    for (int i = 0; i < Math.min(expected, actual); ++i) {
      Type targetType = function.getArguments().get(i).getType();

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
      RegxParser.FunctionCallExpressionContext functionCtx =
          (RegxParser.FunctionCallExpressionContext) ctx.assignment().expression();

      Symbol functionSymbol =
          symbolTable.getEntry(functionCtx.functionCall().identifier().getText()).get();

      errors.add(new AssignmentFromVoidFunctionError(functionSymbol.getIdentifier(),
          functionSymbol.getFirstOccurrence(), fromContext(ctx)));

      return;
    }

    if (targetType != null && targetType != sourceType) {
      errors.add(new TypeMismatchError(targetType, sourceType, fromContext(ctx)));
    }
  }

  @Override
  public void enterForLoop(RegxParser.ForLoopContext ctx) {
    symbolTable.enterScope(ctx);

    Symbol symbol = new Symbol(ctx.identifier().getText(), STRING, fromContext(ctx), from(null));

    symbolTable.addEntry(symbol);
  }

  @Override
  public void exitForLoop(RegxParser.ForLoopContext ctx) {
    if (expressionTypes.get(ctx.expression()) != LIST) {
      errors.add(new InvalidForLoopExpression(fromContext(ctx.expression()),
                                              expressionTypes.get(ctx.expression())));
    }

    symbolTable.exitScope();
  }

  public List<SemanticError> getErrors() {
    return Collections.unmodifiableList(errors);
  }

  public SymbolTable getSymbolTable() {
    return symbolTable;
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
