package org.oakgp;

import java.util.List;
import java.util.Map;

import org.oakgp.node.Node;
import org.oakgp.util.Utils;

abstract class AbstractTerminalSet<T extends Node> {
   private final Map<Type, List<T>> nodesByType;

   AbstractTerminalSet(T[] nodes) {
      nodesByType = Utils.groupNodesByType(nodes);
   }

   // TODO return immutable version
   public List<T> getByType(Type type) {
      return nodesByType.get(type);
   }
}
