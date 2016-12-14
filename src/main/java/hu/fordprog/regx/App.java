package hu.fordprog.regx;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import hu.fordprog.regx.input.ConstantInputReader;
import hu.fordprog.regx.input.FileInputReader;
import hu.fordprog.regx.input.InputReader;
import hu.fordprog.regx.input.StdinInputReader;
import hu.fordprog.regx.interpreter.Interpreter;

public class App {
  /*
   * Remove in release
   */
  private static final boolean DEBUG = true;

  public static void main(String[] args) {
    Arguments arguments = new Arguments();

    JCommander jCommander = new JCommander(arguments, args);

    try {
      startInterpreter(arguments);
    } catch (FileNotFoundException e) {
      System.out.println("Could not open output file: " + e.toString());

      System.out.println("Aborting");
    }
  }

  private static void startInterpreter(Arguments arguments) throws FileNotFoundException{
    Interpreter interpreter =
        Interpreter.builder()
                   .inputReader(getInput(arguments))
                   .outputWriter(getOutput(arguments))
                   .verbose(arguments.verbose)
                   .build();

    interpreter.interpret();
  }

  private static PrintWriter getOutput(Arguments arguments) throws FileNotFoundException {
    if (arguments.outputPath == null) {
      return new PrintWriter(System.out, true);
    }

    return new PrintWriter(arguments.outputPath);
  }

  private static InputReader getInput(Arguments arguments) {
    /*
     * Remove in release
     */

    if (DEBUG) {
      String str =
      "function void main() { regex asd = /((ab+c*)d*)*/;"
          + " regex b = normalize(asd); print(asText(b));}";

      return new ConstantInputReader(str);
    }

    if (arguments.files.isEmpty()) {
      return new StdinInputReader();
    }

    return FileInputReader.fromPath(arguments.files.get(0));
  }

  private static final class Arguments {
    @Parameter(description = "Files to be interpreted")
    private List<String> files = new ArrayList<>();

    @Parameter(names = { "--verbose", "-v" }, description = "Verbose output")
    private boolean verbose;

    @Parameter(names = { "--output", "-o" }, description = "Output file path")
    private String outputPath;
  }
}
