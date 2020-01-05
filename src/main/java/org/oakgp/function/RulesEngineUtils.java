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
import java.util.Map;

import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.walk.NodeWalk;
import org.oakgp.util.Utils;

public class RulesEngineUtils {
   public static RulesEngine buildEngine(Node n, boolean isTrue) { // TODO
      RulesEngine e = buildEngine(n);
      e.addFact(n, isTrue);
      return e;
   }

   public static RulesEngine buildEngine(Node n1, Node n2) { // TODO
      RulesEngine e = buildEngine(n1);
      e.addEngine(buildEngine(n2));
      return e;
   }

   public static RulesEngine buildEngine(Node n) { // TODO
      if (n instanceof FunctionNode && ((FunctionNode) n).getFunction() instanceof BooleanFunction) {
         return ((BooleanFunction) ((FunctionNode) n).getFunction()).getEngine((FunctionNode) n);
      } else {
         return new RulesEngine(); // TODO return null?
      }
   }

   /** Returns true if "y" is always false when "x" is true, and "x" is always false when "y" is true. */
   public static boolean isNeverBothTrue(RulesEngine original, Node x, Node y) {
      try {
         RulesEngine copy = original.copy(x, true);
         if (copy.hasFact(y) && !copy.getFact(y)) {
            copy = original.copy(y, true);
            return copy.hasFact(x) && !copy.getFact(x);
         } else {
            return false;
         }
      } catch (Exception e) {
         return false;
      }
   }

   /** Returns true if "y" is always true when "x" is false, and "x" is always true when "y" is false. */
   public static boolean isExactOpposites(Node x, Node y) {
      RulesEngine e2 = buildEngine(x, y);
      return isExactOpposites(e2, x, y);
   }

   /** Returns true if "y" is always true when "x" is false, and "x" is always true when "y" is false. */
   public static boolean isExactOpposites(RulesEngine original, Node x, Node y) {
      try {
         RulesEngine copy = original.copy(x, true);
         if (!copy.hasFact(y) || copy.getFact(y)) {
            return false;
         }

         copy = original.copy(y, true);
         if (!copy.hasFact(x) || copy.getFact(x)) {
            return false;
         }

         copy = original.copy(x, false);
         if (!copy.hasFact(y) || !copy.getFact(y)) {
            return false;
         }

         copy = original.copy(y, false);
         if (!copy.hasFact(x) || !copy.getFact(x)) {
            return false;
         }

         return true;
      } catch (Exception e) {
         return false;
      }
   }

   /** Returns true if "y" is always true when "x" is true, and "x" is always true when "y" is true. */
   public static boolean isEqual(Node x, Node y) {
      return isEqual(buildEngine(x, y), x, y);
   }

   /** Returns true if "y" is always true when "x" is true, and "x" is always true when "y" is true. */
   public static boolean isEqual(RulesEngine original, Node x, Node y) {
      try {
         RulesEngine copy = original.copy(x, true);
         if (!copy.hasFact(y) || !copy.getFact(y)) {
            return false;
         }

         copy = original.copy(y, true);
         if (!copy.hasFact(x) || !copy.getFact(x)) {
            return false;
         }

         copy = original.copy(x, false);
         if (!copy.hasFact(y) || copy.getFact(y)) {
            return false;
         }

         copy = original.copy(y, false);
         if (!copy.hasFact(x) || copy.getFact(x)) {
            return false;
         }

         return true;
      } catch (Exception e) {
         return false;
      }
   }

   /** Return true if "y" is always true when "x" is true. */
   public static boolean isSubset(RulesEngine engine, Node x, Node y) {
      try {
         RulesEngine copy = engine.copy(x, true);
         return copy.hasFact(y) && copy.getFact(y);
      } catch (Exception e) {
         return false;
      }
   }

   /** Replace any nodes in the given node that have an associated fact in the given rules engine. */
   public static Node replace(RulesEngine e, Node input) {
      return NodeWalk.replaceAll(input, e::hasFact, n -> e.getFact(n) ? Utils.TRUE_NODE : Utils.FALSE_NODE);
   }

   /** Return true if the given fact must evaluate to true. */
   public static boolean isTrue(Node fact) {
      try {
         buildEngine(fact, false);
         return false;
      } catch (RuntimeException e) {
         // buildEngine(fact, true); // TODO only here as a sanity check
         return true;
      }
   }

   /** Return true if the given fact must evaluate to false. */
   public static boolean isFalse(Node fact) {
      try {
         buildEngine(fact, true);
         return false;
      } catch (RuntimeException e) {
         // buildEngine(fact, false); // TODO only here as a sanity check
         return true;
      }
   }

   /** Return facts that are common to both of the given nodes when they are true. */
   public static Map<Node, Boolean> findCommonTruths(Node arg1, Node arg2) {
      try {
         RulesEngine copy1 = buildEngine(arg1, true);
         RulesEngine copy2 = buildEngine(arg2, true);
         return copy1.getCommonFacts(copy2);
      } catch (Exception e) {
         return Collections.emptyMap();
      }
   }

   public static Map<Node, Boolean> findCommonFalses(Node arg1, Node arg2) {
      try {
         RulesEngine copy1 = buildEngine(arg1, false);
         RulesEngine copy2 = buildEngine(arg2, false);
         return copy1.getCommonFacts(copy2);
      } catch (Exception e) {
         return Collections.emptyMap();
      }
   }
}
