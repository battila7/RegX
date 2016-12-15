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
  public Regex normalize(){
    return new Group((Union)child.normalize());
  }

  @Override
  public Regex simplify() {
    return RegexSimplifier.simplify(new Group((Union)child.simplify()));
  }

  @Override
  public String asText() {
    return "(" + child.asText() + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Group group = (Group) o;

    return getChild().equals(group.getChild());

  }

  @Override
  public int hashCode() {
    return getChild().hashCode();
  }
}
