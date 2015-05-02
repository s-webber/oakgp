package org.oakgp.function.math;

import static org.oakgp.node.NodeType.isConstant;

import org.oakgp.Assignments;
import org.oakgp.Type;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
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
}
