package hu.fordprog.regx.interpreter.regex;

public class Term implements Regex {
  private Atom child;

  public Term(Atom child) {
    this.child = child;
  }

  public Term() {
    child = null;
  }

  public Atom getChild() {
    return child;
  }

  public void setChild(Atom child) {
    this.child = child;
  }

  @Override
  public String asText() {
    return child.asText();
  }
}
