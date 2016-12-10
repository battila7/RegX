package hu.fordprog.regx.interpreter.symbol;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import hu.fordprog.regx.interpreter.CodePosition;

public class SymbolTable {
  private final LinkedList<HashMap<String, Entry>> tableList;

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
  }

  public void addEntry(String identifier, Entry entry) {
    tableList.getFirst().put(identifier, entry);
  }

  public Optional<Entry> getEntry(String identifier) {
    return tableList.stream()
        .map(m -> m.get(identifier))
        .filter(e -> e != null)
        .findFirst();
  }

  public static class Entry {
    private final String identifier;

    private final SymbolType symbolType;

    private final CodePosition firstOccurrence;

    private final SymbolValue<?> symbolValue;

    public Entry(String identifier, SymbolType symbolType,
                 CodePosition firstOccurrence, SymbolValue<?> symbolValue) {
      this.identifier = identifier;

      this.symbolType = symbolType;

      this.firstOccurrence = firstOccurrence;

      this.symbolValue = symbolValue;
    }

    public String getIdentifier() {
      return identifier;
    }

    public SymbolType getSymbolType() {
      return symbolType;
    }

    public CodePosition getFirstOccurrence() {
      return firstOccurrence;
    }

    public SymbolValue<?> getSymbolValue() {
      return symbolValue;
    }
  }
}
