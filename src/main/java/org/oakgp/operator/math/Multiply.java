package org.oakgp.operator.math;

import static org.oakgp.operator.math.ArithmeticExpressionSimplifier.ONE;
import static org.oakgp.operator.math.ArithmeticExpressionSimplifier.ZERO;
import static org.oakgp.operator.math.ArithmeticExpressionSimplifier.createConstant;
import static org.oakgp.operator.math.ArithmeticExpressionSimplifier.isAddOrSubtract;
import static org.oakgp.operator.math.ArithmeticExpressionSimplifier.isMultiply;
import static org.oakgp.util.NodeComparator.NODE_COMPARATOR;

import org.oakgp.Arguments;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.operator.Operator;

/** Performs multiplication. */
public final class Multiply extends ArithmeticOperator {
   /**
    * Returns the result of multiplying the two elements of the specified arguments.
    *
    * @return the result of multiplying {@code arg1} and {@code arg2}
    */
   @Override
   protected int evaluate(int arg1, int arg2) {
      return arg1 * arg2;
   }

   @Override
   public Node simplify(Arguments arguments) {
      Node arg1 = arguments.get(0);
      Node arg2 = arguments.get(1);

      if (NODE_COMPARATOR.compare(arg1, arg2) > 0) {
         // as for addition the order of the arguments is not important, order arguments in a consistent way
         // e.g. (* v1 1) -> (* 1 v1)
         return new FunctionNode(this, arg2, arg1);
      } else if (ZERO.equals(arg1)) {
         // anything multiplied by zero is zero
         // e.g. (* 0 v0) -> 0
         return ZERO;
      } else if (ZERO.equals(arg2)) {
         // should never get here to to earlier ordering of arguments
         throw new IllegalArgumentException("arg1 " + arg1 + " arg2 " + arg2);
      } else if (ONE.equals(arg1)) {
         // anything multiplied by one is itself
         // e.g. (* 1 v0) -> v0
         return arg2;
      } else if (ONE.equals(arg2)) {
         // should never get here to to earlier ordering of arguments
         throw new IllegalArgumentException("arg1 " + arg1 + " arg2 " + arg2);
      } else {
         if (arg1 instanceof ConstantNode && arg2 instanceof FunctionNode) {
            FunctionNode fn = (FunctionNode) arg2;
            Operator o = fn.getOperator();
            Arguments args = fn.getArguments();
            Node fnArg1 = args.get(0);
            Node fnArg2 = args.get(1);
            if (fnArg1 instanceof ConstantNode) {
               if (isAddOrSubtract(o)) {
                  return new FunctionNode(o, multiply(arg1, fnArg1), new FunctionNode(this, arg1, fnArg2));
               } else if (isMultiply(o)) {
                  return new FunctionNode(this, multiply(arg1, fnArg1), fnArg2);
               } else {
                  throw new IllegalArgumentException();
               }
            } else if (isAddOrSubtract(o)) {
               return new FunctionNode(o, new FunctionNode(this, arg1, fnArg1), new FunctionNode(this, arg1, fnArg2));
            }
         }

         return null;
      }
   }

   private ConstantNode multiply(Node n1, Node n2) {
      int i1 = (int) n1.evaluate(null);
      int i2 = (int) n2.evaluate(null);
      return createConstant(i1 * i2);
   }
}
