package org.oakgp.function.math;

import static org.oakgp.Type.integerType;
import static org.oakgp.node.NodeType.areFunctions;
import static org.oakgp.node.NodeType.isConstant;
import static org.oakgp.node.NodeType.isFunction;
import static org.oakgp.node.NodeType.isTerminal;
import static org.oakgp.util.NodeComparator.NODE_COMPARATOR;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.serialize.NodeWriter;

final class ArithmeticExpressionSimplifier {
   private static boolean SANITY_CHECK = true;

   static final ConstantNode ZERO = createConstant(0);
   static final ConstantNode ONE = createConstant(1);
   static final ConstantNode TWO = createConstant(2);

   /** Private constructor as all methods are static. */
   private ArithmeticExpressionSimplifier() {
      // do nothing
   }

   /** @return {@code null} if it was not possible to simplify the expression. */
   static Node simplify(Function function, Node firstArg, Node secondArg) {
      sanityCheck(() -> {
         assertAddOrSubtract(function);
         assertArgumentsOrdered(function, firstArg, secondArg);
      });

      Node simplifiedVersion = getSimplifiedVersion(function, firstArg, secondArg);
      sanityCheck(() -> assertEvaluateToSameResult(simplifiedVersion, function, firstArg, secondArg));
      return simplifiedVersion;
   }

