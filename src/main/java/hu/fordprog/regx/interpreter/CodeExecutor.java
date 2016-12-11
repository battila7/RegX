package hu.fordprog.regx.interpreter;

import static hu.fordprog.regx.interpreter.CodePosition.fromContext;
import static hu.fordprog.regx.interpreter.symbol.SymbolValue.from;
import static hu.fordprog.regx.interpreter.symbol.Type.FUNCTION;
import static hu.fordprog.regx.interpreter.symbol.Type.LIST;
import static hu.fordprog.regx.interpreter.symbol.Type.REGEX;
import static hu.fordprog.regx.interpreter.symbol.Type.STRING;

import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import hu.fordprog.regx.grammar.RegxBaseListener;
import hu.fordprog.regx.grammar.RegxParser;
import hu.fordprog.regx.grammar.RegxParser.AssignmentExpressionContext;
import hu.fordprog.regx.grammar.RegxParser.DeclarationContext;
import hu.fordprog.regx.grammar.RegxParser.DeclarationInitializerContext;
import hu.fordprog.regx.grammar.RegxParser.FunctionDeclarationContext;
import hu.fordprog.regx.grammar.RegxParser.IdentifierExpressionContext;
import hu.fordprog.regx.grammar.RegxParser.ListDeclarationContext;
import hu.fordprog.regx.grammar.RegxParser.LiteralContext;
import hu.fordprog.regx.grammar.RegxParser.LiteralExpressionContext;
import hu.fordprog.regx.grammar.RegxParser.ProgramContext;

import hu.fordprog.regx.grammar.RegxParser.RegexDeclarationContext;
import hu.fordprog.regx.grammar.RegxParser.StringDeclarationContext;
import hu.fordprog.regx.grammar.RegxParser.VariableDeclarationContext;
import hu.fordprog.regx.interpreter.stdlib.RegXList;
import hu.fordprog.regx.interpreter.symbol.Function;
import hu.fordprog.regx.interpreter.symbol.Symbol;
import hu.fordprog.regx.interpreter.symbol.SymbolTable;
import hu.fordprog.regx.interpreter.symbol.SymbolValue;
import hu.fordprog.regx.interpreter.symbol.Type;
import hu.fordprog.regx.interpreter.symbol.UserDefinedFunction;

public class CodeExecutor extends RegxBaseListener {
  private static final String DEFAULT_STRING_VALUE = "";

  private final ProgramContext programCtx;

  private final SymbolTable symbolTable;

  private final ParseTreeProperty<Function> functions;

  public CodeExecutor(SymbolTable symbolTable, ProgramContext programCtx) {
    this.programCtx = programCtx;

    this.symbolTable = symbolTable;

    this.functions = new ParseTreeProperty<>();
  }

  public void execute() {
    symbolTable.enterScope(programCtx);

    assignGlobalVariables();
  }

  private void assignGlobalVariables() {
    for (DeclarationContext declaration : programCtx.declaration()) {
      FunctionDeclarationContext functionCtx = declaration.functionDeclaration();

      if (functionCtx != null) {
        if (functionCtx.identifier().getText().equals("main")) {
          break;
        } else {
          continue;
        }
      }

      processVariableDeclaration(declaration.variableDeclaration());
    }
  }

  private void processVariableDeclaration(VariableDeclarationContext declaration) {
    if (declaration.stringDeclaration() != null) {
      processStringDeclaration(declaration.stringDeclaration());
    } else if (declaration.listDeclaration() != null) {
      processListDeclaration(declaration.listDeclaration());
    } else {
      processRegexDeclaration(declaration.regexDeclaration());
    }
  }

  private void processStringDeclaration(StringDeclarationContext declaration) {
    Symbol symbol = symbolTable.getEntry(declaration.identifier().getText()).get();

    DeclarationInitializerContext initializer = declaration.declarationInitializer();

    if (initializer == null) {
      symbol.getSymbolValue().setValue(DEFAULT_STRING_VALUE);
    } else {
      executeExpression(initializer.expression(), symbol.getSymbolValue());
    }
  }

  private void processListDeclaration(ListDeclarationContext declaration) {
    Symbol symbol = symbolTable.getEntry(declaration.identifier().getText()).get();

    DeclarationInitializerContext initializer = declaration.declarationInitializer();

    if (initializer == null) {
      symbol.getSymbolValue().setValue(new RegXList());
    } else {
      executeExpression(initializer.expression(), symbol.getSymbolValue());
    }
  }

