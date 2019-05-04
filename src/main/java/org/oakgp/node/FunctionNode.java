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

import org.oakgp.Assignments;
import org.oakgp.Type;
import org.oakgp.function.Function;

/** Contains a function (operator) and the arguments (operands) to apply to it. */
public final class FunctionNode implements Node {
   /**
    * A sequence of prime numbers.
    * <p>
    * Used to generate the {@code hashCode} value. This is used - rather than calling {@code java.util.Arrays.hashCode(Object a[])} with the node's arguments -
    * so that, for example, the two expressions {@code (- (- (* -1 v3) 0) (- 13 v1))} and {@code (- (- (* -1 v3) 13) (- 0 v1))} have different hash code values.
    * See {@code org.oakgp.node.FunctionNodeTest.testHashCode()} for more details.
    */
   private static final int[] PRIMES = { 2, 3, 5, 7, 11, 13, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97 };

   private final Function function;
   private final ChildNodes arguments;
   private final int nodeCount;
   private final int hashCode;

   /**
    * Constructs a new {@code FunctionNode} with the specified function function and arguments.
    *
    * @param function
    *           the function to associate with this {@code FunctionNode}
    * @param arguments
    *           the arguments (i.e. operands) to apply to {@code function} when evaluating this {@code FunctionNode}
    */
   public FunctionNode(Function function, Node... arguments) {
      this(function, ChildNodes.createChildNodes(arguments));
   }

   /**
    * Constructs a new {@code FunctionNode} with the specified function function and arguments.
    *
    * @param function
    *           the function to associate with this {@code FunctionNode}
    * @param arguments
    *           the arguments (i.e. operands) to apply to {@code function} when evaluating this {@code FunctionNode}
    */
   public FunctionNode(Function function, ChildNodes arguments) {
      this.function = function;
      this.arguments = arguments;
      this.nodeCount = calculateNodeCount(arguments);
      this.hashCode = (function.getClass().getName().hashCode() * 31) * createHashCode(arguments, nodeCount);
   }

   private static int calculateNodeCount(ChildNodes arguments) {
      int total = 1;
      for (int i = 0; i < arguments.size(); i++) {
         total += arguments.getNode(i).getNodeCount();
      }
      return total;
   }

   private static int createHashCode(ChildNodes arguments, int nodeCount) {
      int hashCode = 0;
      int primesIdx = 0;
      for (int i = 0; i < arguments.size(); i++) {
         hashCode += arguments.getNode(i).hashCode() * (PRIMES[primesIdx] + nodeCount);
         if (++primesIdx == PRIMES.length) {
            primesIdx = 0;
         }
      }
      return hashCode;
   }

   public Function getFunction() {
      return function;
   }

   public ChildNodes getChildren() {
      return arguments;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T evaluate(Assignments assignments) {
      return (T) function.evaluate(new FunctionNodeArguments(arguments, assignments));
   }

   @Override
   public int getNodeCount() {
      return nodeCount;
   }

   @Override
   public int getHeight() {
      // TODO it may be beneficial to cache this result on its first call
      int height = 0;
      for (int i = 0; i < arguments.size(); i++) {
         height = Math.max(height, arguments.getNode(i).getHeight());
      }
      return height + 1;
   }

   @Override
   public Type getType() {
      return function.getSignature().getReturnType();
   }

   @Override
   public NodeType getNodeType() {
      return NodeType.FUNCTION;
   }

   @Override
   public int hashCode() {
      return hashCode;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o == null || this.hashCode != o.hashCode()) {
         return false;
      } else if (o instanceof FunctionNode) {
         FunctionNode fn = (FunctionNode) o;
         // NOTE if we often return false here then that indicates hashCode() could be improved
         return this.function == fn.function && this.arguments.equals(fn.arguments);
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append('(').append(function.getDisplayName());
      for (int i = 0; i < arguments.size(); i++) {
         sb.append(' ').append(arguments.getNode(i));
      }
      return sb.append(')').toString();
   }
}
