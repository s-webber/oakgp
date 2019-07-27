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

import java.util.function.Function;
import java.util.function.Predicate;

import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.NodeType;

/** Provides a mechanism for recursively visiting <i>all</i> nodes in a tree structure. */
public final class NodeWalk {
   /** Private constructor as all methods are static. */
   private NodeWalk() {
      // do nothing
   }

   /**
    * Returns a {@code Node} from the tree structure represented by the given {@code Node}.
    *
    * @param index
    *           the index of the {@code Node}, in the tree structure represented by {@code node}, that needs to be returned
    * @return the {@code Node} at {@code index} of the tree structure represented by {@code node}
    */
   public static Node getAt(Node node, int index) {
      if (NodeType.isFunction(node)) {
         FunctionNode functionNode = (FunctionNode) node;
         ChildNodes children = functionNode.getChildren();
         int total = 0;
         for (int i = 0; i < children.size(); i++) {
            Node child = children.getNode(i);
            int c = child.getNodeCount();
            if (total + c > index) {
               return getAt(child, index - total);
            } else {
               total += c;
            }
         }
      }
      return node;
   }

   /**
    * Returns a new {@code Node} resulting from replacing the {@code Node} at position {@code index} of the given {@code Node} with the result of
    * {@code replacement}.
    *
    * @param index
    *           the index of the {@code Node}, in the tree structure represented by {@code node}, that needs to be replaced
    * @param replacement
    *           the function to apply to the {@code Node} at {@code index} to determine the {@code Node} that should replace it
    * @return a new {@code Node} derived from replacing the {@code Node} at {@code index} with the result of {@code replacement}
    */
   public static Node replaceAt(Node node, int index, Function<Node, Node> replacement) {
      if (NodeType.isFunction(node)) {
         FunctionNode functionNode = (FunctionNode) node;
         ChildNodes children = functionNode.getChildren();
         int total = 0;
         for (int i = 0; i < children.size(); i++) {
            Node child = children.getNode(i);
            int c = child.getNodeCount();
            if (total + c > index) {
               return new FunctionNode(functionNode, children.replaceAt(i, replaceAt(child, index - total, replacement)));
            } else {
               total += c;
            }
         }
      }
      return replacement.apply(node);
   }

   /**
    * Returns a new {@code Node} resulting from replacing any components that match the specified predicate with the result of applying the specified function.
    *
    * @param criteria
    *           the predicate used to determine if a node should be replaced
    * @param replacement
    *           the function used to determine what a node should be replaced with
    */
   public static Node replaceAll(Node node, Predicate<Node> criteria, Function<Node, Node> replacement) {
      if (NodeType.isFunction(node)) {
         if (criteria.test(node)) {
            return replaceAll(replacement.apply(node), criteria, replacement);
         } else {
            FunctionNode functionNode = (FunctionNode) node;
            ChildNodes children = functionNode.getChildren();
            boolean updated = false;
            Node[] replacementArgs = new Node[children.size()];
            for (int i = 0; i < children.size(); i++) {
               Node arg = children.getNode(i);
               Node replacedArg = replaceAll(arg, criteria, replacement);
               if (arg != replacedArg) {
                  updated = true;
               }
               replacementArgs[i] = replacedArg;
            }
            if (updated) {
               return new FunctionNode(functionNode, ChildNodes.createChildNodes(replacementArgs));
            } else {
               return node;
            }
         }
      } else {
         if (criteria.test(node)) {
            return replacement.apply(node);
         } else {
            return node;
         }
      }
   }
}
