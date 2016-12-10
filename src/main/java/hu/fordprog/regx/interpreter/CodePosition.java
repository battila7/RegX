package hu.fordprog.regx.interpreter;

import org.antlr.v4.runtime.ParserRuleContext;

public class CodePosition {
  private final int line;

  private final int column;

  public static CodePosition fromContext(ParserRuleContext context) {
    return new CodePosition(context.getStart().getLine(),
                            context.getStart().getCharPositionInLine());
  }

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

  @Override
  public String toString() {
    return "line: " + line + " column: " + column;
  }
}
