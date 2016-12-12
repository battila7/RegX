package hu.fordprog.regx.interpreter.regex;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Concatenation implements Regex {
  private final List<Term> children;

  public Concatenation() {
    children = new ArrayList<>();
  }

  public List<Term> getChildren() {
    return children;
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

        currentAcc.addAll(childAutomaton.getAcceptStates());
      }else{
        Automaton shiftedAutomaton =
            childAutomaton.getShiftedAutomaton(concatenationAutomaton.getNextIdForNewState());

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

    //the acc states are the last child automatons acc states
    concatenationAutomaton.setAcceptStates(currentAcc);

    return concatenationAutomaton;
  }

  @Override
  public String asText() {
    return children.stream().map(Regex::asText).collect(joining());
  }
}
