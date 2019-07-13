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
import static org.oakgp.util.NodeComparator.NODE_COMPARATOR;

import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

/** Performs addition. */
final class Add<T extends Comparable<T>> extends ArithmeticOperator<T> {
   private final NumberUtils<T> numberUtils;
   private final ArithmeticExpressionSimplifier simplifier;

   /** @see NumberUtils#getAdd() */
   Add(NumberUtils<T> numberUtils) {
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
   protected T evaluate(T arg1, T arg2) {
      return numberUtils.add(arg1, arg2);
   }

   @Override
   public Node simplify(ChildNodes children) {
      Node arg1 = children.first();
      Node arg2 = children.second();

      if (NODE_COMPARATOR.compare(arg1, arg2) > 0) {
         // for addition the order of the arguments is not important, so order arguments in a consistent way
         // e.g. (+ v1 1) -> (+ 1 v1)
         return new FunctionNode(this, arg2, arg1);
      } else if (numberUtils.isZero(arg1)) {
         // anything plus zero is itself
         // e.g. (+ 0 v0) -> v0
         return arg2;
      } else if (numberUtils.isZero(arg2)) {
         // the earlier ordering or arguments means we should never get here
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
         if (isConstant(fn2.getChildren().first()) && numberUtils.isAddOrSubtract(fn2.getFunction())) {
            return new FunctionNode(fn2.getFunction(), numberUtils.add(arg1, fn2.getChildren().first()), fn2.getChildren().second());
         }
      }

      return simplifier.simplify(this, arg1, arg2);
   }

   @Override
   public String getDisplayName() {
      return "+";
   }
}
