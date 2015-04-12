package org.oakgp;

import java.util.List;

import org.oakgp.function.Function;
import org.oakgp.node.Node;
import org.oakgp.util.Random;

/** Represents the range of possible functions and terminal nodes to use during a genetic programming run. */
public final class PrimitiveSet {
   private final FunctionSet functionSet;
   private final ConstantSet constantSet;
   private final VariableSet variableSet;
   private final Random random;
   private final double ratioVariables;

   public PrimitiveSet(FunctionSet functionSet, ConstantSet constantSet, VariableSet variableSet, Random random, double ratioVariables) {
      this.functionSet = functionSet;
      this.constantSet = constantSet;
      this.variableSet = variableSet;
      this.random = random;
      this.ratioVariables = ratioVariables;
   }

   /**
    * Returns a randomly selected terminal node.
    *
    * @return a randomly selected terminal node
    */
   public Node nextTerminal(Type type) {
      boolean doCreateVariable = doCreateVariable();
      Node next = nextTerminal(type, doCreateVariable);
      if (next == null) {
         next = nextTerminal(type, !doCreateVariable);
      }
      if (next == null) {
         throw new IllegalArgumentException("No terminals of type: " + type);
      } else {
         return next;
      }
   }

   private Node nextTerminal(Type type, boolean doCreateVariable) {
      List<? extends Node> possibilities = doCreateVariable ? variableSet.getByType(type) : constantSet.getByType(type);
      return randomlySelectAlternative(null, possibilities);
   }

   /**
    * Returns a randomly selected terminal node that is not the same as the specified {@code Node}.
    *
    * @param current
    *           the current {@code Node} that the returned result should be an alternative to (i.e. not the same as)
    * @return a randomly selected terminal node that is not the same as the specified {@code Node}
    */
   public Node nextAlternativeTerminal(Node current) {
      boolean doCreateVariable = doCreateVariable();
      Node next = nextAlternativeNode(current, doCreateVariable);
      if (next == current) {
         return nextAlternativeNode(current, !doCreateVariable);
      } else {
         return next;
      }
   }

   private boolean doCreateVariable() {
      return random.nextDouble() < ratioVariables;
   }

   private Node nextAlternativeNode(Node current, boolean doCreateVariable) {
      Type type = current.getType();
      List<? extends Node> possibilities = doCreateVariable ? variableSet.getByType(type) : constantSet.getByType(type);
      return randomlySelectAlternative(current, possibilities);
   }

   /**
    * Returns a randomly selected {@code Function} of the specified {@code Type}.
    *
    * @param type
    *           the required return type of the {@code Function}
    * @return a randomly selected {@code Function} with a return type of {@code type}
    */
   public Function nextFunction(Type type) {
      List<Function> typeFunctions = functionSet.getByType(type);
      if (typeFunctions == null) {
         throw new IllegalArgumentException("No functions with return type: " + type);
      }
      int index = random.nextInt(typeFunctions.size());
      return typeFunctions.get(index);
   }

   /**
    * Returns a randomly selected {@code Function} that is not the same as the specified {@code Function}.
    *
    * @param current
    *           the current {@code Function} that the returned result should be an alternative to (i.e. not the same as)
    * @return a randomly selected {@code Function} that is not the same as the specified {@code Function}
    */
   public Function nextAlternativeFunction(Function current) {
      Signature signature = current.getSignature();
      List<Function> functions = functionSet.getBySignature(signature);
      return randomlySelectAlternative(current, functions);
   }

   private <C, P extends C> C randomlySelectAlternative(C currentVersion, List<P> possibilities) {
      if (possibilities == null) {
         return currentVersion;
      }

      int possibilitiesSize = possibilities.size();
      int randomIndex = random.nextInt(possibilitiesSize);
      C next = possibilities.get(randomIndex);
      if (next == currentVersion) {
         if (possibilitiesSize == 1) {
            return currentVersion;
         }

         int secondRandomIndex = random.nextInt(possibilitiesSize - 1);
         if (secondRandomIndex >= randomIndex) {
            return possibilities.get(secondRandomIndex + 1);
         } else {
            return possibilities.get(secondRandomIndex);
         }
      } else {
         return next;
      }
   }
}
