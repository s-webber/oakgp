package org.oakgp.function.math;

import org.oakgp.Type;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;

final class IntegerUtils {
   final ConstantNode ZERO = createConstant(0);
   final ConstantNode ONE = createConstant(1);
   final ConstantNode TWO = createConstant(2);

   boolean isNegative(Node n) {
      return ((int) n.evaluate(null)) < 0;
   }

   ConstantNode add(Node n1, Node n2) {
      int i1 = n1.evaluate(null);
      int i2 = n2.evaluate(null);
      return createConstant(i1 + i2);
   }

   public Node add(Node n, int i) {
      return createConstant((int) n.evaluate(null) + i);
   }

   ConstantNode subtract(Node n1, Node n2) {
      int i1 = n1.evaluate(null);
      int i2 = n2.evaluate(null);
      return createConstant(i1 - i2);
   }

   ConstantNode multiply(Node n1, Node n2) {
      int i1 = n1.evaluate(null);
      int i2 = n2.evaluate(null);
      return createConstant(i1 * i2);
   }

   ConstantNode negate(Node n) {
      return createConstant(-(int) n.evaluate(null));
   }

   private ConstantNode createConstant(int i) {
      return new ConstantNode(i, Type.integerType());
   }
}
