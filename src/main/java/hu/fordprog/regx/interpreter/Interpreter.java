package hu.fordprog.regx.interpreter;

import static java.util.stream.Collectors.joining;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.w3c.dom.traversal.TreeWalker;

import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import hu.fordprog.regx.grammar.RegxLexer;
import hu.fordprog.regx.grammar.RegxParser;
import hu.fordprog.regx.input.InputReader;
import hu.fordprog.regx.input.InputReaderException;
import hu.fordprog.regx.input.StdinInputReader;
import hu.fordprog.regx.interpreter.error.SemanticError;

public class Interpreter {
  private InputReader inputReader;

  private PrintWriter outputWriter;

  private boolean verbose;

  private final SyntaxErrorListener syntaxErrorListener;

  public static Builder builder() {
    return new Builder();
  }

  private Interpreter() {
    this.syntaxErrorListener = new SyntaxErrorListener();
  }

  public void interpret() {
    ParseTree parseTree = null;

    try {
      parseTree = obtainParseTree();
    } catch (InputReaderException e) {
      outputWriter.println("Could not read input: " + e);
    }

    boolean syntacticResult = checkSyntax();
    boolean semanticResult = checkSemantics(parseTree);

    if (!(syntacticResult && semanticResult)) {
      outputWriter.println("\n\nAborting because of the listed syntactic/semantic errors...");

      return;
    }
  }

  private boolean checkSyntax() {
    List<SyntaxError> syntaxErrors = syntaxErrorListener.getSyntaxErrors();

    if (syntaxErrors.isEmpty()) {
      return true;
    }

    printSyntaxErrors(syntaxErrors);

    return false;
  }

  private void printSyntaxErrors(List<SyntaxError> syntaxErrors) {
    outputWriter.printf("Found %d syntax errors while parsing your code:\n\n", syntaxErrors.size());

    String errorOut = syntaxErrors.stream()
        .map(SyntaxError::toString)
        .collect(joining("\n\n"));

    outputWriter.println(errorOut);
  }

  private boolean checkSemantics(ParseTree parseTree) {
    SemanticChecker semanticChecker = new SemanticChecker();

    ParseTreeWalker.DEFAULT.walk(semanticChecker, parseTree);

    List<SemanticError> semanticErrors = semanticChecker.getErrors();

    if (!semanticErrors.isEmpty()) {
      printSemanticErrors(semanticErrors);
    }

    return semanticErrors.isEmpty();
  }

  private void printSemanticErrors(List<SemanticError> semanticErrors) {
    outputWriter.printf("\nFound %d errors when checking the semantics:\n\n", semanticErrors.size());

    String errorOut = semanticErrors.stream()
        .map(SemanticError::getMessage)
        .collect(joining("\n\n"));

    outputWriter.println(errorOut);
  }

  private ParseTree obtainParseTree() throws InputReaderException {
    String input = inputReader.readInput();

    RegxLexer lexer = new RegxLexer(new ANTLRInputStream(input));

    lexer.removeErrorListeners();
    lexer.addErrorListener(syntaxErrorListener);

    TokenStream tokenStream = new CommonTokenStream(lexer);

    RegxParser parser = new RegxParser(tokenStream);

    parser.removeErrorListeners();
    parser.addErrorListener(syntaxErrorListener);

    return parser.program();
  }

  public static class Builder {
    private final Interpreter interpreter;

    public Builder() {
      this.interpreter = new Interpreter();
    }

    public Builder verbose(boolean v) {
      interpreter.verbose = v;

      return this;
    }

    public Builder inputReader(InputReader reader) {
      interpreter.inputReader = reader;

      return this;
    }

    public Builder outputWriter(PrintWriter writer) {
      interpreter.outputWriter = writer;

      return this;
    }

    public Interpreter build() {
      Objects.requireNonNull(interpreter.inputReader);
      Objects.requireNonNull(interpreter.outputWriter);

      return interpreter;
    }
  }
}
