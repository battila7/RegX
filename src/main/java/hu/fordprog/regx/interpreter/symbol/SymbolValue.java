package hu.fordprog.regx.interpreter.symbol;

public class SymbolValue<T> {
  private T value;

  public static <T> SymbolValue<T> from(T value) {
    return new SymbolValue<>(value);
  }

  private SymbolValue(T value) {
    this.value = value;
  }

  public T getValue() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }
}
