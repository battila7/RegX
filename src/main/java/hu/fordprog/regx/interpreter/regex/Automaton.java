package hu.fordprog.regx.interpreter.regex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Automaton {

  private Map<Integer, Map<String, Integer>> stateTransitionTable;

  private List<Integer> acceptStates;

  private Integer startState;

  public Automaton() {
    stateTransitionTable = new HashMap<>();
  }

  public Map<Integer, Map<String, Integer>> getStateTransitionTable() {
    return stateTransitionTable;
  }

  public List<Integer> getAcceptStates() {
    return acceptStates;
  }

  public Integer getStartState() {
    return startState;
  }

  public void setStateTransitionTable(
      Map<Integer, Map<String, Integer>> stateTransitionTable) {
    this.stateTransitionTable = stateTransitionTable;
  }

  public void setAcceptStates(List<Integer> acceptStates) {
    this.acceptStates = acceptStates;
  }

  public void setStartState(Integer startState) {
    this.startState = startState;
  }

  public void addNewAcceptState(Integer newAccState){
    acceptStates.add(newAccState);
  }

  public void addNewStateTransition(Integer from, String str, Integer to){
    if(stateTransitionTable.containsKey(from)){
      if(stateTransitionTable.get(from).containsKey(str)){
        System.out.println("Multiple states to go with the same character!");
      }else{
        stateTransitionTable.get(from).put(str, to);
      }
    }else{
      Map<String, Integer> transition = new HashMap<>();
      transition.put(str, to);
      stateTransitionTable.put(from, transition);
    }
  }

  public Automaton getShiftedAutomaton(int shift){
    Map<Integer, Map<String, Integer>> shiftedMap = new HashMap<>();

    for(Map.Entry<Integer, Map<String, Integer>> entry : stateTransitionTable.entrySet()){

      Map<String, Integer> shiftedTransitionMap = new HashMap<>();

      for(Map.Entry<String, Integer> entry1 : entry.getValue().entrySet()){
        shiftedTransitionMap.put(entry1.getKey(), entry1.getValue() + shift);
      }
      shiftedMap.put(entry.getKey() + shift, shiftedTransitionMap);
    }

    Automaton shiftedAutomaton = new Automaton();
    shiftedAutomaton.setStartState(this.startState + shift);
    shiftedAutomaton.setStateTransitionTable(shiftedMap);
    for(Integer acc : acceptStates){
      shiftedAutomaton.addNewAcceptState(acc + shift);
    }
    return shiftedAutomaton;
  }

  public Integer getNextIdForNewState(){
    Integer id = 1;
    while(stateTransitionTable.containsKey(id)){
      id++;
    }
    return id;
  }

  @Override
  public String toString(){
    StringBuilder sb = new StringBuilder();
    sb.append("Automaton:\n");
    sb.append("--------");

    for(Integer state : stateTransitionTable.keySet()){
      for(String character : stateTransitionTable.get(state).keySet()){
        sb.append(state).append(" | ").append(character).append(" | ")
            .append(stateTransitionTable.get(state).get(character)).append("\n");
        sb.append("--------");
      }
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
