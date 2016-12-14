package hu.fordprog.regx.interpreter.regex;

public class RegexCharacter implements Atom {
  private final String character;

  public RegexCharacter(String character) {
    this.character = character;
  }

  public String getCharacter() {
    return character;
  }

  @Override
  public Automaton makeAutomaton(){
    Automaton automaton = new Automaton();

    automaton.setStartState(1);
    automaton.addNewStateTransition(1, character, 2);
    automaton.addNewAcceptState(2);

    return automaton;
  }

  @Override
  public Regex normalize() {
    return this;
  }

  @Override
  public String asText() {
    return character;
  }
}
