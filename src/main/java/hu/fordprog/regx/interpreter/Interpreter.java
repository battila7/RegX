package hu.fordprog.regx.interpreter;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import hu.fordprog.regx.grammar.RegxLexer;
import hu.fordprog.regx.grammar.RegxParser;
import hu.fordprog.regx.input.InputReader;
import hu.fordprog.regx.input.InputReaderException;
import hu.fordprog.regx.interpreter.error.SemanticError;
import hu.fordprog.regx.interpreter.regex.RegexDeclarations;
import hu.fordprog.regx.interpreter.regex.Union;
import hu.fordprog.regx.interpreter.stdlib.IO;
import hu.fordprog.regx.interpreter.stdlib.ImplicitDeclarationSource;
import hu.fordprog.regx.interpreter.stdlib.RegXList;
import hu.fordprog.regx.interpreter.stdlib.RegXStringDeclarations;
import hu.fordprog.regx.interpreter.symbol.Symbol;

public class Interpreter {
  private InputReader inputReader;

  private PrintWriter outputWriter;

  private boolean verbose;

  private ParseTreeProperty<Union> regularExpressions;

  private final SyntaxErrorListener syntaxErrorListener;

  private final SemanticChecker semanticChecker;

  public static Builder builder() {
    return new Builder();
  }

  private Interpreter() {
    this.semanticChecker = new SemanticChecker(createImplicitDeclarations());

    this.syntaxErrorListener = new SyntaxErrorListener();
  }

  public void interpret() {
    RegxParser.ProgramContext parseTree = null;

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

    CodeExecutor codeExecutor = new CodeExecutor(semanticChecker.getSymbolTable(), parseTree,
        regularExpressions);

    codeExecutor.execute();
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

  private boolean checkSemantics(RegxParser.ProgramContext parseTree) {
    ParseTreeWalker.DEFAULT.walk(semanticChecker, parseTree);

    regularExpressions = semanticChecker.getRegularExpressions();

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

  private RegxParser.ProgramContext obtainParseTree() throws InputReaderException {
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

  private List<Symbol> createImplicitDeclarations() {
    List<ImplicitDeclarationSource> sources =
        Arrays.asList(new IO(), new RegXList.Declarations(), new RegXStringDeclarations(),
                      new RegexDeclarations());

    return sources.stream()
        .map(ImplicitDeclarationSource::getDeclarations)
        .flatMap(List::stream)
        .collect(toList());
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
