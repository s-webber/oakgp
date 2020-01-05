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
package org.oakgp.function.bool;

import static org.oakgp.function.RulesEngineUtils.buildEngine;
import static org.oakgp.function.RulesEngineUtils.findCommonFalses;
import static org.oakgp.function.RulesEngineUtils.isFalse;
import static org.oakgp.function.RulesEngineUtils.isTrue;
import static org.oakgp.function.RulesEngineUtils.replace;
import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.util.NodeComparator.pickBest;

import java.util.Map.Entry;

import org.oakgp.Arguments;
import org.oakgp.function.BooleanFunction;
import org.oakgp.function.RulesEngine;
import org.oakgp.function.RulesEngineUtils;
import org.oakgp.function.Signature;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.util.Utils;

/** Determines if two boolean expressions both evaluate to {@code true}. */
public final class And implements BooleanFunction {
   private static final Signature SIGNATURE = Signature.createSignature(booleanType(), booleanType(), booleanType());
   private static final And SINGLETON = new And();

   public static And getSingleton() {
      return SINGLETON;
   }

   private And() {
   }

   @Override
   public Object evaluate(Arguments arguments) {
      return (boolean) arguments.first() && (boolean) arguments.second();
   }

   @Override
   public Node simplify(FunctionNode functionNode) {
      Node arg1 = functionNode.getChildren().first();
      Node arg2 = functionNode.getChildren().second();

      // (and true false) / (and false true) / (and false false) -> false
      if (Utils.FALSE_NODE.equals(arg1) || Utils.FALSE_NODE.equals(arg2)) {
         return Utils.FALSE_NODE;
      }

      // (and true v0) -> v0
      if (Utils.TRUE_NODE.equals(arg1)) {
         return arg2;
      }
      // (and v0 true) -> v0
      if (Utils.TRUE_NODE.equals(arg2)) {
         return arg1;
      }

      Node result = Utils.toOrderedNode(functionNode);
      if (result != null) {
         return result;
      }

      if (isTrue(functionNode)) {
         return Utils.TRUE_NODE;
      }
      if (isFalse(functionNode)) {
         return Utils.FALSE_NODE;
      }

      // (and (or (false? v0) v1) v0) -> (and v1 v0)
      Node simplifiedRightBranch = replace(buildEngine(arg1, true), arg2);
      Node simplifiedLeftBranch = replace(buildEngine(arg2, true), arg1);
      if (arg1 != simplifiedLeftBranch || arg2 != simplifiedRightBranch) {
         return new FunctionNode(functionNode, ChildNodes.createChildNodes(simplifiedLeftBranch, simplifiedRightBranch));
      }

      RulesEngine engine = buildEngine(arg1, arg2);

      // (and (>= v0 v1) (> v0 v1)) -> (>= v0 v1)
      result = simplify(engine, arg1, arg2);
      if (result != null) {
         return result;
      }
      // (and (> v0 v1) (!= v0 v1)) -> (!= v0 v1)
      result = simplify(engine, arg2, arg1);
      if (result != null) {
         return result;
      }

      // (and (>= v0 v1) (>= v1 v0)) -> (= v0 v1)
      RulesEngine c = buildEngine(functionNode);
      c.addFact(arg1, true);
      c.addFact(arg2, true);
      RulesEngine copy1 = buildEngine(arg1, true);
      RulesEngine copy2 = buildEngine(arg2, true);
      for (Entry<Node, Boolean> f : c.getFacts().entrySet()) {
         if (!f.getKey().equals(functionNode) && !copy1.hasFact(f.getKey()) && !copy2.hasFact(f.getKey())
               && RulesEngineUtils.isEqual(functionNode, f.getKey())) {
            result = pickBest(result, f.getKey());
         }
      }

      return result;
   }

   private Node simplify(RulesEngine engine, Node x, Node y) {
      RulesEngine copy = engine.copy(y, true);
      if (copy.hasFact(x)) {
         if (copy.getFact(x)) {
            // If "x" always true when "y" is true then return "y" (as "x" is redundant).
            return y;
         } else {
            // If "x" always false when "y" is true then return false (as "(and x y)" must always be false).
            return Utils.FALSE_NODE;
         }
      } else {
         return null;
      }
   }

   @Override
   public RulesEngine getEngine(FunctionNode fn) {
      Node arg1 = fn.getChildren().first();
      Node arg2 = fn.getChildren().second();

      RulesEngine engine = buildEngine(arg1, arg2);

      engine.addRule(fn, (e, fact, value) -> {
         if (value) {
            // if the "and" node is true then both its children must be true
            e.addFact(arg1, true);
            e.addFact(arg2, true);
         } else {
            // if the "and" node is false then at least one of its children must be false
            e.addRule(arg1, (_e, _fact, _value) -> {
               if (_value) {
                  _e.addFact(arg2, false);
               }
            });
            e.addRule(arg2, (_e, _fact, _value) -> {
               if (_value) {
                  _e.addFact(arg1, false);
               }
            });
            // // set to false any facts that are false when at least one child is false
            for (Entry<Node, Boolean> commonFact : findCommonFalses(arg1, arg2).entrySet()) {
               e.addFact(commonFact.getKey(), commonFact.getValue());
            }
         }
      });
      // if outcome of both children is true, or outcome of either child is false, then can determine outcome of "and" node
      engine.addRule(arg1, (e, fact, value) -> {
         if (!value) {
            e.addFact(fn, false);
         } else if (e.hasFact(arg2)) {
            e.addFact(fn, value && e.getFact(arg2));
         }
      });
      engine.addRule(arg2, (e, fact, value) -> {
         if (!value) {
            e.addFact(fn, false);
         } else if (e.hasFact(arg1)) {
            e.addFact(fn, value && e.getFact(arg1));
         }
      });
      // add rule for any facts that cause both children to be false
      for (Entry<Node, Boolean> commonFact : findCommonFalses(arg1, arg2).entrySet()) {
         engine.addRule(commonFact.getKey(), (_e, _fact, _value) -> {
            if (_value == commonFact.getValue()) {
               _e.addFact(fn, false);
            }
         });
      }

      return engine;
   }

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }
}
