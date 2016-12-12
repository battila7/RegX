package hu.fordprog.regx.interpreter.regex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Automaton {

  private Map<Integer, Map<RegexCharacter, Integer>> stateTransitionTable;

  private List<Integer> acceptState;

  private Integer startState;

  public Automaton() {
    stateTransitionTable = new HashMap<>();
  }

  public Map<Integer, Map<RegexCharacter, Integer>> getStateTransitionTable() {
    return stateTransitionTable;
  }

  public List<Integer> getAcceptState() {
    return acceptState;
  }

  public Integer getStartState() {
    return startState;
  }

  public void setStartState(Integer startState) {
    this.startState = startState;
  }

  public void addNewAcceptState(Integer newAccState){
    acceptState.add(newAccState);
  }

  public void addNewStateTransition(Integer from, RegexCharacter c, Integer to){
    if(stateTransitionTable.containsKey(from)){
      if(stateTransitionTable.get(from).containsKey(c)){
        System.out.println("Multiple states to go with the same character!");
      }else{
        stateTransitionTable.get(from).put(c, to);
      }
    }else{
      Map<RegexCharacter, Integer> transition = new HashMap<>();
      transition.put(c, to);
      stateTransitionTable.put(from, transition);
    }
  }

  @Override
  public String toString(){
    StringBuilder sb = new StringBuilder();
    sb.append("Automaton:\n---------");

    for(Integer state : stateTransitionTable.keySet()){
      for(RegexCharacter character : stateTransitionTable.get(state).keySet()){
        sb.append(state + " | " + character + " | " + stateTransitionTable.get(state).get(character) + "\n");
      }
    }
    return sb.toString();
  }
}