  private void processRegexDeclaration(RegexDeclarationContext declaration) {
    Symbol symbol = symbolTable.getEntry(declaration.identifier().getText()).get();

    DeclarationInitializerContext initializer = declaration.declarationInitializer();

    if (initializer == null) {
      // TODO Assign empty regex tree
      symbol.getSymbolValue().setValue(null);
    } else {
      executeExpression(initializer.expression(), symbol.getSymbolValue());
    }
  }

  private void executeExpression(RegxParser.ExpressionContext expression, SymbolValue target) {
    if (expression instanceof IdentifierExpressionContext) {
      executeIdentifierExpression((IdentifierExpressionContext)expression, target);
    } else if (expression instanceof LiteralExpressionContext) {
      executeLiteralExpression((LiteralExpressionContext)expression, target);
    } else if (expression instanceof AssignmentExpressionContext) {
      executeAssignmentExpression((AssignmentExpressionContext)expression, target);
    } else {
      // TODO function call
    }
  }

  private void executeAssignmentExpression(AssignmentExpressionContext expression,
                                           SymbolValue target) {
    String identifier = expression.assignment().identifier().getText();

    Symbol symbol = symbolTable.getEntry(identifier).get();

    executeExpression(expression.assignment().expression(), symbol.getSymbolValue());

    target.setValue(symbol.getSymbolValue().getValue());
  }

  private void executeIdentifierExpression(IdentifierExpressionContext expression,
                                           SymbolValue target) {
    Symbol source = symbolTable.getEntry(expression.identifier().getText()).get();

    target.setValue(source.getSymbolValue().getValue());
  }


  private void executeLiteralExpression(LiteralExpressionContext expression, SymbolValue target) {
    target.setValue(getLiteralValue(expression.literal()));
  }

  private Object getLiteralValue(LiteralContext literal) {
    if (literal.stringLiteral() != null) {
      String str = literal.stringLiteral().getText();

      // Trim leading and trailing double quotes
      return str.substring(1, str.length() - 1);
    } else if (literal.stringListLiteral() != null) {
      RegXList list = new RegXList();

      literal.stringListLiteral().stringLiteralList().StringLiteral()
          .stream()
          .map(TerminalNode::getText)
          .map(s -> s.substring(1, s.length() - 1))
          .forEach(list::pushBack);

      return list;
    } else {
      // TODO Return Regex
      return null;
    }
  }

  @Override
  public void enterProgram(RegxParser.ProgramContext ctx) {
    symbolTable.enterScope(ctx);
  }

  @Override
  public void exitProgram(RegxParser.ProgramContext ctx) {
    Optional<Symbol> main = symbolTable.getEntry("main");

    symbolTable.exitScope();
  }

  @Override
  public void exitStringDeclaration(StringDeclarationContext ctx) {
    Symbol symbol = new Symbol(ctx.identifier().getText(), STRING, fromContext(ctx), from(null));

    symbolTable.addEntry(symbol);
  }

  @Override
  public void exitListDeclaration(ListDeclarationContext ctx) {
    Symbol symbol = new Symbol(ctx.identifier().getText(), LIST, fromContext(ctx), from(null));

    symbolTable.addEntry(symbol);
  }

  @Override
  public void exitRegexDeclaration(RegexDeclarationContext ctx) {
    Symbol symbol = new Symbol(ctx.identifier().getText(), REGEX, fromContext(ctx), from(null));

    symbolTable.addEntry(symbol);
  }

  @Override
  public void enterFunctionDeclaration(FunctionDeclarationContext ctx) {
    addFunctionSymbol(ctx);

    symbolTable.enterScope(ctx);

    parseFunctionArguments(ctx).forEach(symbolTable::addEntry);
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
  public void exitFunctionDeclaration(FunctionDeclarationContext ctx) {
    if (functions.get(ctx) == null) {
      return;
    }

    symbolTable.exitScope();
  }

  @Override
  public void enterForLoop(RegxParser.ForLoopContext ctx) {
    symbolTable.enterScope(ctx);

    Symbol symbol = new Symbol(ctx.identifier().getText(), STRING, fromContext(ctx), from(null));

    symbolTable.addEntry(symbol);
  }

  @Override
  public void exitForLoop(RegxParser.ForLoopContext ctx) {
    symbolTable.exitScope();
  }
}
