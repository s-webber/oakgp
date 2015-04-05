package org.oakgp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oakgp.node.Node;

abstract class AbstractTerminalSet<T extends Node> {
   private final Map<Type, List<T>> nodesByType;

   AbstractTerminalSet(T[] nodes) {
      nodesByType = new HashMap<>();
      for (T n : nodes) {
         put(nodesByType, n.getType(), n);
      }
   }

   // TODO generify this logic and reuse elsewhere
   private void put(Map<Type, List<T>> map, Type key, T value) {
      List<T> list = map.get(key);
      if (list == null) {
         list = new ArrayList<>();
         map.put(key, list);
      }
      list.add(value);
   }

   // TODO return immutable version
   public List<T> getByType(Type type) {
      return nodesByType.get(type);
   }
}
