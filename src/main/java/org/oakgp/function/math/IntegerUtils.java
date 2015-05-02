package org.oakgp.function.math;

import org.oakgp.Assignments;
import org.oakgp.Type;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;

public final class IntegerUtils implements NumberUtils {
   private static final Type TYPE = Type.integerType();

   public static final IntegerUtils INTEGER_UTILS = new IntegerUtils();

   private final ArithmeticExpressionSimplifier simplifier = new ArithmeticExpressionSimplifier(this);
   private final Add add = new Add(this);
   private final Subtract subtract = new Subtract(this);
   private final Multiply multiply = new Multiply(this);
   private final ConstantNode zero = createConstant(0);
   private final ConstantNode one = createConstant(1);
   private final ConstantNode two = createConstant(2);

   /** @see #INTEGER_UTILS */
   private IntegerUtils() {
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
   public ConstantNode negate(Node n) {
      return createConstant(-evaluate(n));
   }

   @Override
   public boolean isZero(Node n) {
      return zero.equals(n);
   }

   @Override
   public boolean isOne(Node n) {
      return one.equals(n);
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
      return evaluate(n) < 0;
   }

   @Override
   public ConstantNode add(Node n1, Node n2, Assignments assignments) {
      int i1 = n1.evaluate(assignments);
      int i2 = n2.evaluate(assignments);
      return createConstant(i1 + i2);
   }

   @Override
   public ConstantNode increment(Node n) {
      return createConstant(evaluate(n) + 1);
   }

   @Override
   public ConstantNode decrement(Node n) {
      return createConstant(evaluate(n) - 1);
   }

   @Override
   public ConstantNode subtract(Node n1, Node n2, Assignments assignments) {
      int i1 = n1.evaluate(assignments);
      int i2 = n2.evaluate(assignments);
      return createConstant(i1 - i2);
   }

   @Override
   public ConstantNode multiply(Node n1, Node n2, Assignments assignments) {
      int i1 = n1.evaluate(assignments);
      int i2 = n2.evaluate(assignments);
      return createConstant(i1 * i2);
   }

   private ConstantNode createConstant(int i) {
      return new ConstantNode(i, TYPE);
   }

   private int evaluate(Node n) {
      return n.evaluate(null);
   }
}
