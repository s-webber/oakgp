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

import org.oakgp.node.ChildNodes;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

/** Performs division. */
final class Divide<T extends Comparable<T>> extends ArithmeticOperator<T> {
   private final NumberUtils<T> numberUtils;
   private final ConstantNode minusOne;

   /** @see NumberUtils#getDivide() */
   Divide(NumberUtils<T> numberUtils) {
      super(numberUtils.getType());
      this.numberUtils = numberUtils;
      this.minusOne = numberUtils.negateConstant(numberUtils.one());
   }

   @Override
   protected T evaluate(T arg1, T arg2) {
      if (numberUtils.zero().evaluate(null).equals(arg2)) { // TODO
         return (T) numberUtils.one().evaluate(null);
      } else {
         return numberUtils.divide(arg1, arg2);
      }
   }

   @Override
   public Node simplify(FunctionNode functionNode) {
      ChildNodes children = functionNode.getChildren();
      Node arg2 = children.second();
      if (numberUtils.isZero(arg2)) {
         return numberUtils.one();
      } else if (numberUtils.isOne(arg2)) {
         return children.first();
      } else if (minusOne.equals(arg2)) {
         return numberUtils.negate(children.first());
      } else if (children.first().equals(arg2)) {
         return numberUtils.one();
      } else {
         return null;
      }
   }

   @Override
   public String getDisplayName() {
      return "/";
   }
}
