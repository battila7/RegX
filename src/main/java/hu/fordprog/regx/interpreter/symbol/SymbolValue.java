package hu.fordprog.regx.interpreter.symbol;

public class SymbolValue {
  private Object value;

  public static SymbolValue from(Object value) {
    return new SymbolValue(value);
  }

  private SymbolValue(Object value) {
    this.value = value;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }
}
