package org.oakgp.node;

public enum NodeType {
   FUNCTION, CONSTANT, VARIABLE;

   public boolean isFunction() {
      return this == FUNCTION;
   }

   public boolean isConstant() {
      return this == CONSTANT;
   }

   public boolean isVariable() {
      return this == VARIABLE;
   }

   public boolean isTerminal() {
      return this != FUNCTION;
   }
}
