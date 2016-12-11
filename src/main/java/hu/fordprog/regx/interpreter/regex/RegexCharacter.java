package hu.fordprog.regx.interpreter.regex;

public class RegexCharacter extends Atom {
  private final String character;

  public RegexCharacter(String character) {
    this.character = character;
  }

  public String getCharacter() {
    return character;
  }
}
