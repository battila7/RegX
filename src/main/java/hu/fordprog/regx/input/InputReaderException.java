package hu.fordprog.regx.input;

public class InputReaderException extends Exception {
  public InputReaderException(Throwable cause) {
    super(cause);
  }

  public InputReaderException(String message, Throwable cause) {
    super(message, cause);
  }

  public InputReaderException(String message) {
    super(message);
  }
}
