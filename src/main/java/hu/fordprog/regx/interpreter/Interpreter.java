package hu.fordprog.regx.interpreter;

import java.io.PrintWriter;
import java.util.Objects;
import hu.fordprog.regx.input.InputReader;

public class Interpreter {
  private InputReader inputReader;

  private PrintWriter outputWriter;

  private boolean verbose;

  public static Builder builder() {
    return new Builder();
  }

  private Interpreter() {
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
