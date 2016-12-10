package hu.fordprog.regx.interpreter.symbol;

public class SymbolValue<T> {
  private T value;

  public SymbolValue(T value) {
    this.value = value;
  }

  public T getValue() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }
}
