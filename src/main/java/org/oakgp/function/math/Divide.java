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

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;

/** Performs division. */
final class Divide extends ArithmeticOperator {
   private final NumberUtils numberUtils;
   private final ConstantNode minusOne;

   Divide(NumberUtils numberUtils) {
      super(numberUtils.getType());
      this.numberUtils = numberUtils;
      this.minusOne = numberUtils.negateConstant(numberUtils.one());
   }

   @Override
   protected Object evaluate(Node arg1, Node arg2, Assignments assignments) {
      if (numberUtils.isZero(arg2)) {
         return numberUtils.one().evaluate(null);
      } else {
         return numberUtils.divide(arg1, arg2, assignments).evaluate(null);
      }
   }

   @Override
   public Node simplify(Arguments arguments) {
      Node arg2 = arguments.secondArg();
      if (numberUtils.isZero(arg2)) {
         return numberUtils.one();
      } else if (numberUtils.isOne(arg2)) {
         return arguments.firstArg();
      } else if (minusOne.equals(arg2)) {
         return numberUtils.negate(arguments.firstArg());
      } else {
         return null;
      }
   }

   @Override
   public String getDisplayName() {
      return "/";
   }
}
