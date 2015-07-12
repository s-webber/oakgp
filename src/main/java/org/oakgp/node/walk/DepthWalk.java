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

public final class DepthWalk {
   /** Private constructor as all methods are static. */
   private DepthWalk() {
      // do nothing
   }

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

   @FunctionalInterface
   public interface DepthWalkStrategy {
      boolean test(Node node, int depth);
   }

   @FunctionalInterface
   public interface DepthWalkReplacement {
      Node apply(Node node, int depth);
   }
}
