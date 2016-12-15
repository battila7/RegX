package hu.fordprog.regx.interpreter.regex;

public interface Regex {
  Automaton makeAutomaton();

  Regex normalize();

  Regex simplify();

  String asText();
}
