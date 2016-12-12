package hu.fordprog.regx.interpreter.stdlib;

import static hu.fordprog.regx.interpreter.symbol.Symbol.nativeArgument;
import static hu.fordprog.regx.interpreter.symbol.Symbol.nativeFunction;
import static hu.fordprog.regx.interpreter.symbol.Type.FUNCTION;
import static hu.fordprog.regx.interpreter.symbol.Type.LIST;
import static hu.fordprog.regx.interpreter.symbol.Type.STRING;
import static hu.fordprog.regx.interpreter.symbol.Type.VOID;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import hu.fordprog.regx.interpreter.CodePosition;
import hu.fordprog.regx.interpreter.symbol.NativeFunction;
import hu.fordprog.regx.interpreter.symbol.Symbol;
import hu.fordprog.regx.interpreter.symbol.SymbolValue;

public class RegXList extends AbstractList<String> {
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

  @Override
  public String get(int index) {
    return backingList.get(index);
  }

  @Override
  public int size() {
    return backingList.size();
  }

  public static class Declarations implements ImplicitDeclarationSource {
    private static final Object VOID_RETURN_VALUE = null;

    @Override
    public List<Symbol> getDeclarations() {
      List<Symbol> declarations = new ArrayList<>();

      NativeFunction pushBackFn =
          new NativeFunction(asList(nativeArgument("lst", LIST), nativeArgument("str", STRING)),
              VOID, Declarations::pushBack);

      declarations.add(nativeFunction("push_back", pushBackFn));

      NativeFunction popBackFn =
          new NativeFunction(singletonList(nativeArgument("lst", LIST)), STRING, Declarations::popBack);

      declarations.add(nativeFunction("pop_back", popBackFn));

      NativeFunction pushFrontFn =
          new NativeFunction(asList(nativeArgument("lst", LIST), nativeArgument("str", STRING)),
              VOID, Declarations::pushFront);

      declarations.add(nativeFunction("push_front", pushFrontFn));

      NativeFunction popFrontFn =
          new NativeFunction(singletonList(nativeArgument("lst", LIST)), STRING, Declarations::popFront);

      declarations.add(nativeFunction("pop_front", popFrontFn));

      return declarations;
    }

    private static Object pushBack(List<Object> arguments) {
      ((RegXList)arguments.get(0)).pushBack((String)arguments.get(1));

      return VOID_RETURN_VALUE;
    }

    private static Object popBack(List<Object> arguments) {
      return ((RegXList)arguments.get(0)).popBack();
    }

    private static Object pushFront(List<Object> arguments) {
      ((RegXList)arguments.get(0)).pushFront((String)arguments.get(1));

      return VOID_RETURN_VALUE;
    }

    private static Object popFront(List<Object> arguments) {
      return ((RegXList)arguments.get(0)).popFront();
    }
  }
}
