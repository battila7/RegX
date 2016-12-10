package hu.fordprog.regx.interpreter;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.w3c.dom.traversal.TreeWalker;

import java.io.PrintWriter;
import java.util.Objects;
import hu.fordprog.regx.grammar.RegxLexer;
import hu.fordprog.regx.grammar.RegxParser;
import hu.fordprog.regx.input.InputReader;
import hu.fordprog.regx.input.InputReaderException;
import hu.fordprog.regx.input.StdinInputReader;

public class Interpreter {
  private InputReader inputReader;

  private PrintWriter outputWriter;

  private boolean verbose;

  public static Builder builder() {
    return new Builder();
  }

  private Interpreter() {

  }

  public void interpret() {
    ParseTree parseTree = null;

    try {
      parseTree = obtainParseTree();
    } catch (InputReaderException e) {
      outputWriter.write("Could not read input: " + e);
    }

    checkSemantics(parseTree);
  }

  private void checkSemantics(ParseTree parseTree) {
    SemanticChecker semanticChecker = new SemanticChecker();

    ParseTreeWalker.DEFAULT.walk(semanticChecker, parseTree);

    outputWriter.println(semanticChecker.getErrors());
  }

  private ParseTree obtainParseTree() throws InputReaderException {
    String input = inputReader.readInput();

    RegxLexer lexer = new RegxLexer(new ANTLRInputStream(input));

    TokenStream tokenStream = new CommonTokenStream(lexer);

    RegxParser parser = new RegxParser(tokenStream);

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
