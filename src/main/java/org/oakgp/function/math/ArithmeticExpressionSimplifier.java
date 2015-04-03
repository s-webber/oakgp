package org.oakgp.function.math;

import static org.oakgp.Type.integerType;
import static org.oakgp.util.NodeComparator.NODE_COMPARATOR;

import org.oakgp.Arguments;
import org.oakgp.function.Function;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.util.Utils;

final class ArithmeticExpressionSimplifier {
   static final ConstantNode ZERO = createConstant(0);
   static final ConstantNode ONE = createConstant(1);
   static final ConstantNode TWO = createConstant(2);

   /** Private constructor as all methods are static. */
   private ArithmeticExpressionSimplifier() {
      // do nothing
   }

   /** @return {@code null} if it was not possible to simplify the expression. */
   static Node simplify(Function function, Node firstArg, Node secondArg) {
      assertAddOrSubtract(function);
      assertArgumentsOrdered(function, firstArg, secondArg);

      Node simplifiedVersion = getSimplifiedVersion(function, firstArg, secondArg);
      assertEvaluateToSameResult(simplifiedVersion, function, firstArg, secondArg);
      return simplifiedVersion;
   }

   private static Node getSimplifiedVersion(Function function, Node firstArg, Node secondArg) {
      boolean isPos = isAdd(function);
      if (firstArg instanceof FunctionNode && secondArg instanceof FunctionNode) {
         NodePair p = removeFromChildNodes(firstArg, secondArg, isPos);
         if (p != null) {
            return new FunctionNode(function, p.x, p.y);
         }
         p = removeFromChildNodes(secondArg, firstArg, isPos);
         if (p != null) {
            return new FunctionNode(function, p.y, p.x);
         }
      } else if (firstArg instanceof FunctionNode) {
         return combineWithChildNodes(firstArg, secondArg, isPos);
      } else if (secondArg instanceof FunctionNode) {
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
      if (nodeToWalk instanceof FunctionNode) {
         FunctionNode fn = (FunctionNode) nodeToWalk;
         Function f = fn.getFunction();
         Node firstArg = fn.getArguments().get(0);
         Node secondArg = fn.getArguments().get(1);
         if (isMultiply(f) && nodeToRemove instanceof FunctionNode) {
            FunctionNode x = (FunctionNode) nodeToRemove;
            Arguments a = x.getArguments();
            if (isMultiply(x) && firstArg instanceof ConstantNode && a.get(0) instanceof ConstantNode && secondArg.equals(a.get(1))) {
               int i1 = (int) firstArg.evaluate(null);
               int i2 = (int) a.get(0).evaluate(null);
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
               NodePair p2 = removeFromChildNodes(secondArg, p.y, isSubtract ? !isPos : isPos);
               if (p2 == null) {
                  return new NodePair(new FunctionNode(f, p.x, secondArg), p.y);
               } else {
                  return new NodePair(new FunctionNode(f, p.x, p2.x), p2.y);
               }
            }
            p = removeFromChildNodes(secondArg, nodeToRemove, isSubtract ? !isPos : isPos);
            if (p != null) {
               return new NodePair(new FunctionNode(f, firstArg, p.x), p.y);
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
      if (!(nodeToWalk instanceof FunctionNode)) {
         return null;
      }

      FunctionNode currentFunctionNode = (FunctionNode) nodeToWalk;
      Node firstArg = currentFunctionNode.getArguments().get(0);
      Node secondArg = currentFunctionNode.getArguments().get(1);
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
      } else if (isMultiply(currentFunction) && firstArg instanceof ConstantNode && secondArg.equals(nodeToAdd)) {
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
      if (nodeToReplace instanceof ConstantNode) {
         return currentNode instanceof ConstantNode;
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
      assertSameClass(first, second);

      if (second instanceof ConstantNode) {
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
    * <p>
    * Examples of arguments that would return true: {@code (* 3 v0), (* 7 v0)} or {@code (* 1 v0), (* -8 v0)}
    * </p>
    * <p>
    * Examples of arguments that would return false: {@code (* 3 v0), (+ 7 v0)} or {@code (* 1 v0), (* -8 v1)}
    * </p>
    */
   private static boolean isMultiplyingTheSameValue(Node n1, Node n2) {
      if (n1 instanceof FunctionNode && n2 instanceof FunctionNode) {
         FunctionNode f1 = (FunctionNode) n1;
         FunctionNode f2 = (FunctionNode) n2;
         if (isMultiply(f1) && isMultiply(f2) && f1.getArguments().get(0) instanceof ConstantNode && f2.getArguments().get(0) instanceof ConstantNode
               && f1.getArguments().get(1).equals(f2.getArguments().get(1))) {
            return true;
         }
      }
      return false;
   }

   /** e.g. arguments: {@code (* 3 v0), (* 7 v0)} would produce: {@code (* 10 v0)} */
   private static Node combineMultipliers(Node n1, Node n2, boolean isPos) {
      FunctionNode f1 = (FunctionNode) n1;
      FunctionNode f2 = (FunctionNode) n2;
      int i1 = (int) f1.getArguments().get(0).evaluate(null);
      int i2 = (int) f2.getArguments().get(0).evaluate(null);
      int result;
      if (isPos) {
         result = i1 + i2;
      } else {
         result = i1 - i2;
      }
      return new FunctionNode(f1.getFunction(), createConstant(result), f1.getArguments().get(1));
   }

   private static void assertAddOrSubtract(Function f) {
      // TODO remove this method - only here to sanity check input during development
      if (!isAddOrSubtract(f)) {
         throw new IllegalArgumentException(f.getClass().getName());
      }
   }

   private static void assertArgumentsOrdered(Function f, Node firstArg, Node secondArg) {
      // TODO remove this method - only here to sanity check input during development
      if (!isSubtract(f) && NODE_COMPARATOR.compare(firstArg, secondArg) > 0) {
         throw new IllegalArgumentException("arg1 " + firstArg + " arg2 " + secondArg);
      }
   }

   private static void assertEvaluateToSameResult(Node simplifiedVersion, Function function, Node firstArg, Node secondArg) {
      // TODO remove this method - only here to sanity check output during development
      if (simplifiedVersion != null) {
         FunctionNode in = new FunctionNode(function, firstArg, secondArg);
         Utils.assertEvaluateToSameResult(in, simplifiedVersion);
      }
   }

   private static void assertSameClass(Node currentNode, Node nodeToReplace) {
      // TODO remove this method - only here to sanity check input during development
      if (nodeToReplace.getClass() != currentNode.getClass()) {
         throw new IllegalArgumentException(nodeToReplace.getClass().getName() + " " + currentNode.getClass().getName());
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
      if (arg instanceof ConstantNode) {
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
      return n instanceof FunctionNode && isSubtract((FunctionNode) n);
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
      final Node x;
      final Node y;

      NodePair(Node x, Node y) {
         this.x = x;
         this.y = y;
      }
   }
}
