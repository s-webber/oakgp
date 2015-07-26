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
package org.oakgp.node.walk;

import org.oakgp.Arguments;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.NodeType;

/** Provides a mechanism for recursively visiting nodes in a tree structure while keeping track of the depth of each node. */
public final class DepthWalk {
   /** Private constructor as all methods are static. */
   private DepthWalk() {
      // do nothing
   }

   /**
    * Returns the total number of nodes contained in the tree-structure represented by the given {@code Node} that match the specified predicate.
    *
    * @param treeWalkerStrategy
    *           the predicate used to determine if a {@code Node} should be included in the count
    */
   public static int getNodeCount(Node tree, DepthWalkStrategy treeWalkerStrategy) {
      return getNodeCount(tree, treeWalkerStrategy, 1);
   }

   private static int getNodeCount(Node node, DepthWalkStrategy treeWalkerStrategy, int currentDepth) {
      if (NodeType.isFunction(node)) {
         int total = treeWalkerStrategy.test(node, currentDepth) ? 1 : 0;
         FunctionNode functionNode = (FunctionNode) node;
         Arguments arguments = functionNode.getArguments();
         for (int i = 0; i < arguments.getArgCount(); i++) {
            total += getNodeCount(arguments.getArg(i), treeWalkerStrategy, currentDepth + 1);
         }
         return total;
      } else {
         return treeWalkerStrategy.test(node, currentDepth) ? 1 : 0;
      }
   }

   /** Returns a {@code Node} from the tree structure represented by the given {@code Node} that matches the specified predicate. */
   public static Node getAt(Node current, int index, DepthWalkStrategy treeWalkerStrategy) {
      return getAt(current, index, treeWalkerStrategy, 1);
   }

   private static Node getAt(Node current, int index, DepthWalkStrategy treeWalkerStrategy, int currentDepth) {
      if (NodeType.isFunction(current)) {
         int total = 0;
         FunctionNode functionNode = (FunctionNode) current;
         Arguments arguments = functionNode.getArguments();
         for (int i = 0; i < arguments.getArgCount(); i++) {
            Node child = arguments.getArg(i);
            int c = getNodeCount(child, treeWalkerStrategy, currentDepth + 1);
            if (total + c > index) {
               return getAt(child, index - total, treeWalkerStrategy, currentDepth + 1);
            } else {
               total += c;
            }
         }
         if (!treeWalkerStrategy.test(current, currentDepth)) {
            throw new IllegalStateException();
         }
      }
      return current;
   }

   /**
    * Returns a new {@code Node} resulting from replacing the {@code Node} at position {@code index} of the given {@code Node} with the result of
    * {@code replacement}.
    *
    * @param index
    *           the index of the {@code Node}, in the tree structure represented by this object, that needs to be replaced
    * @param replacement
    *           the function to apply to the {@code Node} at {@code index} to determine the {@code Node} that should replace it
    * @return a new {@code Node} derived from replacing the {@code Node} at {@code index} with the result of {@code replacement}
    */
   public static Node replaceAt(Node current, int index, DepthWalkReplacement replacement) {
      return replaceAt(current, index, replacement, 1);
   }

   private static Node replaceAt(Node current, int index, DepthWalkReplacement replacement, int currentDepth) {
      if (NodeType.isFunction(current)) {
         int total = 0;
         FunctionNode functionNode = (FunctionNode) current;
         Arguments arguments = functionNode.getArguments();
         for (int i = 0; i < arguments.getArgCount(); i++) {
            Node child = arguments.getArg(i);
            int c = child.getNodeCount();
            if (total + c > index) {
               return new FunctionNode(functionNode.getFunction(), arguments.replaceAt(i, replaceAt(child, index - total, replacement, currentDepth + 1)));
            } else {
               total += c;
            }
         }
      }
      return replacement.apply(current, currentDepth);
   }

   /** Defines a strategy for determining which nodes should be considered when walking a tree structure. */
   @FunctionalInterface
   public interface DepthWalkStrategy {
      /**
       * @param node
       *           a node that has been encountered while walking a tree structure
       * @param depth
       *           the depth (distance from the root) of the specified {@code node} within the tree structure being walked
       * @return {@code true} if this node should be considered, else {@code false}
       */
      boolean test(Node node, int depth);
   }

   /** Defines a strategy for determining a replacement for a node encountered when walking a tree structure. */
   @FunctionalInterface
   public interface DepthWalkReplacement {
      /**
       * @param node
       *           a node that has been encountered while walking a tree structure
       * @param depth
       *           the depth (distance from the root) of the specified {@code node} within the tree structure being walked
       * @return a node that should replace the specified {@code node} within the tree structure being walked
       */
      Node apply(Node node, int depth);
   }
}
