package org.oakgp.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oakgp.Type;
import org.oakgp.node.Node;

/** Utility methods that support the functionality provided by the rest of the framework. */
public final class Utils {
   /** Private constructor as all methods are static. */
   private Utils() {
      // do nothing
   }

   public static <T extends Node> Map<Type, List<T>> groupNodesByType(T[] nodes) {
      Map<Type, List<T>> nodesByType = new HashMap<>();
      for (T n : nodes) {
         addToListOfMap(nodesByType, n.getType(), n);
      }
      return nodesByType;
   }

   public static <T> void addToListOfMap(Map<Type, List<T>> map, Type key, T value) {
      List<T> list = map.get(key);
      if (list == null) {
         list = new ArrayList<>();
         map.put(key, list);
      }
      list.add(value);
   }
}
