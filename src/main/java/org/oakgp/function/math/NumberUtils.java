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
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.type.CommonTypes;
import org.oakgp.type.Types.Type;

/**
 * Provides support for working with classes that represent numeric values.
 *
 * @param <T>
 *           the type this instance is concerned with; this will commonly be (but is not limited to) a sub-class of {@code java.lang.Number}
 */
abstract class NumberUtils<T extends Comparable<T>> {
   private final Type type;
   private final T rawZero;
   private final T rawOne;
   private final ConstantNode zero;
   private final ConstantNode one;
   private final ConstantNode two;
   private final ArithmeticExpressionSimplifier simplifier;
   private final Add<T> add;
   private final Subtract<T> subtract;
   private final Multiply<T> multiply;
   private final Divide<T> divide;
   private final Sum<T> sum;

   /**
    * Creates a {@code NumberUtils} for the numeric values represented by instances of {@link #T}.
    *
    * @param type
    *           the {@code Type} associated with type {@link #T}
    * @param zero
    *           the value {@code 0} of type {@link #T}
    * @param one
    *           the value {@code 1} of type {@link #T}
    * @param two
    *           the value {@code 2} of type {@link #T}
    */
   protected NumberUtils(Type type, T zero, T one, T two) {
      if (!type.getParents().contains(CommonTypes.numberType())) { // sanity check
         throw new IllegalStateException(type + " does not extend " + CommonTypes.numberType());
      }

      this.type = type;
      this.rawZero = zero;
      this.rawOne = one;
      this.zero = createConstant(zero);
      this.one = createConstant(one);
      this.two = createConstant(two);
      this.simplifier = new ArithmeticExpressionSimplifier(this);
      this.add = new Add<>(this);
      this.subtract = new Subtract<>(this);
      this.multiply = new Multiply<>(this);
      this.divide = new Divide<>(this);
      this.sum = new Sum<>(this);
   }

   /** Returns the {@code Type} associated with the numeric values this instance is concerned with. */
   public final Type getType() {
      return type;
   }

   /** Returns a {@code ArithmeticExpressionSimplifier} for the numeric type this instance is concerned with. */
   public final ArithmeticExpressionSimplifier getSimplifier() {
      return simplifier;
   }

   /** Returns an addition operator for the numeric type this instance is concerned with. */
   public final Add<T> getAdd() {
      return add;
   }

   /** Returns a subtraction operator for the numeric type this instance is concerned with. */
   public final Subtract<T> getSubtract() {
      return subtract;
   }

   /** Returns a multiplication operator for the numeric type this instance is concerned with. */
   public final Multiply<T> getMultiply() {
      return multiply;
   }

   /** Returns a division operator for the numeric type this instance is concerned with. */
   public final Divide<T> getDivide() {
      return divide;

   }

   /** Returns a sum operator for the numeric type this instance is concerned with. */
   public final Sum<T> getSum() {
      return sum;
   }

   /** Returns true if the given {@code Node} is a {@code ConstantNode} with the value {@code 0} of type {@link #T}. */
   public final boolean isZero(Node n) {
      return zero.equals(n);
   }

   /** Returns true if the given {@code Node} is a {@code ConstantNode} with the value {@code 1} of type {@link #T}. */
   public final boolean isOne(Node n) {
      return one.equals(n);
   }

   /** Returns the value {@code 0} of type {@link #T}. */
   public final T rawZero() { // TODO rename
      return rawZero;
   }

   /** Returns a {@code ConstantNode} with the value {@code 0} of type {@link #T}. */
   public final ConstantNode zero() {
      return zero;
   }

   /** Returns a {@code ConstantNode} with the value {@code 1} of type {@link #T}. */
   public final ConstantNode one() {
      return one;
   }

   /**
    * Returns the result of adding the numeric values represented by the given {@code Node}s.
    *
    * @param n1
    *           a {@code ConstantNode} with a value of type {@link #T}
    * @param n2
    *           a {@code ConstantNode} with a value of type {@link #T}
    */
   public final ConstantNode add(Node n1, Node n2) {
      return createConstant(add(evaluate(n1), evaluate(n2)));
   }

   /**
    * Returns the result of adding one to the numeric value represented by the given node.
    *
    * @param n
    *           a {@code ConstantNode} with a value of type {@link #T}
    */
   public final ConstantNode increment(Node n) {
      return createConstant(add(evaluate(n), rawOne));
   }