   private static Node getSimplifiedVersion(Function function, Node firstArg, Node secondArg) {
      boolean isPos = isAdd(function);
      if (areFunctions(firstArg, secondArg)) {
         NodePair p = removeFromChildNodes(firstArg, secondArg, isPos);
         if (p != null) {
            return new FunctionNode(function, p.nodeThatHasBeenReduced, p.nodeThatHasBeenExpanded);
         }
         p = removeFromChildNodes(secondArg, firstArg, isPos);
         if (p != null) {
            return new FunctionNode(function, p.nodeThatHasBeenExpanded, p.nodeThatHasBeenReduced);
         }
      } else if (isFunction(firstArg)) {
         return combineWithChildNodes(firstArg, secondArg, isPos);
      } else if (isFunction(secondArg)) {
         // 3, (+ (* 12 v2) 30) -> (+ (* 12 v2) 33)
         Node tmp = combineWithChildNodes(secondArg, firstArg, isPos);
         if (tmp != null && isSubtract(function)) {
            // 3, (- (* 12 v2) 30) -> (- (* 12 v2) 33) -> (0 - (- (* 12 v2) 33))
            return new FunctionNode(function, ZERO, tmp);
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
   private static NodePair removeFromChildNodes(final Node nodeToWalk, final Node nodeToRemove, final boolean isPos) {
      if (isFunction(nodeToWalk)) {
         FunctionNode fn = (FunctionNode) nodeToWalk;
         Function f = fn.getFunction();
         Node firstArg = fn.getArguments().firstArg();
         Node secondArg = fn.getArguments().secondArg();
         if (isMultiply(f) && isFunction(nodeToRemove)) {
            FunctionNode x = (FunctionNode) nodeToRemove;
            Arguments a = x.getArguments();
            if (isMultiply(x) && isConstant(firstArg) && isConstant(a.firstArg()) && secondArg.equals(a.secondArg())) {
               int i1 = (int) firstArg.evaluate(null);
               int i2 = (int) a.firstArg().evaluate(null);
               int result;
               if (isPos) {
                  result = i2 + i1;
               } else {
                  result = i2 - i1;
               }
               Node tmp = new FunctionNode(f, createConstant(result), secondArg);
               return new NodePair(ZERO, tmp);
            }

            Node tmp = combineWithChildNodes(nodeToRemove, nodeToWalk, isPos);
            if (tmp != null) {
               return new NodePair(ZERO, tmp);
            }
         }

         boolean isSubtract = isSubtract(f);
         if (isAdd(f) || isSubtract) {
            NodePair p = removeFromChildNodes(firstArg, nodeToRemove, isPos);
            if (p != null) {
               NodePair p2 = removeFromChildNodes(secondArg, p.nodeThatHasBeenExpanded, isSubtract ? !isPos : isPos);
               if (p2 == null) {
                  return new NodePair(new FunctionNode(f, p.nodeThatHasBeenReduced, secondArg), p.nodeThatHasBeenExpanded);
               } else {
                  return new NodePair(new FunctionNode(f, p.nodeThatHasBeenReduced, p2.nodeThatHasBeenReduced), p2.nodeThatHasBeenExpanded);
               }
            }
            p = removeFromChildNodes(secondArg, nodeToRemove, isSubtract ? !isPos : isPos);
            if (p != null) {
               return new NodePair(new FunctionNode(f, firstArg, p.nodeThatHasBeenReduced), p.nodeThatHasBeenExpanded);
            }
         }
      } else if (!ZERO.equals(nodeToWalk)) {
         Node tmp = combineWithChildNodes(nodeToRemove, nodeToWalk, isPos);
         if (tmp != null) {
            return new NodePair(ZERO, tmp);
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
   static Node combineWithChildNodes(final Node nodeToWalk, final Node nodeToAdd, final boolean isPos) {
      if (isSuitableForCombining(nodeToWalk, nodeToAdd)) {
         // TODO is it OK to instead just do nodeToWalk.equals(nodeToAdd)) {
         return combine(nodeToWalk, nodeToAdd, isPos);
      }
      if (isTerminal(nodeToWalk)) {
         return null;
      }

      FunctionNode currentFunctionNode = (FunctionNode) nodeToWalk;
      Node firstArg = currentFunctionNode.getArguments().firstArg();
      Node secondArg = currentFunctionNode.getArguments().secondArg();
      Function currentFunction = currentFunctionNode.getFunction();
      boolean isAdd = isAdd(currentFunction);
      boolean isSubtract = isSubtract(currentFunction);
      if (isAdd || isSubtract) {
         boolean recursiveIsPos = isPos;
         if (isSubtract) {
            recursiveIsPos = !isPos;
         }
         if (isSuitableForCombining(firstArg, nodeToAdd)) {
            return new FunctionNode(currentFunction, combine(firstArg, nodeToAdd, isPos), secondArg);
         } else if (isSuitableForCombining(secondArg, nodeToAdd)) {
            return new FunctionNode(currentFunction, firstArg, combine(secondArg, nodeToAdd, recursiveIsPos));
         }
         Node tmp = combineWithChildNodes(firstArg, nodeToAdd, isPos);
         if (tmp != null) {
            return new FunctionNode(currentFunction, tmp, secondArg);
         }
         tmp = combineWithChildNodes(secondArg, nodeToAdd, recursiveIsPos);
         if (tmp != null) {
            return new FunctionNode(currentFunction, firstArg, tmp);
         }
      } else if (isMultiply(currentFunction) && isConstant(firstArg) && secondArg.equals(nodeToAdd)) {
         int inc = isPos ? 1 : -1;
         return new FunctionNode(currentFunction, createConstant((int) ((ConstantNode) firstArg).evaluate(null) + inc), nodeToAdd);
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
   private static Node combine(Node first, Node second, boolean isPos) {
      sanityCheck(() -> assertSameClass(first, second));

      if (isConstant(second)) {
         int currentNodeValue = (int) first.evaluate(null);
         int nodeToReplaceValue = (int) second.evaluate(null);
         if (isPos) {
            return createConstant(currentNodeValue + nodeToReplaceValue);
         } else {
            return createConstant(currentNodeValue - nodeToReplaceValue);
         }
      } else {
         if (isPos) {
            return multiplyByTwo(second);
         } else {
            return ZERO;
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
   private static boolean isMultiplyingTheSameValue(Node n1, Node n2) {
      if (areFunctions(n1, n2)) {
         FunctionNode f1 = (FunctionNode) n1;
         FunctionNode f2 = (FunctionNode) n2;
         if (isMultiply(f1) && isMultiply(f2) && isConstant(f1.getArguments().firstArg()) && isConstant(f2.getArguments().firstArg())
               && f1.getArguments().secondArg().equals(f2.getArguments().secondArg())) {
            return true;
         }
      }
      return false;
   }

   /** e.g. arguments: {@code (* 3 v0), (* 7 v0)} would produce: {@code (* 10 v0)} */
   private static Node combineMultipliers(Node n1, Node n2, boolean isPos) {
      FunctionNode f1 = (FunctionNode) n1;
      FunctionNode f2 = (FunctionNode) n2;
      int i1 = (int) f1.getArguments().firstArg().evaluate(null);
      int i2 = (int) f2.getArguments().firstArg().evaluate(null);
      int result;
      if (isPos) {
         result = i1 + i2;
      } else {
         result = i1 - i2;
      }
      return new FunctionNode(f1.getFunction(), createConstant(result), f1.getArguments().secondArg());
   }

   private static void sanityCheck(Runnable r) {
      // TODO remove this method - only here to sanity check input during development
      if (SANITY_CHECK) {
         r.run();
      }
   }

   private static void assertAddOrSubtract(Function f) {
      if (!isAddOrSubtract(f)) {
         throw new IllegalArgumentException(f.getClass().getName());
      }
   }

   private static void assertArgumentsOrdered(Function f, Node firstArg, Node secondArg) {
      if (!isSubtract(f) && NODE_COMPARATOR.compare(firstArg, secondArg) > 0) {
         throw new IllegalArgumentException("arg1 " + firstArg + " arg2 " + secondArg);
      }
   }

   private static void assertSameClass(Node currentNode, Node nodeToReplace) {
      if (nodeToReplace.getClass() != currentNode.getClass()) {
         throw new IllegalArgumentException(nodeToReplace.getClass().getName() + " " + currentNode.getClass().getName());
      }
   }

   private static void assertEvaluateToSameResult(Node simplifiedVersion, Function function, Node firstArg, Node secondArg) {
      if (simplifiedVersion != null) {
         FunctionNode in = new FunctionNode(function, firstArg, secondArg);
         assertEvaluateToSameResult(in, simplifiedVersion);
      }
   }

   /**
    * Asserts that the specified nodes evaluate to the same results.
    *
    * @param first
    *           the node to compare to {@code second}
    * @param second
    *           the node to compare to {@code first}
    * @throws IllegalArgumentException
    *            if the specified nodes evaluate to different results
    */
   static void assertEvaluateToSameResult(Node first, Node second) {
      Object[] assignedValues = { 2, 14, 4, 9, 7 };
      Assignments assignments = Assignments.createAssignments(assignedValues);
      Object firstResult = first.evaluate(assignments);
      Object secondResult = second.evaluate(assignments);
      if (!firstResult.equals(secondResult)) {
         NodeWriter writer = new NodeWriter();
         throw new IllegalArgumentException(writer.writeNode(first) + " = " + firstResult + " " + writer.writeNode(second) + " = " + secondResult);
      }
   }

   static ConstantNode createConstant(int i) {
      return new ConstantNode(i, integerType());
   }

   static FunctionNode multiplyByTwo(Node arg) {
      // TODO don't create a new Multiply each time - reuse the same instance (and do the same for other functions)
      return new FunctionNode(new Multiply(), TWO, arg);
   }

   static Node negate(Node arg) {
      if (isConstant(arg)) {
         return createConstant(-(int) arg.evaluate(null));
      } else {
         return new FunctionNode(new Subtract(), ZERO, arg);
      }
   }

   static boolean isAddOrSubtract(Function f) {
      return isAdd(f) || isSubtract(f);
   }

   static boolean isAdd(FunctionNode n) {
      return isAdd(n.getFunction());
   }

   static boolean isAdd(Function f) {
      return isFunctionOfType(f, Add.class);
   }

   static boolean isSubtract(Node n) {
      return isFunction(n) && isSubtract((FunctionNode) n);
   }

   static boolean isSubtract(FunctionNode n) {
      return isSubtract(n.getFunction());
   }

   static boolean isSubtract(Function f) {
      return isFunctionOfType(f, Subtract.class);
   }

   static boolean isMultiply(FunctionNode n) {
      return isMultiply(n.getFunction());
   }

   static boolean isMultiply(Function f) {
      return isFunctionOfType(f, Multiply.class);
   }

   private static boolean isFunctionOfType(Function f, Class<? extends Function> functionClass) {
      return f.getClass() == functionClass;
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
