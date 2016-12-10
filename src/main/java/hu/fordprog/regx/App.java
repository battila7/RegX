package hu.fordprog.regx;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class App {
  public static void main(String[] args) {
    Arguments arguments = new Arguments();

    JCommander jCommander = new JCommander(arguments, args);

    startInterpeter(arguments);
  }

  private static void startInterpeter(Arguments arguments) {

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
