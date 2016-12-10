package hu.fordprog.regx.interpreter.symbol;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import hu.fordprog.regx.interpreter.CodePosition;

public class SymbolTable {
  private final LinkedList<HashMap<String, Symbol>> tableList;

  public SymbolTable() {
    this.tableList = new LinkedList<>();
  }

  public void newScope() {
    tableList.addFirst(new HashMap<>());
  }

  public void destroyScope() {
    if (tableList.isEmpty()) {
      throw new IllegalStateException("The Symbol Table is empty!");
    }

    tableList.poll();
  }

  public void addEntry(Symbol symbol) {
    tableList.getFirst().put(symbol.getIdentifier(), symbol);
  }

  public Optional<Symbol> getEntry(String identifier) {
    return tableList.stream()
        .map(m -> m.get(identifier))
        .filter(e -> e != null)
        .findFirst();
  }

  public Optional<Symbol> getEntryFromCurrentScope(String identifier) {
    return Optional.ofNullable(tableList.peek().get(identifier));
  }
}
