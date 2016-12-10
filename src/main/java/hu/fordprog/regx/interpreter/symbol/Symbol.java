package hu.fordprog.regx.interpreter.symbol;

import hu.fordprog.regx.interpreter.CodePosition;

public class Symbol {
  private final String identifier;

  private final SymbolType symbolType;

  private final CodePosition firstOccurrence;

  private final SymbolValue<?> symbolValue;

  public Symbol(String identifier, SymbolType symbolType,
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
