package hu.fordprog.regx.input;

public class ConstantInputReader implements InputReader {
  private final String contents;

  public ConstantInputReader(String contents) {
    this.contents = contents;
  }

  @Override
  public String readInput() throws InputReaderException {
    return contents;
  }
}
