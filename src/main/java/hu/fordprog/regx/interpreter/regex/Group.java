package hu.fordprog.regx.interpreter.regex;

public class Group implements Atom {
  private Union child;

  public Group() {
    this.child = null;
  }

  public Group(Union child) {
    this.child = child;
  }

  public Union getChild() {
    return child;
  }

  public void setChild(Union child) {
    this.child = child;
  }

  @Override
  public Automaton makeAutomaton(){
    return child.makeAutomaton();
  }

  @Override
  public String asText() {
    return "(" + child.asText() + ")";
  }
}
