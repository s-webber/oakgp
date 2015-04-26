package org.oakgp.function.math;

import static org.oakgp.function.math.ArithmeticExpressionSimplifier.isAdd;
import static org.oakgp.function.math.ArithmeticExpressionSimplifier.isMultiply;
import static org.oakgp.function.math.ArithmeticExpressionSimplifier.isSubtract;
import static org.oakgp.function.math.ArithmeticExpressionSimplifier.negate;
import static org.oakgp.node.NodeType.isConstant;
import static org.oakgp.node.NodeType.isFunction;

import org.oakgp.Arguments;
import org.oakgp.Type;
import org.oakgp.function.Function;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

/** Performs subtraction. */
public final class Subtract extends ArithmeticOperator<Integer> {
   private final IntegerUtils integerUtils = new IntegerUtils();

   public Subtract() {
      super(Type.integerType());
   }

   /**
    * Returns the result of subtracting the second element of the specified arguments from the first.
    *
    * @return the result of subtracting {@code arg2} from {@code arg1}
    */
   @Override
   protected Integer evaluate(Integer arg1, Integer arg2) {
      return arg1 - arg2;
   }

   @Override
   public Node simplify(Arguments arguments) {
      Node arg1 = arguments.firstArg();
      Node arg2 = arguments.secondArg();

      if (arg1.equals(arg2)) {
         // anything minus itself is zero
         // e.g. (- x x) -> 0
         return integerUtils.ZERO;
      } else if (integerUtils.ZERO.equals(arg2)) {
         // anything minus zero is itself
         // e.g. (- x 0) -> x
         return arg1;
      } else if (integerUtils.ZERO.equals(arg1) && isSubtract(arg2)) {
         // simplify "zero minus ?" expressions
         // e.g. (- 0 (- x y) -> (- y x)
         FunctionNode fn2 = (FunctionNode) arg2;
         Arguments fn2Arguments = fn2.getArguments();
         return new FunctionNode(this, fn2Arguments.secondArg(), fn2Arguments.firstArg());
      } else if (isConstant(arg2) && ((int) arg2.evaluate(null)) < 0) {
         // convert double negatives to addition
         // e.g. (- x -1) -> (+ 1 x)
         return new FunctionNode(new Add(), negate(arg2), arg1);
      } else {
         if (isFunction(arg2)) {
            FunctionNode fn = (FunctionNode) arg2;
            Function f = fn.getFunction();
            Arguments args = fn.getArguments();
            Node fnArg1 = args.firstArg();
            Node fnArg2 = args.secondArg();
            if (isConstant(fnArg1) && isMultiply(f)) {
               if (integerUtils.ZERO.equals(arg1)) {
                  return new FunctionNode(f, integerUtils.negate(fnArg1), fnArg2);
               } else if (integerUtils.isNegative(fnArg1)) {
                  return new FunctionNode(new Add(), arg1, new FunctionNode(f, integerUtils.negate(fnArg1), fnArg2));
               }
            } else if (integerUtils.ZERO.equals(arg1) && isAdd(f)) {
               // (- 0 (+ v0 v1) -> (+ (0 - v0) (0 - v1))
               return new FunctionNode(f, negate(fnArg1), negate(fnArg2));
            } else if (isConstant(arg1) && isConstant(fnArg1) && isSubtract(fn)) {
               if (integerUtils.ZERO.equals(arg1)) {
                  // added exception to confirm we never actually get here
                  throw new IllegalArgumentException();
                  // (- 0 (- 0 v0)) -> v0
                  // (- 0 (- 7 v0)) -> (- v0 7)
                  // return Optional.of(new FunctionNode(op, Arguments.createArguments(fnArg2, fnArg1)));
               } else if (integerUtils.ZERO.equals(fnArg1)) {
                  // (- 1 (- 0 v0)) -> (+ 1 v0)
                  return new FunctionNode(new Add(), arg1, fnArg2);
               } else {
                  return new FunctionNode(new Add(), integerUtils.subtract(arg1, fnArg1), fnArg2);
               }
            }
         }

         return ArithmeticExpressionSimplifier.simplify(this, arg1, arg2);
      }
   }

   @Override
   public String getDisplayName() {
      return "-";
   }
}
