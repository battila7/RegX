package hu.fordprog.regx.interpreter.regex;

public interface Atom extends Regex {
  Automaton makeAutomaton();
}
