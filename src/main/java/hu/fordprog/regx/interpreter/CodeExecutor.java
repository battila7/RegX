package hu.fordprog.regx.interpreter;

import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import hu.fordprog.regx.grammar.RegxParser;
import hu.fordprog.regx.grammar.RegxParser.ArgumentListContext;
import hu.fordprog.regx.grammar.RegxParser.AssignmentExpressionContext;
import hu.fordprog.regx.grammar.RegxParser.BlockContext;
import hu.fordprog.regx.grammar.RegxParser.DeclarationContext;
import hu.fordprog.regx.grammar.RegxParser.DeclarationInitializerContext;
import hu.fordprog.regx.grammar.RegxParser.ForLoopContext;
import hu.fordprog.regx.grammar.RegxParser.FunctionCallExpressionContext;
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
import hu.fordprog.regx.interpreter.symbol.FunctionVisitor;
import hu.fordprog.regx.interpreter.symbol.NativeFunction;
import hu.fordprog.regx.interpreter.symbol.Symbol;
import hu.fordprog.regx.interpreter.symbol.SymbolTable;
import hu.fordprog.regx.interpreter.symbol.SymbolValue;
import hu.fordprog.regx.interpreter.symbol.UserDefinedFunction;

public class CodeExecutor implements FunctionVisitor {
  private static final String DEFAULT_STRING_VALUE = "";

  private final ProgramContext programCtx;

  private final SymbolTable symbolTable;

  private final ParseTreeProperty<Function> functions;

  private final Deque<SymbolValue> returnStack;

  private final SymbolValue voidSymbolValue;

  public CodeExecutor(SymbolTable symbolTable, ProgramContext programCtx) {
    this.programCtx = programCtx;

    this.symbolTable = symbolTable;

    this.functions = new ParseTreeProperty<>();

    this.returnStack = new LinkedList<>();

    this.voidSymbolValue = SymbolValue.from(null);
  }

  public void execute() {
    symbolTable.enterScope(programCtx);

    assignGlobalVariables();

    executeMain();
  }

  private void executeMain() {
    Symbol symbol = symbolTable.getEntry("main").get();

    Function function = (Function)symbol.getSymbolValue().getValue();

    returnStack.addFirst(voidSymbolValue);

    function.accept(this);

    returnStack.removeFirst();
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
      executeFunctionCallExpression((FunctionCallExpressionContext)expression, target);
    }
  }

  private void executeFunctionCallExpression(FunctionCallExpressionContext expression,
                                             SymbolValue target) {
    String identifier = expression.functionCall().identifier().getText();

    Symbol symbol = symbolTable.getEntry(identifier).get();

    Function function = (Function)symbol.getSymbolValue().getValue();

    setFunctionArguments(function, expression.functionCall().argumentList());

    returnStack.addFirst(target);

    function.accept(this);

    returnStack.removeFirst();
  }

  @Override
  public void visit(UserDefinedFunction function) {
    executeFunction(function.getContext());
  }

  @Override
  public void visit(NativeFunction function) {
    function.call(returnStack.peekFirst());
  }

  private void executeFunction(FunctionDeclarationContext context) {
    symbolTable.enterScope(context);

    executeBlock(context.block());

    symbolTable.exitScope();
  }

  private void executeForLoop(ForLoopContext loop) {
    SymbolValue collection = SymbolValue.from(null);

    executeExpression(loop.expression(), collection);

    symbolTable.enterScope(loop);

    Symbol loopVariable = symbolTable.getEntry(loop.identifier().getText()).get();

    RegXList list = (RegXList)collection.getValue();

    for (String str : list) {
      loopVariable.getSymbolValue().setValue(str);

      if (executeBlock(loop.block())) {
        break;
      }
    }

    symbolTable.exitScope();
  }

  private boolean executeBlock(BlockContext block) {
    for (RegxParser.StatementContext statement : block.statement()) {
      if (statement.expression() != null) {
        executeExpression(statement.expression(), voidSymbolValue);
      } else if (statement.returnStatement() != null) {
        executeExpression(statement.returnStatement().expression(), returnStack.peekFirst());

        return true;
      } else if (statement.variableDeclaration() != null) {
        processVariableDeclaration(statement.variableDeclaration());
      } else if (statement.forLoop() != null) {
        executeForLoop(statement.forLoop());
      }
    }

    return false;
  }

  private void setFunctionArguments(Function function, ArgumentListContext arguments) {
    List<Symbol> functionArguments = function.getArguments();

    for (int i = 0; i < functionArguments.size(); ++i) {
      executeExpression(arguments.argument(i).expression(),
                        functionArguments.get(i).getSymbolValue());
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
}
