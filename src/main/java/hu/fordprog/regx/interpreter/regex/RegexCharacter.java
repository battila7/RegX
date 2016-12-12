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
  public String asText() {
    return character;
  }
}
