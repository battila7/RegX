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
  public Automaton makeAutomaton(){
    return child.makeAutomaton();
  }

  @Override
  public Regex normalize(){
    return new Term((Atom)child.normalize());
  }

  @Override
  public Regex simplify() {
    return new Term((Atom) child.simplify());
  }

  @Override
  public String asText() {
    return child.asText();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Term term = (Term) o;

    return getChild().equals(term.getChild());

  }

  @Override
  public int hashCode() {
    return getChild().hashCode();
  }
}
