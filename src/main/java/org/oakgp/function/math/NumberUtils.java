package org.oakgp.function.math;

import org.oakgp.Type;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;

interface NumberUtils {
   Type getType();

   Add getAdd();

   Subtract getSubtract();

   Multiply getMultiply();

   boolean isZero(Node n);

   boolean isOne(Node arg1);

   ConstantNode zero();

   ConstantNode one();

   ConstantNode two();

   boolean isNegative(Node n);

   ConstantNode add(Node n1, Node n2);

   ConstantNode add(Node n, int i);

   ConstantNode subtract(Node n1, Node n2);

   ConstantNode multiply(Node n1, Node n2);

   ConstantNode negate(Node n);
}
