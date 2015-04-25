package org.oakgp.util;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.unmodifiableList;
import static org.oakgp.Type.booleanType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import org.oakgp.Type;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;

/** Utility methods that support the functionality provided by the rest of the framework. */
public final class Utils {
   public static final ConstantNode TRUE_NODE = new ConstantNode(TRUE, booleanType());
   public static final ConstantNode FALSE_NODE = new ConstantNode(FALSE, booleanType());

   /** Private constructor as all methods are static. */
   private Utils() {
      // do nothing
   }

   public static <T extends Node> Map<Type, List<T>> groupByType(T[] nodes) {
      return groupBy(nodes, Node::getType);
   }

   public static <K, V> Map<K, List<V>> groupBy(V[] values, Function<V, K> valueToKey) {
      Map<K, List<V>> nodesByType = new HashMap<>();
      for (V v : values) {
         addToListOfMap(nodesByType, valueToKey.apply(v), v);
      }
      makeValuesImmutable(nodesByType);
      return nodesByType;
   }

   private static <K, V> void addToListOfMap(Map<K, List<V>> map, K key, V value) {
      List<V> list = map.get(key);
      if (list == null) {
         list = new ArrayList<>();
         map.put(key, list);
      }
      list.add(value);
   }

   private static <K, V> void makeValuesImmutable(Map<K, List<V>> map) {
      for (Map.Entry<K, List<V>> e : map.entrySet()) {
         map.put(e.getKey(), unmodifiableList(e.getValue()));
      }
   }

   public static int selectSubNodeIndex(Node tree, Random random) {
      int nodeCount = tree.getNodeCount();
      if (nodeCount == 1) {
         // will get here if and only if 'tree' is a terminal (i.e. variable or constant) rather than a function node
         return 0;
      } else {
         // Note: -1 to avoid selecting root node
         return random.nextInt(nodeCount - 1);
      }
   }

   public static int selectSubNodeIndex(Node tree, Random random, Predicate<Node> treeWalkerStrategy) {
      int nodeCount = tree.getNodeCount(treeWalkerStrategy);
      // TODO what if nodeCount==0
      if (nodeCount == 1) {
         return 0;
      } else {
         // Note: -1 to avoid selecting root node
         return random.nextInt(nodeCount - 1);
      }
   }

   public static boolean areFunctions(Node n1, Node n2) {
      return isFunction(n1) && isFunction(n2);
   }

   public static boolean isFunction(Node n) {
      return n.getNodeType().isFunction();
   }

   public static boolean areTerminals(Node n1, Node n2) {
      return n1.getNodeType().isTerminal() && n2.getNodeType().isTerminal();
   }

   public static boolean isTerminal(Node n) {
      return n.getNodeType().isTerminal();
   }

   public static boolean isConstant(Node n) {
      return n.getNodeType().isConstant();
   }

   public static boolean isVariable(Node n) {
      return n.getNodeType().isVariable();
   }
}
