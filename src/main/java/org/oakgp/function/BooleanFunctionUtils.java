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
import java.util.HashMap;
import java.util.Map;

import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.walk.NodeWalk;
import org.oakgp.util.Utils;

public class BooleanFunctionUtils {
   public static Node replace(Map<Node, Boolean> f, Node input) {
      return NodeWalk.replaceAll(input, n -> f.containsKey(n), n -> f.get(n) ? Utils.TRUE_NODE : Utils.FALSE_NODE);
   }

   public static Node replaceWithNegation(Map<Node, Boolean> f, Node input) {
      return NodeWalk.replaceAll(input, n -> f.containsKey(n), n -> f.get(n) ? Utils.FALSE_NODE : Utils.TRUE_NODE);
   }

   public static Map<Node, Boolean> getFactsWhenTrue(Node n) {
      if (hasBooleanFunction(n)) {
         FunctionNode fn = (FunctionNode) n;
         BooleanFunction booleanFunction = (BooleanFunction) fn.getFunction();
         Map<Node, Boolean> whenTrue = new HashMap<>();
         whenTrue.put(n, true);
         for (Node s : booleanFunction.getConsequences(fn)) {
            whenTrue.put(s, true);
         }
         Node opposite = booleanFunction.getOpposite(fn);
         if (opposite != null) {
            whenTrue.put(opposite, false);
         }
         for (Node i : booleanFunction.getIncompatibles(fn)) {
            whenTrue.put(i, false);
         }
         return whenTrue;
      } else {
         return Collections.singletonMap(n, true);
      }
   }

   public static Map<Node, Boolean> getFactsWhenFalse(Node n) {
      if (hasBooleanFunction(n)) {
         FunctionNode fn = (FunctionNode) n;
         BooleanFunction booleanFunction = (BooleanFunction) fn.getFunction();
         Map<Node, Boolean> whenFalse = new HashMap<>();
         whenFalse.put(n, false);
         Node opposite = booleanFunction.getOpposite(fn);
         if (opposite != null) {
            whenFalse.put(opposite, true);
         }
         for (Node s : booleanFunction.getCauses(fn)) {
            whenFalse.put(s, false);
         }
         return whenFalse;
      } else {
         return Collections.singletonMap(n, false);
      }
   }

   public static Map<Node, Boolean> getConsequences(Node n) {
      if (hasBooleanFunction(n)) {
         FunctionNode fn = (FunctionNode) n;
         BooleanFunction booleanFunction = (BooleanFunction) fn.getFunction();

         Map<Node, Boolean> conseqeunces = new HashMap<>();
         conseqeunces.put(n, true);
         Node opposite = booleanFunction.getOpposite(fn);
         if (opposite != null) {
            conseqeunces.put(opposite, false);
         }
         for (Node s : booleanFunction.getCauses(fn)) {
            conseqeunces.put(s, true);
         }
         return conseqeunces;
      } else {
         return Collections.singletonMap(n, true);
      }
   }

   public static boolean isOpposite(Node arg1, Node arg2) {
      // Need to check opposites of *both* arguments, rather than just doing "arg1.equals(getOpposite(arg2))",
      // for when one arg is "v0" and the other is "(false? v0)" - as getOpposite("v0") will return null.
      return arg1.equals(getOpposite(arg2)) || arg2.equals(getOpposite(arg1));
   }

   public static Node getOpposite(Node n) {
      if (hasBooleanFunction(n)) {
         FunctionNode fn = (FunctionNode) n;
         return ((BooleanFunction) fn.getFunction()).getOpposite(fn);
      } else {
         return null;
      }
   }

   public static Node getIntersection(Node n, Node other) {
      if (hasBooleanFunction(n)) {
         FunctionNode fn = (FunctionNode) n;
         return ((BooleanFunction) fn.getFunction()).getIntersection(fn, other);
      } else {
         return null;
      }
   }

   public static Node getUnion(Node n, Node other) {
      if (hasBooleanFunction(n)) {
         FunctionNode fn = (FunctionNode) n;
         return ((BooleanFunction) fn.getFunction()).getUnion(fn, other);
      } else {
         return null;
      }
   }

   private static boolean hasBooleanFunction(Node n) {
      return n instanceof FunctionNode && ((FunctionNode) n).getFunction() instanceof BooleanFunction;
   }
}
