/*
 * Copyright 2015 S. Webber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.oakgp.function.math;

import static org.oakgp.node.NodeType.areFunctions;
import static org.oakgp.node.NodeType.isConstant;
import static org.oakgp.node.NodeType.isFunction;
import static org.oakgp.util.NodeComparator.NODE_COMPARATOR;

import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

final class ArithmeticExpressionSimplifier {
   private static final boolean SANITY_CHECK = true;

   private final NumberUtils<?> numberUtils;

   ArithmeticExpressionSimplifier(NumberUtils<?> numberUtils) {
      this.numberUtils = numberUtils;
   }

   /** @return {@code null} if it was not possible to simplify the expression. */
   Node simplify(Function function, Node firstArg, Node secondArg) {
      sanityCheck(() -> {
         assertAddOrSubtract(function);
         assertArgumentsOrdered(function, firstArg, secondArg);
      });

      return getSimplifiedVersion(function, firstArg, secondArg);
   }

   private Node getSimplifiedVersion(Function function, Node firstArg, Node secondArg) {
      boolean isPos = numberUtils.isAdd(function);
      if (areFunctions(firstArg, secondArg)) {
         NodePair p = removeFromChildNodes(firstArg, secondArg, isPos);
         if (p != null) {
            return new FunctionNode(function, numberUtils.getType(), p.nodeThatHasBeenReduced, p.nodeThatHasBeenExpanded);
         }
         p = removeFromChildNodes(secondArg, firstArg, isPos);
         if (p != null) {
            return new FunctionNode(function, numberUtils.getType(), p.nodeThatHasBeenExpanded, p.nodeThatHasBeenReduced);
         }
      } else if (isFunction(firstArg)) {
         return combineWithChildNodes(firstArg, secondArg, isPos);
      } else if (isFunction(secondArg)) {
         // 3, (+ (* 12 v2) 30) -> (+ (* 12 v2) 33)
         Node tmp = combineWithChildNodes(secondArg, firstArg, isPos);
         if (tmp != null && numberUtils.isSubtract(function)) {
            // 3, (- (* 12 v2) 30) -> (- (* 12 v2) 33) -> (0 - (- (* 12 v2) 33))
            return new FunctionNode(function, numberUtils.getType(), numberUtils.zero(), tmp);
         } else {
            return tmp;
         }
      }

      return null;
   }

   /**
    * Returns the result of removing the second argument from the first argument.
    *
    * @param nodeToWalk
    *           tree structure to walk and remove the node from
    * @param nodeToRemove
    *           the node to remove from {@code nodeToWalk}
    * @param isPos
    *           {@code true} to indicate that {@code nodeToRemove} should be removed from {@code nodeToWalk}, else {@code false} to indicate that
    *           {@code nodeToAdd} should be added to {@code nodeToWalk}
    * @return {@code null} if it was not possible to remove (@code nodeToRemove} from {@code nodeToWalk}
    */
   private NodePair removeFromChildNodes(final Node nodeToWalk, final Node nodeToRemove, final boolean isPos) {
      if (numberUtils.isArithmeticExpression(nodeToWalk)) {
         FunctionNode fn = (FunctionNode) nodeToWalk;
         Function f = fn.getFunction();
         Node firstArg = fn.getChildren().first();
         Node secondArg = fn.getChildren().second();
         if (numberUtils.isMultiply(f) && isFunction(nodeToRemove)) {
            FunctionNode x = (FunctionNode) nodeToRemove;
            ChildNodes a = x.getChildren();
            if (numberUtils.isMultiply(x) && isConstant(firstArg) && isConstant(a.first()) && secondArg.equals(a.second())) {
               ConstantNode result;
               if (isPos) {
                  result = numberUtils.add(a.first(), firstArg);
               } else {
                  result = numberUtils.subtract(a.first(), firstArg);
               }
               Node tmp = new FunctionNode(f, numberUtils.getType(), result, secondArg);
               return new NodePair(numberUtils.zero(), tmp);
            }

            Node tmp = combineWithChildNodes(nodeToRemove, nodeToWalk, isPos);
            if (tmp != null) {
               return new NodePair(numberUtils.zero(), tmp);
            }
         }

         // TODO is this logic still required or is it provided by Add and Subtract?
         boolean isSubtract = numberUtils.isSubtract(f);
         if (numberUtils.isAdd(f) || isSubtract) {
            NodePair p = removeFromChildNodes(firstArg, nodeToRemove, isPos);
            if (p != null) {
               NodePair p2 = removeFromChildNodes(secondArg, p.nodeThatHasBeenExpanded, isSubtract ? !isPos : isPos);
               if (p2 == null) {
                  return new NodePair(new FunctionNode(f, numberUtils.getType(), p.nodeThatHasBeenReduced, secondArg), p.nodeThatHasBeenExpanded);
               } else {
                  return new NodePair(new FunctionNode(f, numberUtils.getType(), p.nodeThatHasBeenReduced, p2.nodeThatHasBeenReduced),
                        p2.nodeThatHasBeenExpanded);
               }
            }
            p = removeFromChildNodes(secondArg, nodeToRemove, isSubtract ? !isPos : isPos);
            if (p != null) {
               return new NodePair(new FunctionNode(f, numberUtils.getType(), firstArg, p.nodeThatHasBeenReduced), p.nodeThatHasBeenExpanded);
            }
         }
      } else if (!numberUtils.isZero(nodeToWalk)) {
         Node tmp = combineWithChildNodes(nodeToRemove, nodeToWalk, isPos);
         if (tmp != null) {
            return new NodePair(numberUtils.zero(), tmp);
         }
      }
      return null;
   }

   /**
    * Returns the result of merging the second argument into the first argument.
    *
    * @param nodeToWalk
    *           tree structure to walk and remove the node to
    * @param nodeToAdd
    *           the node to remove from {@code nodeToWalk}
    * @param isPos
    *           {@code true} to indicate that {@code nodeToAdd} should be added to {@code nodeToWalk}, else {@code false} to indicate that {@code nodeToAdd}
    *           should be subtracted from {@code nodeToWalk}
    * @return {@code null} if it was not possible to merge (@code nodeToAdd} into {@code nodeToWalk}
    */
   Node combineWithChildNodes(final Node nodeToWalk, final Node nodeToAdd, final boolean isPos) {
      if (isSuitableForCombining(nodeToWalk, nodeToAdd)) {
         return combine(nodeToWalk, nodeToAdd, isPos);
      }
      if (!numberUtils.isArithmeticExpression(nodeToWalk)) {
         return null;
      }

      FunctionNode currentFunctionNode = (FunctionNode) nodeToWalk;
      Node firstArg = currentFunctionNode.getChildren().first();
      Node secondArg = currentFunctionNode.getChildren().second();
      Function currentFunction = currentFunctionNode.getFunction();
      boolean isAdd = numberUtils.isAdd(currentFunction);
      boolean isSubtract = numberUtils.isSubtract(currentFunction);
      if (isAdd || isSubtract) {
         boolean recursiveIsPos = isPos;
         if (isSubtract) {
            recursiveIsPos = !isPos;
         }
         if (isSuitableForCombining(firstArg, nodeToAdd)) {
            return new FunctionNode(currentFunction, numberUtils.getType(), combine(firstArg, nodeToAdd, isPos), secondArg);
         } else if (isSuitableForCombining(secondArg, nodeToAdd)) {
            return new FunctionNode(currentFunction, numberUtils.getType(), firstArg, combine(secondArg, nodeToAdd, recursiveIsPos));
         }
         Node tmp = combineWithChildNodes(firstArg, nodeToAdd, isPos);
         if (tmp != null) {
            return new FunctionNode(currentFunction, numberUtils.getType(), tmp, secondArg);
         }
         tmp = combineWithChildNodes(secondArg, nodeToAdd, recursiveIsPos);
         if (tmp != null) {
            return new FunctionNode(currentFunction, numberUtils.getType(), firstArg, tmp);
         }
      } else if (numberUtils.isMultiply(currentFunction) && isConstant(firstArg) && secondArg.equals(nodeToAdd)) {
         ConstantNode multiplier;
         if (isPos) {
            multiplier = numberUtils.increment(firstArg);
         } else {
            multiplier = numberUtils.decrement(firstArg);
         }
         return new FunctionNode(currentFunction, numberUtils.getType(), multiplier, nodeToAdd);
      } else if (isMultiplyingTheSameValue(nodeToWalk, nodeToAdd)) {
         return combineMultipliers(nodeToWalk, nodeToAdd, isPos);
      }

      return null;
   }

   /**
    * Returns {@code true} if the specified nodes can be combined into a single node.
    * <p>
    * Two constants (even if they have different values) can be combined. e.g. {@code 9} and {@code 12} can be combined to form {@code 21}
    * </p>
    * <p>
    * Any two node that are {@code equal} can be combined. e.g. {@code v0} and {@code v0} can be combined to form {@code (* 2 v0)}, {@code (- 8 v0)} and
    * {@code (- 8 v0)} can be combined to form {@code (* 2 (- 8 v0))}
    * </p>
    */
   private static boolean isSuitableForCombining(Node currentNode, Node nodeToReplace) {
      if (isConstant(nodeToReplace)) {
         return isConstant(currentNode);
      } else {
         return nodeToReplace.equals(currentNode);
      }
   }

   /**
    * Returns a node that is the result of combining the two specified nodes.
    * <p>
    * e.g. {@code 9} and {@code 12} can be combined to form {@code 21}, {@code v0} and {@code v0} can be combined to form {@code (* 2 v0)}
    * </p>
    *
    * @param isPos
    *           {@code true} to indicate that {@code second} should be added to {@code first}, else {@code false} to indicate that {@code second} should be
    *           subtracted from {@code first}
    */
   private Node combine(Node first, Node second, boolean isPos) {
      sanityCheck(() -> assertSameClass(first, second));

      if (isConstant(second)) {
         if (isPos) {
            return numberUtils.add(first, second);
         } else {
            return numberUtils.subtract(first, second);
         }
      } else {
         if (isPos) {
            return numberUtils.multiplyByTwo(second);
         } else {
            return numberUtils.zero();
         }
      }
   }

   /**
    * Returns {@code true} if both of the specified nodes represent multiplication of the same value by a constant.
    * <p>
    * Examples of arguments that would return true: {@code (* 3 v0), (* 7 v0)} or {@code (* 1 v0), (* -8 v0)}
    * </p>
    * <p>
    * Examples of arguments that would return false: {@code (* 3 v0), (+ 7 v0)} or {@code (* 1 v0), (* -8 v1)}
    * </p>
    */
   private boolean isMultiplyingTheSameValue(Node n1, Node n2) {
      if (areFunctions(n1, n2)) {
         FunctionNode f1 = (FunctionNode) n1;
         FunctionNode f2 = (FunctionNode) n2;
         if (numberUtils.isMultiply(f1) && numberUtils.isMultiply(f2) && isConstant(f1.getChildren().first()) && isConstant(f2.getChildren().first())
               && f1.getChildren().second().equals(f2.getChildren().second())) {
            return true;
         }
      }
      return false;
   }

   /** e.g. arguments: {@code (* 3 v0), (* 7 v0)} would produce: {@code (* 10 v0)} */
   private Node combineMultipliers(Node n1, Node n2, boolean isPos) {
      FunctionNode f1 = (FunctionNode) n1;
      FunctionNode f2 = (FunctionNode) n2;
      ConstantNode result;
      if (isPos) {
         result = numberUtils.add(f1.getChildren().first(), f2.getChildren().first());
      } else {
         result = numberUtils.subtract(f1.getChildren().first(), f2.getChildren().first());
      }
      return new FunctionNode(f1.getFunction(), numberUtils.getType(), result, f1.getChildren().second());
   }

   private static void sanityCheck(Runnable r) {
      // TODO remove this method - only here to sanity check input during development
      if (SANITY_CHECK) {
         r.run();
      }
   }

   private void assertAddOrSubtract(Function f) {
      if (!numberUtils.isAddOrSubtract(f)) {
         throw new IllegalArgumentException(f.getClass().getName());
      }
   }

   private void assertArgumentsOrdered(Function f, Node firstArg, Node secondArg) {
      if (!numberUtils.isSubtract(f) && NODE_COMPARATOR.compare(firstArg, secondArg) > 0) {
         throw new IllegalArgumentException("arg1 " + firstArg + " arg2 " + secondArg);
      }
   }

   private static void assertSameClass(Node currentNode, Node nodeToReplace) {
      if (nodeToReplace.getClass() != currentNode.getClass()) {
         throw new IllegalArgumentException(nodeToReplace.getClass().getName() + " " + currentNode.getClass().getName());
      }
   }

   /**
    * Asserts that the specified nodes evaluate to the same results.
    *
    * TODO remove this method?
    *
    * @param first
    *           the node to compare to {@code second}
    * @param second
    *           the node to compare to {@code first}
    * @throws IllegalArgumentException
    *            if the specified nodes evaluate to different results
    */
   static void assertEvaluateToSameResult(Node first, Node second) { // TODO move to test utils
      Object[] assignedValues = { 2, 14, 4, 9, 7 };
      Assignments assignments = Assignments.createAssignments(assignedValues);
      Object firstResult = first.evaluate(assignments, null);
      Object secondResult = second.evaluate(assignments, null);
      if (!firstResult.equals(secondResult)) {
         throw new IllegalArgumentException(first + " = " + firstResult + " " + second + " = " + secondResult);
      }
   }

   private static class NodePair {
      private final Node nodeThatHasBeenReduced;
      private final Node nodeThatHasBeenExpanded;

      NodePair(Node nodeThatHasBeenReduced, Node nodeThatHasBeenExpanded) {
         this.nodeThatHasBeenReduced = nodeThatHasBeenReduced;
         this.nodeThatHasBeenExpanded = nodeThatHasBeenExpanded;
      }
   }
}
