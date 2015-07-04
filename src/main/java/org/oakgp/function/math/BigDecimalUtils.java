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

import java.math.BigDecimal;

import org.oakgp.Assignments;
import org.oakgp.Type;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;

/** Provides support for working with instances of {@code java.math.BigDecimal}. */
public final class BigDecimalUtils implements NumberUtils {
   private static final Type TYPE = Type.bigDecimalType();

   public static final BigDecimalUtils BIG_DECIMAL_UTILS = new BigDecimalUtils();

   private final ArithmeticExpressionSimplifier simplifier = new ArithmeticExpressionSimplifier(this);
   private final ConstantNode zero = createConstant(BigDecimal.ZERO);
   private final ConstantNode one = createConstant(BigDecimal.ONE);
   private final ConstantNode two = createConstant(BigDecimal.valueOf(2));
   private final Add add = new Add(this);
   private final Subtract subtract = new Subtract(this);
   private final Multiply multiply = new Multiply(this);
   private final Divide divide = new Divide(this);

   /** @see #BIG_DECIMAL_UTILS */
   private BigDecimalUtils() {
      // do nothing
   }

   @Override
   public Type getType() {
      return TYPE;
   }

   @Override
   public ArithmeticExpressionSimplifier getSimplifier() {
      return simplifier;
   }

   @Override
   public Add getAdd() {
      return add;
   }

   @Override
   public Subtract getSubtract() {
      return subtract;
   }

   @Override
   public Multiply getMultiply() {
      return multiply;
   }

   @Override
   public Divide getDivide() {
      return divide;
   }

   @Override
   public ConstantNode negateConstant(Node n) {
      BigDecimal bd = n.evaluate(null);
      return createConstant(bd.negate());
   }

   @Override
   public ConstantNode zero() {
      return zero;
   }

   @Override
   public ConstantNode one() {
      return one;
   }

   @Override
   public ConstantNode two() {
      return two;
   }

   @Override
   public boolean isNegative(Node n) {
      BigDecimal bd = n.evaluate(null);
      return bd.compareTo(BigDecimal.ZERO) < 0;
   }

   @Override
   public ConstantNode add(Node n1, Node n2, Assignments assignments) {
      BigDecimal bd1 = n1.evaluate(assignments);
      BigDecimal bd2 = n2.evaluate(assignments);
      return createConstant(bd1.add(bd2));
   }

   @Override
   public ConstantNode increment(Node n) {
      BigDecimal bd = n.evaluate(null);
      return createConstant(bd.add(BigDecimal.ONE));
   }

   @Override
   public ConstantNode decrement(Node n) {
      BigDecimal bd = n.evaluate(null);
      return createConstant(bd.subtract(BigDecimal.ONE));
   }

   @Override
   public ConstantNode subtract(Node n1, Node n2, Assignments assignments) {
      BigDecimal bd1 = n1.evaluate(assignments);
      BigDecimal bd2 = n2.evaluate(assignments);
      return createConstant(bd1.subtract(bd2));
   }

   @Override
   public ConstantNode multiply(Node n1, Node n2, Assignments assignments) {
      BigDecimal bd1 = n1.evaluate(assignments);
      BigDecimal bd2 = n2.evaluate(assignments);
      return createConstant(bd1.multiply(bd2));
   }

   @Override
   public ConstantNode divide(Node n1, Node n2, Assignments assignments) {
      BigDecimal bd1 = n1.evaluate(assignments);
      BigDecimal bd2 = n2.evaluate(assignments);
      return createConstant(bd1.divide(bd2));
   }

   private ConstantNode createConstant(BigDecimal bd) {
      return new ConstantNode(bd, TYPE);
   }
}
