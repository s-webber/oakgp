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

import org.oakgp.function.Function;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.type.Types.Type;

/** Performs multiplication. */
final class Multiply<T extends Comparable<T>> extends ArithmeticOperator<T> {
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
   protected T evaluate(T arg1, T arg2) {
      return numberUtils.multiply(arg1, arg2);
   }

   @Override
   public Node simplify(FunctionNode functionNode) {
      Type returnType = functionNode.getType();
      ChildNodes children = functionNode.getChildren();
      Node arg1 = children.first();
      Node arg2 = children.second();

      if (NODE_COMPARATOR.compare(arg1, arg2) > 0) {
         // for multiplication the order of the arguments is not important, so order arguments in a consistent way
         // e.g. (* v1 1) -> (* 1 v1)
         return new FunctionNode(this, returnType, arg2, arg1);
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
            ChildNodes args = fn.getChildren();
            Node fnArg1 = args.first();
            Node fnArg2 = args.second();
            if (isConstant(fnArg1)) {
               if (numberUtils.isAddOrSubtract(f)) {
                  return new FunctionNode(f, returnType, numberUtils.multiply(arg1, fnArg1), new FunctionNode(this, returnType, arg1, fnArg2));
               } else if (numberUtils.isMultiply(f)) {
                  return new FunctionNode(this, returnType, numberUtils.multiply(arg1, fnArg1), fnArg2);
               } else if (numberUtils.isDivide(f)) {
                  return null;
               } else {
                  // should never get here
                  throw new IllegalArgumentException();
               }
            } else if (numberUtils.isAddOrSubtract(f)) {
               return new FunctionNode(f, returnType, new FunctionNode(this, returnType, arg1, fnArg1), new FunctionNode(this, returnType, arg1, fnArg2));
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
