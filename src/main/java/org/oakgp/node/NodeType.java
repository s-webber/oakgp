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
package org.oakgp.node;

/**
 * Defines the node types used to construct tree structures.
 *
 * @see Node#getNodeType()
 */
public enum NodeType {
   /** @see FunctionNode */
   FUNCTION,
   /** @see ConstantNode */
   CONSTANT,
   /** @see VariableNode */
   VARIABLE;

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
