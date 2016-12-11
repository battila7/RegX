package hu.fordprog.regx.interpreter.regex;

public class ClosureTerm extends Term {
  public String asText() {
    return super.asText() + "*";
  }
}
