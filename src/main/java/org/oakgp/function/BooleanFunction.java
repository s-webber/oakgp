/*
 * Copyright 2019 S. Webber
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
package org.oakgp.function;

import java.util.Collections;
import java.util.Set;

import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

/** Defines methods to support the simplification of functions that return a boolean. */
public interface BooleanFunction extends Function {
   /**
    * Return a Node that is the exact opposite of the given FunctionNode.
    *
    * i.e. When the given FunctionNode evaluates to true the returned Node evaluates to false. When the returned Node evaluates to true the given FunctionNode
    * evaluates to false.
    *
    * Example: if the given FunctionNode represents "(= v0 v1)" then "(!= v0 v1)" would be a suitable value to return.
    */
   default Node getOpposite(FunctionNode fn) {
      return null;
   }

   /**
    * Returns a set of Nodes that always evaluate to false when the given FunctionNode evaluates to true.
    *
    * Example: if the given FunctionNode represents "(pos? v0)" then "(neg? v0)" would be a suitable value to return.
    */
   default Set<Node> getIncompatibles(FunctionNode fn) {
      return Collections.emptySet();
   }

   /**
    * Returns a set of Nodes that always evaluate to true when the given FunctionNode evaluates to true.
    *
    * Example: if the given FunctionNode represents "(zero? v0)" then "(even? v0)" would be a suitable value to return.
    */
   default Set<Node> getConsequences(FunctionNode fn) {
      return Collections.emptySet();
   }

   /**
    * Returns a set of Nodes that when any of them evaluate to true then the given FunctionNode always evaluates to true.
    *
    * Example: if the given FunctionNode represents "(even? v0)" then "(zero? v0)" would be a suitable value to return.
    */
   default Set<Node> getCauses(FunctionNode fn) {
      return Collections.emptySet();
   }

   /**
    * Returns a Node that always evaluates to true when both of the given nodes evaluate to true and always evaluates to false when either of the given nodes
    * evaluates to false.
    *
    * Example: Given the arguments "(>= v0 v1)" and "(>= v1 v0)" then "(= v0 v1)" would be a suitable value to return.
    */
   default Node getIntersection(FunctionNode fn, Node n) {
      return null;
   }

   /**
    * Returns a Node that always evaluates to true when either of the given nodes evaluate to true and always evaluates to false when neither of the given nodes
    * evaluates to false.
    *
    * Example: Given the arguments "(> v0 v1)" and "(>= v1 v0)" then "true" would be a suitable value to return.
    */
   default Node getUnion(FunctionNode fn, Node n) {
      return null;
   }
}
