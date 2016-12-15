package hu.fordprog.regx.interpreter.regex;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Concatenation implements Regex {
  private final List<Term> children;

  public Concatenation() {
    children = new ArrayList<>();
  }

  public Concatenation(Term term){
    children = new ArrayList<>();
    children.add(term);
  }

  public List<Term> getChildren() {
    return children;
  }

  public void addUnionChild(Union union){
    children.add(new Term(new Group(union)));
  }

  public void addConcatenationChild(Concatenation concatenation){
    addUnionChild(new Union(concatenation));
  }

  @Override
  public Automaton makeAutomaton() {
    Automaton concatenationAutomaton = new Automaton();

    concatenationAutomaton.setStartState(1);

    Set<Integer> currentAcc = null;
    for(Term term : children){

      Automaton childAutomaton = term.makeAutomaton();

      if(currentAcc == null){
        concatenationAutomaton.setStateTransitionTable(childAutomaton.getStateTransitionTable());

        currentAcc = new HashSet<>();
        currentAcc.addAll(childAutomaton.getAcceptStates());

      }else{
        Automaton shiftedAutomaton =
            childAutomaton.getShiftedAutomaton(concatenationAutomaton.getNextIdForNewState()-1);

        concatenationAutomaton.getStateTransitionTable()
            .addAll(shiftedAutomaton.getStateTransitionTable());

        //link the previous acc states to the current start state

        for(Integer acc : currentAcc){

          concatenationAutomaton.addNewStateTransition(acc, "\\", shiftedAutomaton.getStartState());
        }

        //reset the current acc states
        currentAcc.clear();
        currentAcc.addAll(shiftedAutomaton.getAcceptStates());

      }
    }

    //the acc states are the last child automaton's acc states
    concatenationAutomaton.setAcceptStates(currentAcc);

    return concatenationAutomaton;
  }

  @Override
  public Regex normalize() {
    Concatenation concat = new Concatenation();

    for (Term child : children){
      Term term = (Term)child.normalize();
      concat.getChildren().add(term);
    }

    return RegexUnionNormForm.normalize(concat);
  }

  @Override
  public Regex simplify() {
    Concatenation concat = new Concatenation();

    for (Term child : children){
      Term term = (Term)child.simplify();
      concat.getChildren().add(term);
    }

    return concat;
  }

  @Override
  public String asText() {
    return children.stream().map(Regex::asText).collect(joining());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Concatenation that = (Concatenation) o;

    return getChildren().equals(that.getChildren());

  }

  @Override
  public int hashCode() {
    return getChildren().hashCode();
  }
}
