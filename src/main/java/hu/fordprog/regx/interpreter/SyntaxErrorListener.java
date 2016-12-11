package hu.fordprog.regx.interpreter;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SyntaxErrorListener extends BaseErrorListener {
  private final List<SyntaxError> syntaxErrors;

  public SyntaxErrorListener() {
    this.syntaxErrors = new ArrayList<>();
  }

  @Override
  public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                          int charPositionInLine, String msg, RecognitionException e) {
    syntaxErrors.add(new SyntaxError(recognizer, offendingSymbol,
                                     new CodePosition(line, charPositionInLine), msg, e));
  }

  public List<SyntaxError> getSyntaxErrors() {
    return Collections.unmodifiableList(syntaxErrors);
  }
}
