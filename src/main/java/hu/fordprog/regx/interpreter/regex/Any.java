package hu.fordprog.regx.interpreter.regex;

public class Any implements Atom {
  @Override
  public String asText() {
    return ".";
  }

  @Override
  public Automaton makeAutomaton() {
    return null;
  }

  @Override
  public Regex normalize() {
    return this;
  }

  @Override
  public Regex simplify() {
    return null;
  }
}
