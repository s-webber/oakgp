package org.oakgp.util;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.oakgp.Type;
import org.oakgp.node.Node;

/** Utility methods that support the functionality provided by the rest of the framework. */
public final class Utils {
   /** Private constructor as all methods are static. */
   private Utils() {
      // do nothing
   }

   public static <T extends Node> Map<Type, List<T>> groupNodesByType(T[] nodes) {
      return groupValuesByKey(nodes, Node::getType);
   }

   public static <K, V> Map<K, List<V>> groupValuesByKey(V[] values, Function<V, K> valueToKey) {
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
}
