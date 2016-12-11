package hu.fordprog.regx.interpreter;

import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class SyntaxError {
  private final Recognizer<?, ?> recognizer;

  private final Object offendingSymbol;

  private final CodePosition position;

  private final String message;

  private final RecognitionException recognitionException;

  public SyntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                     CodePosition position, String message,
                     RecognitionException recognitionException) {
    this.recognizer = recognizer;

    this.offendingSymbol = offendingSymbol;

    this.position = position;

    this.message = message;

    this.recognitionException = recognitionException;
  }

  public Recognizer<?, ?> getRecognizer() {
    return recognizer;
  }

  public Object getOffendingSymbol() {
    return offendingSymbol;
  }

  public CodePosition getPosition() {
    return position;
  }

  public String getMessage() {
    return message;
  }

  public RecognitionException getRecognitionException() {
    return recognitionException;
  }

  @Override
  public String toString() {
    return message + " at " + position;
  }
}
