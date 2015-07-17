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

/** Provides support for working with instances of {@code java.lang.Number}. */
abstract class NumberUtils<T extends Comparable<T>> {
   private final Type type;
   private final T rawZero;
   private final T rawOne;
   private final ConstantNode zero;
   private final ConstantNode one;
   private final ConstantNode two;
   private final ArithmeticExpressionSimplifier simplifier;
   private final Add add;
   private final Subtract subtract;
   private final Multiply multiply;
   private final Divide divide;

   protected NumberUtils(Type type, T zero, T one, T two) {
      this.type = type;
      this.rawZero = zero;
      this.rawOne = one;
      this.zero = createConstant(zero);
      this.one = createConstant(one);
      this.two = createConstant(two);
      this.simplifier = new ArithmeticExpressionSimplifier(this);
      this.add = new Add(this);
      this.subtract = new Subtract(this);
      this.multiply = new Multiply(this);
      this.divide = new Divide(this);
   }

   public final Type getType() {
      return type;
   }

   public final ArithmeticExpressionSimplifier getSimplifier() {
      return simplifier;
   }

   public final Add getAdd() {
      return add;
   }

   public final Subtract getSubtract() {
      return subtract;
   }

   public final Multiply getMultiply() {
      return multiply;
   }

   public final Divide getDivide() {
      return divide;
   }

   public final boolean isZero(Node n) {
      return zero.equals(n);
   }

   public final boolean isOne(Node n) {
      return one.equals(n);
   }

   public final ConstantNode zero() {
      return zero;
   }

   public final ConstantNode one() {
      return one;
   }

   public final ConstantNode add(Node n1, Node n2, Assignments assignments) {
      return createConstant(add(evaluate(n1, assignments), evaluate(n2, assignments)));
   }

   public final ConstantNode add(Node n1, Node n2) {
      return add(n1, n2, null);
   }

   public final ConstantNode increment(Node n) {
      return createConstant(add(evaluate(n), rawOne));
   }

   public final ConstantNode decrement(Node n) {
      return createConstant(subtract(evaluate(n), rawOne));
   }

   public final ConstantNode subtract(Node n1, Node n2, Assignments assignments) {
      return createConstant(subtract(evaluate(n1, assignments), evaluate(n2, assignments)));
   }

   public final ConstantNode subtract(Node n1, Node n2) {
      return subtract(n1, n2, null);
   }

   public final ConstantNode negateConstant(Node n) {
      return subtract(zero, n);
   }

   public final Node negate(Node arg) {
      if (isConstant(arg)) {
         return negateConstant(arg);
      } else {
         return new FunctionNode(subtract, zero, arg);
      }
   }

   public final ConstantNode multiply(Node n1, Node n2, Assignments assignments) {
      return createConstant(multiply(evaluate(n1, assignments), evaluate(n2, assignments)));
   }

   public final ConstantNode multiply(Node n1, Node n2) {
      return multiply(n1, n2, null);
   }

   public final FunctionNode multiplyByTwo(Node arg) {
      return new FunctionNode(multiply, two, arg);
   }

   public final ConstantNode divide(Node n1, Node n2, Assignments assignments) {
      return createConstant(divide(evaluate(n1, assignments), evaluate(n2, assignments)));
   }

   public final boolean isNegative(Node n) {
      return evaluate(n).compareTo(rawZero) < 0;
   }

   public final boolean isAddOrSubtract(Function f) {
      return isAdd(f) || isSubtract(f);
   }

   public final boolean isAdd(Function f) {
      return f == add;
   }

   public final boolean isSubtract(Node n) {
      return isFunction(n) && isSubtract((FunctionNode) n);
   }

   public final boolean isSubtract(FunctionNode n) {
      return isSubtract(n.getFunction());
   }

   public final boolean isSubtract(Function f) {
      return f == subtract;
   }

   public final boolean isMultiply(FunctionNode n) {
      return isMultiply(n.getFunction());
   }

   public final boolean isMultiply(Function f) {
      return f == multiply;
   }

   protected abstract T add(T i1, T i2);

   protected abstract T subtract(T i1, T i2);

   protected abstract T multiply(T i1, T i2);

   protected abstract T divide(T i1, T i2);

   private final ConstantNode createConstant(T o) {
      return new ConstantNode(o, type);
   }

   private final T evaluate(Node n) {
      return evaluate(n, null);
   }

   private final T evaluate(Node n, Assignments a) {
      return n.evaluate(a);
   }
}
