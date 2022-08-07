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

import static org.oakgp.node.NodeType.isConstant;
import static org.oakgp.node.NodeType.isFunction;

import org.oakgp.function.Function;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.type.Types.Type;

/** Performs subtraction. */
final class Subtract<T extends Comparable<T>> extends ArithmeticOperator<T> {
   private final NumberUtils<T> numberUtils;
   private final ArithmeticExpressionSimplifier simplifier;

   /** @see NumberUtils#getSubtract() */
   Subtract(NumberUtils<T> numberUtils) {
      super(numberUtils.getType());
      this.numberUtils = numberUtils;
      this.simplifier = numberUtils.getSimplifier();
   }

   /**
    * Returns the result of subtracting the second element of the specified arguments from the first.
    *
    * @return the result of subtracting {@code arg2} from {@code arg1}
    */
   @Override
   protected T calculate(T arg1, T arg2) {
      return numberUtils.subtract(arg1, arg2);
   }

   @Override
   public Node simplify(FunctionNode functionNode) {
      Type returnType = functionNode.getType();
      ChildNodes children = functionNode.getChildren();
      Node arg1 = children.first();
      Node arg2 = children.second();

      if (arg1.equals(arg2)) {
         // anything minus itself is zero
         // e.g. (- x x) -> 0
         return numberUtils.zero();
      } else if (numberUtils.isZero(arg2)) {
         // anything minus zero is itself
         // e.g. (- x 0) -> x
         return arg1;
      } else if (numberUtils.isZero(arg1) && isSubtract(arg2)) {
         // simplify "zero minus n" expressions
         // e.g. (- 0 (- x y) -> (- y x)
         FunctionNode fn2 = (FunctionNode) arg2;
         ChildNodes fn2Arguments = fn2.getChildren();
         return new FunctionNode(this, returnType, fn2Arguments.second(), fn2Arguments.first());
      } else if (isConstant(arg2) && numberUtils.isNegative(arg2)) {
         // convert double negatives to addition
         // e.g. (- x -1) -> (+ 1 x)
         return new FunctionNode(numberUtils.getAdd(), returnType, numberUtils.negate(arg2), arg1);
      } else {
         if (numberUtils.isArithmeticExpression(arg2)) {
            FunctionNode fn = (FunctionNode) arg2;
            Function f = fn.getFunction();
            ChildNodes args = fn.getChildren();
            Node fnArg1 = args.first();
            Node fnArg2 = args.second();
            if (numberUtils.isMultiply(f) && isConstant(fnArg1)) {
               if (numberUtils.isZero(arg1)) {
                  // (- 0 (* -3 v0)) -> (* 3 v0)
                  return new FunctionNode(f, returnType, numberUtils.negateConstant(fnArg1), fnArg2);
               } else if (numberUtils.isNegative(fnArg1)) {
                  // (- 7 (* -3 v0)) -> (+ 7 (* 3 v0))
                  return new FunctionNode(numberUtils.getAdd(), returnType, arg1, new FunctionNode(f, returnType, numberUtils.negateConstant(fnArg1), fnArg2));
               }
            } else if (numberUtils.isAdd(f) && numberUtils.isZero(arg1)) {
               // (- 0 (+ v0 v1)) -> (+ (0 - v0) (0 - v1))
               return new FunctionNode(f, returnType, numberUtils.negate(fnArg1), numberUtils.negate(fnArg2));
            } else if (numberUtils.isSubtract(fn) && isConstant(arg1) && isConstant(fnArg1)) {
               if (numberUtils.isZero(arg1)) {
                  // added exception to confirm we never actually get here
                  throw new IllegalArgumentException();
               } else if (numberUtils.isZero(fnArg1)) {
                  // (- 1 (- 0 v0)) -> (+ 1 v0)
                  return new FunctionNode(numberUtils.getAdd(), returnType, arg1, fnArg2);
               } else {
                  // (- 1 (- 7 v0)) -> (- v0 6)
                  return new FunctionNode(numberUtils.getAdd(), returnType, numberUtils.subtract(arg1, fnArg1), fnArg2);
               }
            }
         }

         return simplifier.simplify(this, arg1, arg2);
      }
   }

   private boolean isSubtract(Node arg2) {
      return isFunction(arg2) && numberUtils.isSubtract((FunctionNode) arg2);
   }

   @Override
   public String getDisplayName() {
      return "-";
   }
}
