package hu.fordprog.regx.interpreter.regex;

public class ClosureTerm extends Term {
  public ClosureTerm(Atom child) {
    super(child);
  }

  @Override
  public Automaton makeAutomaton(){
    Automaton closureAutomaton = new Automaton();

    Automaton childAutomaton = getChild().makeAutomaton();

    Automaton shiftedAutomaton = childAutomaton.getShiftedAutomaton(1);

    closureAutomaton.setStartState(1);

    //All original transitions stay in the new automaton
    closureAutomaton.setStateTransitionTable(shiftedAutomaton.getStateTransitionTable());

    //The previous acc states are linked to the previous start state
    for(Integer acc : shiftedAutomaton.getAcceptStates()){

      closureAutomaton
          .addNewStateTransition(acc, "\\", shiftedAutomaton.getStartState());

    }

    //the new start state is linked to the previous start state
    closureAutomaton.addNewStateTransition(1, "\\", shiftedAutomaton.getStartState());

    Integer newAccState = closureAutomaton.getNextIdForNewState();

    closureAutomaton.addNewAcceptState(newAccState);

    //link the previous acc states to the new acc state
    for(Integer acc : shiftedAutomaton.getAcceptStates()){

      closureAutomaton
          .addNewStateTransition(acc, "\\", newAccState);
    }

    //link the new start state to the new acc state
    closureAutomaton.addNewStateTransition(1, "\\", newAccState);

    return closureAutomaton;
  }

  public String asText() {
    return super.asText() + "*";
  }
}
