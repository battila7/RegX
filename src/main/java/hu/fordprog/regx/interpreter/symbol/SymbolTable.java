package hu.fordprog.regx.interpreter.symbol;

import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SymbolTable {
  private static final TableNode NO_PARENT = null;

  private final TableNode rootNode;

  private final Map<ParserRuleContext, TableNode> nodeMap;

  private TableNode currentNode;

  public SymbolTable(ParserRuleContext rootContext, List<Symbol> implicitDeclarations) {
    rootNode = new TableNode(NO_PARENT, rootContext);

    nodeMap = new IdentityHashMap<>();

    nodeMap.put(rootContext, rootNode);

    currentNode = rootNode;

    implicitDeclarations.forEach(this::addEntry);
  }

  public void enterScope(ParserRuleContext context) {
    currentNode = nodeMap.computeIfAbsent(context, c -> new TableNode(currentNode, context));
  }

  public void exitScope() {
    if (currentNode == rootNode) {
      throw new IllegalStateException("The current scope has no parent scope!");
    }

    currentNode = currentNode.parentNode;
  }

  public ParserRuleContext getCurrentScope() {
    return currentNode.context;
  }

  public void addEntry(Symbol symbol) {
    currentNode.symbolMap.put(symbol.getIdentifier(), symbol);
  }

  public Optional<Symbol> getEntry(String identifier) {
    TableNode node = currentNode;
    Optional<Symbol> symbol = Optional.empty();

    do {
      symbol = node.getEntry(identifier);

      if (symbol.isPresent()) {
        break;
      }

      node = node.parentNode;
    } while (node != null);

    return symbol;
  }

  public Optional<Symbol> getEntryFromCurrentScope(String identifier) {
    return currentNode.getEntry(identifier);
  }

  private static class TableNode {
    private final Map<String, Symbol> symbolMap;

    private final ParserRuleContext context;

    private final TableNode parentNode;

    public TableNode(TableNode parentNode, ParserRuleContext context) {
      this.parentNode = parentNode;

      this.context = context;

      this.symbolMap = new HashMap<>();
    }

    public Optional<Symbol> getEntry(String identifier) {
      return Optional.ofNullable(symbolMap.get(identifier));
    }
  }
}
