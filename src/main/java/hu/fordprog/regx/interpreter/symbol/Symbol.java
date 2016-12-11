package hu.fordprog.regx.interpreter.symbol;

import hu.fordprog.regx.interpreter.CodePosition;

public class Symbol {
  private final String identifier;

  private final Type type;

  private final CodePosition firstOccurrence;

  private final SymbolValue symbolValue;

  public Symbol(String identifier, Type type,
                CodePosition firstOccurrence, SymbolValue symbolValue) {
    this.identifier = identifier;

    this.type = type;

    this.firstOccurrence = firstOccurrence;

    this.symbolValue = symbolValue;
  }

  public String getIdentifier() {
    return identifier;
  }

  public Type getType() {
    return type;
  }

  public CodePosition getFirstOccurrence() {
    return firstOccurrence;
  }

  public SymbolValue getSymbolValue() {
    return symbolValue;
  }
}
