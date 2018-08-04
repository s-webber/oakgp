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
import static org.oakgp.util.NodeComparator.NODE_COMPARATOR;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

/** Performs multiplication. */
final class Multiply<T extends Comparable<T>> extends ArithmeticOperator {
   private final NumberUtils<T> numberUtils;

   /** @see NumberUtils#getMultiply() */
   Multiply(NumberUtils<T> numberUtils) {
      super(numberUtils.getType());
      this.numberUtils = numberUtils;
   }

   /**
    * Returns the result of multiplying the two elements of the specified arguments.
    *
    * @return the result of multiplying {@code arg1} and {@code arg2}
    */
   @Override
   protected T evaluate(Node arg1, Node arg2, Assignments assignments) {
      return numberUtils.multiply(arg1, arg2, assignments);
   }

   @Override
   public Node simplify(Arguments arguments) {
      Node arg1 = arguments.firstArg();
      Node arg2 = arguments.secondArg();

      if (NODE_COMPARATOR.compare(arg1, arg2) > 0) {
         // as for addition the order of the arguments is not important, order arguments in a consistent way
         // e.g. (* v1 1) -> (* 1 v1)
         return new FunctionNode(this, arg2, arg1);
      } else if (numberUtils.isZero(arg1)) {
         // anything multiplied by zero is zero
         // e.g. (* 0 v0) -> 0
         return numberUtils.zero();
      } else if (numberUtils.isZero(arg2)) {
         // the earlier ordering or arguments means we should never get here
         throw new IllegalArgumentException("arg1 " + arg1 + " arg2 " + arg2);
      } else if (numberUtils.isOne(arg1)) {
         // anything multiplied by one is itself
         // e.g. (* 1 v0) -> v0
         return arg2;
      } else if (numberUtils.isOne(arg2)) {
         // the earlier ordering or arguments means we should never get here
         throw new IllegalArgumentException("arg1 " + arg1 + " arg2 " + arg2);
      } else {
         if (isConstant(arg1) && numberUtils.isArithmeticExpression(arg2)) {
            FunctionNode fn = (FunctionNode) arg2;
            Function f = fn.getFunction();
            Arguments args = fn.getArguments();
            Node fnArg1 = args.firstArg();
            Node fnArg2 = args.secondArg();
            if (isConstant(fnArg1)) {
               if (numberUtils.isAddOrSubtract(f)) {
                  return new FunctionNode(f, numberUtils.multiply(arg1, fnArg1), new FunctionNode(this, arg1, fnArg2));
               } else if (numberUtils.isMultiply(f)) {
                  return new FunctionNode(this, numberUtils.multiply(arg1, fnArg1), fnArg2);
               } else {
                  throw new IllegalArgumentException();
               }
            } else if (numberUtils.isAddOrSubtract(f)) {
               return new FunctionNode(f, new FunctionNode(this, arg1, fnArg1), new FunctionNode(this, arg1, fnArg2));
            }
         }

         return null;
      }
   }

   @Override
   public String getDisplayName() {
      return "*";
   }
}
