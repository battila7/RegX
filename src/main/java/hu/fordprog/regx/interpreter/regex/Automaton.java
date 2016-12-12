package hu.fordprog.regx.interpreter.regex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Automaton {

  private List<StateTransition> stateTransitionTable;

  private Set<Integer> acceptStates;

  private Integer startState;

  public Automaton() {
    stateTransitionTable = new ArrayList<>();
    acceptStates = new HashSet<>();
  }

  public List<StateTransition> getStateTransitionTable() {
    return stateTransitionTable;
  }

  public Set<Integer> getAcceptStates() {
    return acceptStates;
  }

  public Integer getStartState() {
    return startState;
  }

  public void setStateTransitionTable(List<StateTransition> stateTransitionTable) {
    this.stateTransitionTable = stateTransitionTable;
  }

  public void setAcceptStates(Set<Integer> acceptStates) {
    this.acceptStates = acceptStates;
  }

  public void setStartState(Integer startState) {
    this.startState = startState;
  }

  public void addNewAcceptState(Integer newAccState){
    acceptStates.add(newAccState);
  }

  public void addNewStateTransition(Integer from, String str, Integer to){
    stateTransitionTable.add(new StateTransition(from, str, to));
  }

  public Automaton getShiftedAutomaton(int shift){
    List<StateTransition> shiftedTable = new ArrayList<>();

    for(StateTransition entry : this.stateTransitionTable){
      shiftedTable
          .add(new StateTransition(entry.getFrom() + shift, entry.getWith(), entry.getTo() + shift));
    }

    Automaton shiftedAutomaton = new Automaton();
    shiftedAutomaton.setStartState(this.startState + shift);
    shiftedAutomaton.setStateTransitionTable(shiftedTable);
    for(Integer acc : acceptStates){
      shiftedAutomaton.addNewAcceptState(acc + shift);
    }
    return shiftedAutomaton;
  }

  public Integer getNextIdForNewState(){
    int max = stateTransitionTable.stream()
        .mapToInt(t -> t.getFrom())
        .max().getAsInt();

    if(max == 0){
      return 2;
    }else{
      return max + 1;
    }
  }

  @Override
  public String toString(){
    StringBuilder sb = new StringBuilder();
    sb.append("Automaton:\n");
    sb.append("--------");

    for(StateTransition entry : stateTransitionTable){
        sb.append(entry.toString());
        sb.append("--------");
    }

    sb.append("Starting state: ").append(startState).append("\n");
    sb.append("Accepting states: ");
    for(Integer i : acceptStates){
      sb.append(i).append(" ");
    }
    sb.append("\n");

    return sb.toString();
  }
}