   /**
    * Returns the result of subtracting one from the numeric value represented by the given node.
    *
    * @param n
    *           a {@code ConstantNode} with a value of type {@link #T}
    */
   public final ConstantNode decrement(Node n) {
      return createConstant(subtract(evaluate(n), rawOne));
   }

   /**
    * Returns the result of subtracting the numeric values represented by the given {@code Node}s.
    *
    * @param n1
    *           a {@code ConstantNode} with a value of type {@link #T}
    * @param n2
    *           a {@code ConstantNode} with a value of type {@link #T}
    * @return {@code n1} - {@code n2}
    */
   public final ConstantNode subtract(Node n1, Node n2) {
      return createConstant(subtract(evaluate(n1), evaluate(n2)));
   }

   /**
    * Returns the result of negating the numeric value represented by the given node.
    *
    * @param n
    *           the expression to negate (may be a {@code ConstantNode}, {@code VariableNode} or a {@code FunctionNode})
    */
   public final Node negate(Node arg) {
      if (isConstant(arg)) {
         return negateConstant(arg);
      } else {
         return new FunctionNode(subtract, type, zero, arg);
      }
   }

   /**
    * Returns the result of negating the numeric value represented by the given node.
    *
    * @param n
    *           a {@code ConstantNode} with a value of type {@link #T}
    */
   public final ConstantNode negateConstant(Node n) {
      return subtract(zero, n);
   }

   /**
    * Returns the result of multiplying the numeric values represented by the given {@code Node}s.
    *
    * @param n1
    *           a {@code ConstantNode} with a value of type {@link #T}
    * @param n2
    *           a {@code ConstantNode} with a value of type {@link #T}
    */
   public final ConstantNode multiply(Node n1, Node n2) {
      return createConstant(multiply(evaluate(n1), evaluate(n2)));
   }

   /** Returns a new expression which multiplies the given {@code Node} by two. */
   public final FunctionNode multiplyByTwo(Node arg) {
      return new FunctionNode(multiply, type, two, arg);
   }

   /**
    * Returns {@code true} if the numeric value represented by the given node is less than zero.
    *
    * @param n
    *           a {@code ConstantNode} with a value of type {@link #T}
    */
   public final boolean isNegative(Node n) {
      return evaluate(n).compareTo(rawZero) < 0;
   }

   /** Returns {@code true} if the given {@code Function} is the same as the values returned from {@link #getAdd()} or {@link #getSubtract()}. */
   public final boolean isAddOrSubtract(Function f) {
      return isAdd(f) || isSubtract(f);
   }

   /** Returns {@code true} if the given {@code Function} is the same as returned from {@link #getAdd()}. */
   public final boolean isAdd(Function f) {
      return f == add;
   }

   /** Returns {@code true} if the {@code Function} of the given {@code FunctionNode} is the same as returned from {@link #getSubtract()}. */
   public final boolean isSubtract(FunctionNode n) {
      return isSubtract(n.getFunction());
   }

   /** Returns {@code true} if the given {@code Function} is the same as returned from {@link #getSubtract()}. */
   public final boolean isSubtract(Function f) {
      return f == subtract;
   }

   /** Returns {@code true} if the {@code Function} of the given {@code FunctionNode} is the same as returned from {@link #getMultiply()}. */
   public final boolean isMultiply(FunctionNode n) {
      return isMultiply(n.getFunction());
   }

   /** Returns {@code true} if the given {@code Function} is the same as returned from {@link #getMultiply()}. */
   public final boolean isMultiply(Function f) {
      return f == multiply;
   }

   /** Returns {@code true} if the given {@code Function} is the same as returned from {@link #getDivide()}. */
   public final boolean isDivide(Function f) {
      return f == divide;
   }

   /**
    * Returns {@code true} if the given {@code Node} is a {@code FunctionNode} with the same {@code Function} as returned from {@link #getAdd()},
    * {@link #getSubtract()}, {@link #getMultiply()} or {@link #getDivide()}.
    */
   public final boolean isArithmeticExpression(Node n) {
      if (isFunction(n)) {
         FunctionNode fn = (FunctionNode) n;
         Function f = fn.getFunction();
         return isAdd(f) || isSubtract(f) || isMultiply(f) || isDivide(f);
      } else {
         return false;
      }
   }

   protected abstract T add(T i1, T i2);

   protected abstract T subtract(T i1, T i2);

   protected abstract T multiply(T i1, T i2);

   protected abstract T divide(T i1, T i2);

   private ConstantNode createConstant(T o) {
      return new ConstantNode(o, type);
   }

   private T evaluate(Node n) {
      return n.evaluate(null);
   }
}
