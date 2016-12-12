package hu.fordprog.regx.interpreter.regex;

public class ClosureTerm extends Term {
  public ClosureTerm(Atom child) {
    super(child);
  }

  public String asText() {
    return super.asText() + "*";
  }
}
