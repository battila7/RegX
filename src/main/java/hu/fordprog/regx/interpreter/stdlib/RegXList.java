package hu.fordprog.regx.interpreter.stdlib;

import java.util.LinkedList;

public class RegXList {
  private final LinkedList<String> backingList;

  public RegXList() {
    backingList = new LinkedList<>();
  }

  public void pushBack(String element) {
    backingList.addLast(element);
  }

  public String popBack() {
    return backingList.removeLast();
  }

  public void pushFront(String element) {
    backingList.addFirst(element);
  }

  public String popFront() {
    return backingList.removeFirst();
  }
}
