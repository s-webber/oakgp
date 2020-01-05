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
import static org.oakgp.function.RulesEngineUtils.isEqual;
import static org.oakgp.function.RulesEngineUtils.isExactOpposites;
import static org.oakgp.function.RulesEngineUtils.isFalse;
import static org.oakgp.function.RulesEngineUtils.isNeverBothTrue;
import static org.oakgp.function.RulesEngineUtils.isSubset;
import static org.oakgp.function.RulesEngineUtils.isTrue;
import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.util.NodeComparator.NODE_COMPARATOR;
import static org.oakgp.util.NodeComparator.pickBest;

import java.util.Map.Entry;

import org.oakgp.Arguments;
import org.oakgp.function.BooleanFunction;
import org.oakgp.function.RulesEngine;
import org.oakgp.function.Signature;
import org.oakgp.function.classify.IsFalse;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.util.Utils;

/** Determines if exactly one of two boolean expressions evaluate to {@code true}. */
public final class Xor implements BooleanFunction {
   private static final Signature SIGNATURE = Signature.createSignature(booleanType(), booleanType(), booleanType());
   private static final Xor SINGLETON = new Xor();

   public static Xor getSingleton() {
      return SINGLETON;
   }

   private Xor() {
   }

   @Override
   public Object evaluate(Arguments arguments) {
      boolean b1 = arguments.first();
      boolean b2 = arguments.second();
      return (b1 || b2) && b1 != b2;
   }

   @Override
   public Node simplify(FunctionNode functionNode) {
      Node arg1 = functionNode.getChildren().first();
      Node arg2 = functionNode.getChildren().second();

      // (xor v0 v0) -> false
      if (arg1.equals(arg2)) {
         return Utils.FALSE_NODE;
      }

      // (xor false v0) -> v0
      if (Utils.FALSE_NODE.equals(arg1)) {
         return arg2;
      }
      // (xor v0 false) -> v0
      if (Utils.FALSE_NODE.equals(arg2)) {
         return arg1;
      }

      // (xor true v0) -> (false? v0)
      if (Utils.TRUE_NODE.equals(arg1)) {
         return IsFalse.negate(arg2);
      }
      // (xor v0 true) -> (false? v0)
      if (Utils.TRUE_NODE.equals(arg2)) {
         return IsFalse.negate(arg1);
      }

      if (NODE_COMPARATOR.compare(arg1, arg2) < 0) {
         return new FunctionNode(functionNode, ChildNodes.createChildNodes(arg2, arg1));
      }

      if (isTrue(functionNode)) {
         return Utils.TRUE_NODE;
      }
      if (isFalse(functionNode)) {
         return Utils.FALSE_NODE;
      }

      RulesEngine engine = buildEngine(arg1, arg2);

      // (xor v0 (false v0)) -> true
      if (isExactOpposites(engine, arg1, arg2)) {
         return Utils.TRUE_NODE;
      }

      // (xor (!= v0 v1) (or (> v1 v0) (> v0 v1))) -> false
      if (isEqual(engine, arg1, arg2)) {
         return Utils.FALSE_NODE;
      }

      if (isNeverBothTrue(engine, arg1, arg2)) {
         return new FunctionNode(Or.getSingleton(), booleanType(), functionNode.getChildren());
      }

      // (xor (> v0 v1) (>= v0 v1)) -> (!= v0 v1)
      Node result = getDifference(engine, arg1, arg2);
      if (result != null) {
         return result;
      }
      result = getDifference(engine, arg2, arg1);
      if (result != null) {
         return result;
      }

      return null;
   }

   /** Return expression that is true when both "x" is true and "y" is false. */
   private Node getDifference(RulesEngine engine, Node x, Node y) {
      if (isSubset(engine, x, y)) {
         RulesEngine e1 = buildEngine(x, true);
         RulesEngine e2 = buildEngine(y, false);
         // what is common when "x" is true and "y" is false?
         Node result = null;
         for (Entry<Node, Boolean> entry : e2.getFacts().entrySet()) {
            if (e1.hasFact(entry.getKey()) && e1.getFact(entry.getKey()).equals(entry.getValue())) {
               if (entry.getValue()) {
                  result = pickBest(result, entry.getKey());
               }
            }
         }
         return result;
      } else {
         return null;
      }
   }

   @Override
   public RulesEngine getEngine(FunctionNode fn) {
      Node arg1 = fn.getChildren().first();
      Node arg2 = fn.getChildren().second();

      RulesEngine engine = buildEngine(arg1, arg2);
      engine.addRule(fn, (_e, _fact, _value) -> {
         if (!_value) {
            _e.addRule(arg1, (e, fact, value) -> {
               e.addFact(arg2, value);
            });
            _e.addRule(arg2, (e, fact, value) -> {
               e.addFact(arg1, value);
            });
         }
      });
      // if know outcome of either child then know the other child has the opposite outcome
      engine.addRule(arg1, (e, fact, value) -> {
         e.addFact(arg2, !value);
      });
      engine.addRule(arg2, (e, fact, value) -> {
         e.addFact(arg1, !value);
      });
      return engine;
   }

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }
}
