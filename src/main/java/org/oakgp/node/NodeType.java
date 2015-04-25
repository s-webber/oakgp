package org.oakgp.node;

public enum NodeType {
   FUNCTION, CONSTANT, VARIABLE;

   public static boolean areFunctions(Node n1, Node n2) {
      return isFunction(n1) && isFunction(n2);
   }

   public static boolean isFunction(Node n) {
      return n.getNodeType() == FUNCTION;
   }

   public static boolean areTerminals(Node n1, Node n2) {
      return isTerminal(n1) && isTerminal(n2);
   }

   public static boolean isTerminal(Node n) {
      return n.getNodeType() != FUNCTION;
   }

   public static boolean isConstant(Node n) {
      return n.getNodeType() == CONSTANT;
   }

   public static boolean isVariable(Node n) {
      return n.getNodeType() == VARIABLE;
   }
}
