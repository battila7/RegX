package hu.fordprog.regx.interpreter.regex;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;

public class Concatenation implements Regex {
  private final List<Term> children;

  public Concatenation() {
    children = new ArrayList<>();
  }

  public List<Term> getChildren() {
    return children;
  }

  @Override
  public String asText() {
    return children.stream().map(Regex::asText).collect(joining());
  }
}
