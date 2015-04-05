package org.oakgp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oakgp.node.VariableNode;

public final class VariableSet {
   private final VariableNode[] variables;
   private final Map<Type, List<VariableNode>> variablesByType;

   public VariableSet(Type... variableTypes) {
      variables = new VariableNode[variableTypes.length];
      variablesByType = new HashMap<>();
      for (int i = 0; i < variableTypes.length; i++) {
         Type type = variableTypes[i];
         VariableNode v = new VariableNode(i, type);
         variables[i] = v;
         put(variablesByType, type, v);
      }
   }

   // TODO generify this logic and reuse elsewhere
   private void put(Map<Type, List<VariableNode>> map, Type key, VariableNode value) {
      List<VariableNode> list = map.get(key);
      if (list == null) {
         list = new ArrayList<>();
         map.put(key, list);
      }
      list.add(value);
   }

   public VariableNode getById(int id) {
      return variables[id];
   }

   // TODO return immutable version
   public List<VariableNode> getByType(Type type) {
      return variablesByType.get(type);
   }
}
