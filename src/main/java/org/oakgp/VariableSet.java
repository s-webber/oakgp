package org.oakgp;

import org.oakgp.node.VariableNode;

/** Represents the range of possible variables to use during a genetic programming run. */
public final class VariableSet extends AbstractTerminalSet<VariableNode> {
   private final VariableNode[] variables;

   public static VariableSet createVariableSet(Type... variableTypes) {
      VariableNode[] variables = new VariableNode[variableTypes.length];
      for (int i = 0; i < variableTypes.length; i++) {
         variables[i] = new VariableNode(i, variableTypes[i]);
      }
      return new VariableSet(variables);
   }

   private VariableSet(VariableNode[] variables) {
      super(variables);
      this.variables = variables;
   }

   public VariableNode getById(int id) {
      return variables[id];
   }
}
