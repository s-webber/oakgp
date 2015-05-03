package org.oakgp.function.math;

import static org.oakgp.node.NodeType.isConstant;
import static org.oakgp.node.NodeType.isFunction;
import static org.oakgp.util.NodeComparator.NODE_COMPARATOR;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

/** Performs addition. */
final class Add extends ArithmeticOperator {
   private final NumberUtils numberUtils;
   private final ArithmeticExpressionSimplifier simplifier;

   Add(NumberUtils numberUtils) {
      super(numberUtils.getType());
      this.numberUtils = numberUtils;
      this.simplifier = numberUtils.getSimplifier();
   }

   /**
    * Returns the result of adding the two elements of the specified arguments.
    *
    * @return the result of adding {@code arg1} and {@code arg2}
    */
   @Override
   protected Object evaluate(Node arg1, Node arg2, Assignments assignments) {
      return numberUtils.add(arg1, arg2, assignments).evaluate(null);
   }

   @Override
   public Node simplify(Arguments arguments) {
      Node arg1 = arguments.firstArg();
      Node arg2 = arguments.secondArg();

      if (NODE_COMPARATOR.compare(arg1, arg2) > 0) {
         // as for addition the order of the arguments is not important, order arguments in a consistent way
         // e.g. (+ v1 1) -> (+ 1 v1)
         return new FunctionNode(this, arg2, arg1);
      } else if (numberUtils.isZero(arg1)) {
         // anything plus zero is itself
         // e.g. (+ 0 v0) -> v0
         return arg2;
      } else if (numberUtils.isZero(arg2)) {
         // should never get here to to earlier ordering of arguments
         throw new IllegalArgumentException("arg1 " + arg1 + " arg2 " + arg2);
      } else if (arg1.equals(arg2)) {
         // anything plus itself is equal to itself multiplied by two
         // e.g. (+ x x) -> (* 2 x)
         return numberUtils.multiplyByTwo(arg1);
      } else if (isConstant(arg1) && numberUtils.isNegative(arg1)) {
         // convert addition of negative numbers to subtraction
         // e.g. (+ -3 x) -> (- x 3)
         return new FunctionNode(numberUtils.getSubtract(), arg2, numberUtils.negateConstant(arg1));
      } else if (isConstant(arg2) && numberUtils.isNegative(arg2)) {
         // should never get here as, due to the earlier ordering of arguments,
         // the only time the second argument will be a constant is when the first argument is also a constant -
         // in which case it would of already been simplified to the result of the addition.
         // e.g. (+ 2 7) would have already been simplified to 9 before it got this far
         throw new IllegalArgumentException("arg1 " + arg1 + " arg2 " + arg2);
      } else if (isConstant(arg1) && isFunction(arg2)) {
         FunctionNode fn2 = (FunctionNode) arg2;
         if (isConstant(fn2.getArguments().firstArg()) && numberUtils.isAddOrSubtract(fn2.getFunction())) {
            return new FunctionNode(fn2.getFunction(), numberUtils.add(arg1, fn2.getArguments().firstArg()), fn2.getArguments().secondArg());
         }
      }

      return simplifier.simplify(this, arg1, arg2);
   }

   @Override
   public String getDisplayName() {
      return "+";
   }
}
