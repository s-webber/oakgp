package org.oakgp.function.math;

import org.oakgp.Assignments;
import org.oakgp.Type;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;

interface NumberUtils {
   Type getType();

   ArithmeticExpressionSimplifier getSimplifier();

   Add getAdd();

   Subtract getSubtract();

   Multiply getMultiply();

   boolean isZero(Node n);

   boolean isOne(Node arg1);

   ConstantNode zero();

   ConstantNode one();

   ConstantNode two();

   boolean isNegative(Node n);

   ConstantNode add(Node n1, Node n2, Assignments assignments);

   default ConstantNode add(Node n1, Node n2) {
      return add(n1, n2, null);
   }

   ConstantNode add(Node n, int i);

   ConstantNode subtract(Node n1, Node n2, Assignments assignments);

   default ConstantNode subtract(Node n1, Node n2) {
      return subtract(n1, n2, null);
   }

   ConstantNode multiply(Node n1, Node n2, Assignments assignments);

   default ConstantNode multiply(Node n1, Node n2) {
      return multiply(n1, n2, null);
   }

   ConstantNode negate(Node n);
}
