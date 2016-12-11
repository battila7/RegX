package hu.fordprog.regx.interpreter.regex;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;

public class Union implements Regex {
  private final List<Concatenation> children;

  public Union() {
    children = new ArrayList<>();
  }

  public List<Concatenation> getChildren() {
    return children;
  }

  @Override
  public String asText() {
    return children.stream().map(Regex::asText).collect(joining("+"));
  }
}
