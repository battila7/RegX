package hu.fordprog.regx.interpreter.regex;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;

public class Union implements Regex {
  private final List<Concatenation> children;

  public Union() {
    children = new ArrayList<>();
  }

  public List<Concatenation> getChildren() {
    return children;
  }

  @Override
  public Automaton makeAutomaton(){
    Automaton unionAutomaton = new Automaton();

    //First state created to start the automaton
    unionAutomaton.setStartState(1);

    //Find all accept states of the child automatons, to make a state transition from them
    //to the newly created acc state of this automaton
    List<Integer> childAcceptStates = new ArrayList<>();

    //All child automaton's state indexes should be shifted to avoid multiple
    //states with the same indexes
    for(Concatenation concatenation : children){
      //Shifting all state indexes
      Automaton childAutomaton = concatenation.makeAutomaton()
          .getShiftedAutomaton(unionAutomaton.getNextIdForNewState() - 1);

      //Create a new entry in the new state transition table with a lambda
      unionAutomaton.addNewStateTransition(1, "\\", childAutomaton.getStartState());

      //collect all acceptStates for later transitions to add
      childAcceptStates.addAll(childAutomaton.getAcceptStates());

      //we can add all the shifted state transitions now to the new table
      unionAutomaton.getStateTransitionTable().addAll(childAutomaton.getStateTransitionTable());
    }

    //all previous acc states should be linked to the new acc state

    Integer accState = unionAutomaton.getNextIdForNewState();

    unionAutomaton.addNewAcceptState(accState);

    for(Integer oldAcc : childAcceptStates){
      unionAutomaton.addNewStateTransition(oldAcc, "\\", accState);
    }

    return unionAutomaton;
  }

  @Override
  public String asText() {
    return children.stream().map(Regex::asText).collect(joining("+"));
  }
}
