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

import org.oakgp.Assignments;
import org.oakgp.Type;
import org.oakgp.function.Function;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

interface NumberUtils {
   Type getType();

   ArithmeticExpressionSimplifier getSimplifier();

   Add getAdd();

   Subtract getSubtract();

   Multiply getMultiply();

   Divide getDivide();

   default boolean isZero(Node n) {
      return zero().equals(n);
   }

   default boolean isOne(Node n) {
      return one().equals(n);
   }

   ConstantNode zero();

   ConstantNode one();

   ConstantNode two();

   boolean isNegative(Node n);

   ConstantNode add(Node n1, Node n2, Assignments assignments);

   default ConstantNode add(Node n1, Node n2) {
      return add(n1, n2, null);
   }

   ConstantNode increment(Node n);

   ConstantNode decrement(Node n);

   ConstantNode subtract(Node n1, Node n2, Assignments assignments);

   default ConstantNode subtract(Node n1, Node n2) {
      return subtract(n1, n2, null);
   }

   ConstantNode multiply(Node n1, Node n2, Assignments assignments);

   default ConstantNode multiply(Node n1, Node n2) {
      return multiply(n1, n2, null);
   }

   ConstantNode divide(Node n1, Node n2, Assignments assignments);

   ConstantNode negateConstant(Node n);

   default Node negate(Node arg) {
      if (isConstant(arg)) {
         return negateConstant(arg);
      } else {
         return new FunctionNode(getSubtract(), zero(), arg);
      }
   }

   default FunctionNode multiplyByTwo(Node arg) {
      return new FunctionNode(getMultiply(), two(), arg);
   }

   default boolean isAddOrSubtract(Function f) {
      return isAdd(f) || isSubtract(f);
   }

   default boolean isAdd(Function f) {
      return f == getAdd();
   }

   default boolean isSubtract(Node n) {
      return isFunction(n) && isSubtract((FunctionNode) n);
   }

   default boolean isSubtract(FunctionNode n) {
      return isSubtract(n.getFunction());
   }

   default boolean isSubtract(Function f) {
      return f == getSubtract();
   }

   default boolean isMultiply(FunctionNode n) {
      return isMultiply(n.getFunction());
   }

   default boolean isMultiply(Function f) {
      return f == getMultiply();
   }
}
