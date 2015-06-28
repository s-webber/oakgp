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

import java.util.function.Function;
import java.util.function.Predicate;

import org.oakgp.Assignments;
import org.oakgp.Type;

/** A node represents a single point of a tree structure. */
public interface Node {
   /**
    * Returns the result of evaluating this {@code Node} using the values of the specified {@code Assignments}.
    *
    * @param assignments
    *           represents the values to assign to any variables for this particular evaluation of this {@code Node}
    * @return the result of evaluating this {@code Node} using the values of the specified {@code Assignments}
    */
   <T> T evaluate(Assignments assignments);

   /**
    * Returns the total number of nodes represented by this {@code Node} - including any child-nodes.
    *
    * @return the total number of nodes represented by this {@code Node} - including any child-nodes
    */
   int getNodeCount();

   int getNodeCount(Predicate<Node> treeWalkerStrategy);

   /**
    * Returns a new {@code Node} resulting from replacing the {@code Node} at position {@code index} with the result of {@code replacement}.
    *
    * @param index
    *           the index of the {@code Node}, in the tree structure represented by this object, that needs to be replaced
    * @param replacement
    *           the function to apply to the {@code Node} at {@code index} to determine the {@code Node} that should replace it
    * @return A new {@code Node} derived from replacing the {@code Node} at {@code index} with the result of {@code replacement}
    */
   Node replaceAt(int index, Function<Node, Node> replacement);

   Node replaceAt(int index, Function<Node, Node> replacement, Predicate<Node> treeWalkerStrategy);

   Node replaceAll(Predicate<Node> criteria, Function<Node, Node> replacement);

   /**
    * Returns a {@code Node} from the tree structure represented by this object.
    *
    * @param index
    *           the index of the {@code Node}, in the tree structure represented by this object, that needs to be returned
    * @return the {@code Node} at {@code index} of the tree structure represented by this object
    */
   Node getAt(int index);

   Node getAt(int index, Predicate<Node> treeWalkerStrategy);

   /** The height of a node is the number of edges on the longest downward path between that node and a leaf. */
   int getHeight();

   Type getType();

   NodeType getNodeType();
}
