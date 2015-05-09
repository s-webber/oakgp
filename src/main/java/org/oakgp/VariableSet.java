package org.oakgp;

import java.util.List;
import java.util.Map;

import org.oakgp.node.VariableNode;
import org.oakgp.util.Utils;

/** Represents the range of possible variables to use during a genetic programming run. */
public final class VariableSet {
   private final Map<Type, List<VariableNode>> variablesByType;
   private final VariableNode[] variables;

   public static VariableSet createVariableSet(Type... variableTypes) {
      VariableNode[] variables = new VariableNode[variableTypes.length];
      for (int i = 0; i < variableTypes.length; i++) {
         variables[i] = new VariableNode(i, variableTypes[i]);
      }
      return new VariableSet(variables);
   }

   private VariableSet(VariableNode[] variables) {
      variablesByType = Utils.groupByType(variables);
      this.variables = variables;
   }

   public List<VariableNode> getByType(Type type) {
      return variablesByType.get(type);
   }

   public VariableNode getById(int id) {
      return variables[id];
   }
}
