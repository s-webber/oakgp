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

import java.util.function.Predicate;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Type;
import org.oakgp.function.Function;

/** Contains a function (operator) and the arguments (operands) to apply to it. */
public final class FunctionNode implements Node {
   private static final int PRIMES[] = { 2, 3, 5, 7, 11, 13, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97 };
   private final Function function;
   private final Arguments arguments;
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
      this(function, Arguments.createArguments(arguments));
   }

   /**
    * Constructs a new {@code FunctionNode} with the specified function function and arguments.
    *
    * @param function
    *           the function to associate with this {@code FunctionNode}
    * @param arguments
    *           the arguments (i.e. operands) to apply to {@code function} when evaluating this {@code FunctionNode}
    */
   public FunctionNode(Function function, Arguments arguments) {
      this.function = function;
      this.arguments = arguments;
      this.nodeCount = calculateNodeCount(arguments);
      this.hashCode = (function.getClass().getName().hashCode() * 31) * createHashCode(arguments, nodeCount);
   }

   private static int calculateNodeCount(Arguments arguments) {
      int total = 1;
      for (int i = 0; i < arguments.getArgCount(); i++) {
         total += arguments.getArg(i).getNodeCount();
      }
      return total;
   }

   private static int createHashCode(Arguments arguments, int nodeCount) {
      int hashCode = 0;
      int primesIdx = 0;
      for (int i = 0; i < arguments.getArgCount(); i++) {
         hashCode += arguments.getArg(i).hashCode() * (PRIMES[primesIdx] + nodeCount);
         if (++primesIdx == PRIMES.length) {
            primesIdx = 0;
         }
      }
      return hashCode;
   }

   public Function getFunction() {
      return function;
   }

   public Arguments getArguments() {
      return arguments;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Object evaluate(Assignments assignments) {
      return function.evaluate(arguments, assignments);
   }

   @Override
   public Node replaceAt(int index, java.util.function.Function<Node, Node> replacement) {
      int total = 0;
      for (int i = 0; i < arguments.getArgCount(); i++) {
         Node node = arguments.getArg(i);
         int c = node.getNodeCount();
         if (total + c > index) {
            return new FunctionNode(function, arguments.replaceAt(i, node.replaceAt(index - total, replacement)));
         } else {
            total += c;
         }
      }
      return replacement.apply(this);
   }

   @Override
   public Node replaceAt(int index, java.util.function.Function<Node, Node> replacement, Predicate<Node> treeWalkerStrategy) {
      int total = 0;
      for (int i = 0; i < arguments.getArgCount(); i++) {
         Node node = arguments.getArg(i);
         int c = node.getNodeCount(treeWalkerStrategy);
         if (total + c > index) {
            return new FunctionNode(function, arguments.replaceAt(i, node.replaceAt(index - total, replacement, treeWalkerStrategy)));
         } else {
            total += c;
         }
      }
      return replacement.apply(this);
   }

   @Override
   public Node replaceAll(Predicate<Node> criteria, java.util.function.Function<Node, Node> replacement) {
      if (criteria.test(this)) {
         return replacement.apply(this).replaceAll(criteria, replacement);
      } else {
         boolean updated = false;
         Node[] replacementArgs = new Node[arguments.getArgCount()];
         for (int i = 0; i < arguments.getArgCount(); i++) {
            Node arg = arguments.getArg(i);
            Node replacedArg = arg.replaceAll(criteria, replacement);
            if (arg != replacedArg) {
               updated = true;
            }
            replacementArgs[i] = replacedArg;
         }
         if (updated) {
            return new FunctionNode(function, Arguments.createArguments(replacementArgs));
         } else {
            return this;
         }
      }
   }

   @Override
   public Node getAt(int index) {
      int total = 0;
      for (int i = 0; i < arguments.getArgCount(); i++) {
         Node node = arguments.getArg(i);
         int c = node.getNodeCount();
         if (total + c > index) {
            return node.getAt(index - total);
         } else {
            total += c;
         }
      }
      return this;
   }

   @Override
   public Node getAt(int index, Predicate<Node> treeWalkerStrategy) {
      int total = 0;
      for (int i = 0; i < arguments.getArgCount(); i++) {
         Node node = arguments.getArg(i);
         int c = node.getNodeCount(treeWalkerStrategy);
         if (total + c > index) {
            return node.getAt(index - total, treeWalkerStrategy);
         } else {
            total += c;
         }
      }
      if (!treeWalkerStrategy.test(this)) {
         throw new IllegalStateException();
      }
      return this;
   }

   @Override
   public int getNodeCount() {
      return nodeCount;
   }

   @Override
   public int getNodeCount(Predicate<Node> treeWalkerStrategy) {
      int total = treeWalkerStrategy.test(this) ? 1 : 0;
      for (int i = 0; i < arguments.getArgCount(); i++) {
         total += arguments.getArg(i).getNodeCount(treeWalkerStrategy);
      }
      return total;
   }

   @Override
   public int getHeight() {
      // TODO cache on first call
      int height = 0;
      for (int i = 0; i < arguments.getArgCount(); i++) {
         height = Math.max(height, arguments.getArg(i).getHeight());
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
         // TODO see how often we return false when we get here - as that indicates hashCode() could be improved
         return this.function.getClass().equals(fn.function.getClass()) && this.arguments.equals(fn.arguments);
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append('(').append(function.getDisplayName());
      for (int i = 0; i < arguments.getArgCount(); i++) {
         sb.append(' ').append(arguments.getArg(i));
      }
      return sb.append(')').toString();
   }
}
