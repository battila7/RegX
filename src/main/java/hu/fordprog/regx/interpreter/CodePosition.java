package hu.fordprog.regx.interpreter;

public class CodePosition {
  private final int line;

  private final int column;

  public CodePosition(int line, int column) {
    this.line = line;

    this.column = column;
  }

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }
}
