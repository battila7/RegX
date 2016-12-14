package hu.fordprog.regx.interpreter.regex;

public interface Regex {
  Automaton makeAutomaton();

  Regex normalize();

  String asText();
}
