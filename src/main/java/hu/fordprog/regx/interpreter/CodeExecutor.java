package hu.fordprog.regx.interpreter;

import static hu.fordprog.regx.interpreter.CodePosition.fromContext;
import static hu.fordprog.regx.interpreter.symbol.SymbolValue.from;
import static hu.fordprog.regx.interpreter.symbol.Type.FUNCTION;
import static hu.fordprog.regx.interpreter.symbol.Type.LIST;
import static hu.fordprog.regx.interpreter.symbol.Type.REGEX;
import static hu.fordprog.regx.interpreter.symbol.Type.STRING;
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
import hu.fordprog.regx.grammar.RegxParser.ProgramContext;
import hu.fordprog.regx.interpreter.error.ArgumentTypeMismatchError;
import hu.fordprog.regx.interpreter.error.AssignmentFromVoidFunctionError;
import hu.fordprog.regx.interpreter.error.AssignmentToFunctionError;
import hu.fordprog.regx.interpreter.error.FunctionCallExpectedError;
import hu.fordprog.regx.interpreter.error.IdentifierAlreadyDeclaredError;
import hu.fordprog.regx.interpreter.error.InvalidMainFunctionSignatureError;
import hu.fordprog.regx.interpreter.error.InvalidRegularExpressionError;
import hu.fordprog.regx.interpreter.error.MissingMainFunctionError;
import hu.fordprog.regx.interpreter.error.MissingReturnInFunctionError;
import hu.fordprog.regx.interpreter.error.ReturnFromVoidFunctionError;
import hu.fordprog.regx.interpreter.error.ReturnTypeMismatchError;
import hu.fordprog.regx.interpreter.error.SemanticError;
import hu.fordprog.regx.interpreter.error.TypeMismatchError;
import hu.fordprog.regx.interpreter.error.UndeclaredIdentifierError;
import hu.fordprog.regx.interpreter.error.WrongNumberOfArgumentsError;
import hu.fordprog.regx.interpreter.symbol.Function;
import hu.fordprog.regx.interpreter.symbol.Symbol;
import hu.fordprog.regx.interpreter.symbol.SymbolTable;
import hu.fordprog.regx.interpreter.symbol.Type;
import hu.fordprog.regx.interpreter.symbol.UserDefinedFunction;

public class CodeExecutor extends RegxBaseListener {
  private final ProgramContext programCtx;

  private final SymbolTable symbolTable;

  private final ParseTreeProperty<Function> functions;

  public CodeExecutor(SymbolTable symbolTable, ProgramContext programCtx) {
    this.programCtx = programCtx;

    this.symbolTable = symbolTable;

    this.functions = new ParseTreeProperty<>();
  }

  public void execute() {

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
  public void exitStringDeclaration(RegxParser.StringDeclarationContext ctx) {
    Symbol symbol = new Symbol(ctx.identifier().getText(), STRING, fromContext(ctx), from(null));

    symbolTable.addEntry(symbol);
  }

  @Override
  public void exitListDeclaration(RegxParser.ListDeclarationContext ctx) {
    Symbol symbol = new Symbol(ctx.identifier().getText(), LIST, fromContext(ctx), from(null));

    symbolTable.addEntry(symbol);
  }

  @Override
  public void exitRegexDeclaration(RegxParser.RegexDeclarationContext ctx) {
    Symbol symbol = new Symbol(ctx.identifier().getText(), REGEX, fromContext(ctx), from(null));

    symbolTable.addEntry(symbol);
  }

  @Override
  public void enterFunctionDeclaration(RegxParser.FunctionDeclarationContext ctx) {
    addFunctionSymbol(ctx);

    symbolTable.enterScope(ctx);

    parseFunctionArguments(ctx).forEach(symbolTable::addEntry);
  }

  private void addFunctionSymbol(RegxParser.FunctionDeclarationContext ctx) {
    Type returnType = Type.valueOf(ctx.returnType().getText().toUpperCase());

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
          Type.valueOf(paramCtx.typeName().getText().toUpperCase()),
          fromContext(paramCtx), from(null));

      symbols.add(s);
    }

    return symbols;
  }

  @Override
  public void exitFunctionDeclaration(RegxParser.FunctionDeclarationContext ctx) {
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
