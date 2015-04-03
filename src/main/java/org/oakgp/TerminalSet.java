package org.oakgp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.node.VariableNode;
import org.oakgp.util.Random;

/** Represents the range of possible terminal nodes to use during a genetic programming run. */
public final class TerminalSet {
   private final Random random;
   private final double ratioVariables;
   private final Map<Type, List<VariableNode>> variablesByType;
   private final Map<Type, List<ConstantNode>> constantsByType;

   public TerminalSet(Random random, double ratioVariables, Type[] variableTypes, ConstantNode[] constants) {
      this.random = random;
      this.ratioVariables = ratioVariables;

      this.variablesByType = new HashMap<>();
      setVariablesByType(variableTypes);

      this.constantsByType = new HashMap<>();
      setConstantsByType(constants);
   }

   private void setVariablesByType(Type[] variableTypes) {
      for (int i = 0; i < variableTypes.length; i++) {
         // TODO have utility class for this logic and share with setConstantsByType and FunctionSet
         Type type = variableTypes[i];
         VariableNode variableNode = new VariableNode(i, type);
         List<VariableNode> variables = variablesByType.get(type);
         if (variables == null) {
            variables = new ArrayList<>();
            variablesByType.put(type, variables);
         }
         variables.add(variableNode);
      }
   }

   private void setConstantsByType(ConstantNode[] constants) {
      for (ConstantNode constantNode : constants) {
         Type type = constantNode.getType();
         List<ConstantNode> constantsOfType = constantsByType.get(type);
         if (constantsOfType == null) {
            constantsOfType = new ArrayList<>();
            constantsByType.put(type, constantsOfType);
         }
         constantsOfType.add(constantNode);
      }
   }

   /**
    * Returns a randomly selected terminal node.
    *
    * @return a randomly selected terminal node
    */
   public Node next(Type type) {
      List<VariableNode> variables = variablesByType.get(type);
      List<ConstantNode> constants = constantsByType.get(type);
      if (variables == null && constants == null) {
         // TODO remove this check?
         throw new RuntimeException("No " + type);
      } else if (variables == null) {
         return nextConstant(constants);
      } else if (constants == null) {
         return nextVariable(variables);
      } else if (doCreateVariable()) {
         return nextVariable(variables);
      } else {
         return nextConstant(constants);
      }
   }

   /**
    * Returns a randomly selected terminal node that is not the same as the specified {@code Node}.
    *
    * @param current
    *           the current {@code Node} that the returned result should be an alternative to (i.e. not the same as)
    * @return a randomly selected terminal node that is not the same as the specified {@code Node}
    */
   public Node nextAlternative(Node current) {
      if (doCreateVariable()) {
         return nextAlternativeVariable(current);
      } else {
         return nextAlternativeConstant(current);
      }
   }

   private Node nextAlternativeVariable(Node current) {
      Type type = current.getType();
      List<VariableNode> variables = variablesByType.get(type);
      if (current instanceof VariableNode) {
         int numVariables = variables.size();
         if (variables.size() < 2) {
            return nextConstant(constantsByType.get(type));
         } else {
            int randomId = random.nextInt(numVariables);
            if (randomId == ((VariableNode) current).getId()) {
               int secondRandomId = random.nextInt(numVariables - 1);
               if (secondRandomId >= randomId) {
                  return variables.get(secondRandomId + 1);
               } else {
                  return variables.get(secondRandomId);
               }
            } else {
               return variables.get(randomId);
            }
         }
      } else {
         if (variables == null) {
            // TODO
            return current;
         }
         return nextVariable(variables);
      }
   }

   private Node nextConstant(List<ConstantNode> constants) {
      return constants.get(random.nextInt(constants.size()));
   }

   private Node nextVariable(List<VariableNode> variables) {
      return variables.get(random.nextInt(variables.size()));
   }

   private Node nextAlternativeConstant(Node current) {
      Type type = current.getType();
      List<ConstantNode> constants = constantsByType.get(type);
      int numConstants = constants.size();
      int randomIndex = random.nextInt(numConstants);
      ConstantNode next = constants.get(randomIndex);
      if (next == current) {
         if (numConstants == 1) {
            // TODO
            return constants.get(0);
         }
         int secondRandomIndex = random.nextInt(numConstants - 1);
         if (secondRandomIndex >= randomIndex) {
            return constants.get(secondRandomIndex + 1);
         } else {
            return constants.get(secondRandomIndex);
         }
      } else {
         return next;
      }
   }

   private boolean doCreateVariable() {
      return random.nextDouble() < ratioVariables;
   }
}
