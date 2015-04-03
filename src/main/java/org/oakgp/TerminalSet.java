package org.oakgp;

import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.node.VariableNode;
import org.oakgp.util.Random;

/** Represents the range of possible terminal nodes to use during a genetic programming run. */
public final class TerminalSet {
   private final Random random;
   private final double ratioVariables;
   private final int numVariables;
   private final VariableNode[] variableNodeCache;
   private final ConstantNode[] constants;

   // TODO defensive copy of constants?
   public TerminalSet(Random random, double ratioVariables, Type[] variableTypes, ConstantNode[] constants) {
      this.random = random;
      this.ratioVariables = ratioVariables;
      this.numVariables = variableTypes.length;
      this.variableNodeCache = new VariableNode[numVariables];
      for (int i = 0; i < numVariables; i++) {
         variableNodeCache[i] = new VariableNode(i, variableTypes[i]);
      }
      this.constants = constants;
   }

   /**
    * Returns a randomly selected terminal node.
    *
    * @return a randomly selected terminal node
    */
   public Node next() {
      if (doCreateVariable()) {
         return nextVariable();
      } else {
         return nextConstant();
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
      if (current instanceof VariableNode) {
         if (numVariables == 1) {
            return nextConstant();
         } else {
            int randomId = random.nextInt(numVariables);
            if (randomId == ((VariableNode) current).getId()) {
               int secondRandomId = random.nextInt(numVariables - 1);
               if (secondRandomId >= randomId) {
                  return variableNodeCache[secondRandomId + 1];
               } else {
                  return variableNodeCache[secondRandomId];
               }
            } else {
               return variableNodeCache[randomId];
            }
         }
      } else {
         return nextVariable();
      }
   }

   private Node nextConstant() {
      return constants[random.nextInt(constants.length)];
   }

   private Node nextVariable() {
      return variableNodeCache[random.nextInt(numVariables)];
   }

   private Node nextAlternativeConstant(Node current) {
      int randomIndex = random.nextInt(constants.length);
      ConstantNode next = constants[randomIndex];
      if (next == current) {
         int secondRandomIndex = random.nextInt(constants.length - 1);
         if (secondRandomIndex >= randomIndex) {
            return constants[secondRandomIndex + 1];
         } else {
            return constants[secondRandomIndex];
         }
      } else {
         return next;
      }
   }

   private boolean doCreateVariable() {
      return random.nextDouble() < ratioVariables;
   }
}
