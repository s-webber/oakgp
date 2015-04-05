package org.oakgp;

import java.util.List;

import org.oakgp.function.Function;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.node.VariableNode;
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
      List<VariableNode> variables = variableSet.getByType(type);
      List<ConstantNode> constants = constantSet.getByType(type);
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

   private ConstantNode nextConstant(List<ConstantNode> constants) {
      return constants.get(random.nextInt(constants.size()));
   }

   private VariableNode nextVariable(List<VariableNode> variables) {
      return variables.get(random.nextInt(variables.size()));
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
      List<VariableNode> variables = variableSet.getByType(type);
      if (current instanceof VariableNode) {
         int numVariables = variables.size();
         if (variables.size() < 2) {
            return nextConstant(constantSet.getByType(type));
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

   private Node nextAlternativeConstant(Node current) {
      Type type = current.getType();
      List<ConstantNode> constants = constantSet.getByType(type);
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

   /**
    * Returns a randomly selected {@code Function} of the specified {@code Type}.
    *
    * @param type
    *           the required return type of the {@code Function}
    * @return a randomly selected {@code Function} with a return type of {@code type}
    */
   public Function nextFunction(Type type) {
      List<Function> typeFunctions = functionSet.getByType(type);
      if (typeFunctions == null) { // TODO remove this check?
         throw new RuntimeException("No " + type);
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
   public Function nextFunction(Function current) {
      Signature signature = current.getSignature();
      List<Function> functions = functionSet.getBySignature(signature);
      if (functions == null) {
         // TODO remove this check?
         throw new RuntimeException("no match " + current + " " + current.getSignature() + " " + functions);
      }
      int functionsSize = functions.size();
      if (functionsSize == 1) {
         // TODO return Optional.empty() instead - so calling called can try something different
         // (e.g. call this method on one of the node's arguments instead)
         return current;
      }
      int randomIndex = random.nextInt(functionsSize);
      Function next = functions.get(randomIndex);
      if (next == current) {
         int secondRandomIndex = random.nextInt(functionsSize - 1);
         if (secondRandomIndex >= randomIndex) {
            return functions.get(secondRandomIndex + 1);
         } else {
            return functions.get(secondRandomIndex);
         }
      } else {
         return next;
      }
   }
}
