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
import static org.oakgp.function.RulesEngineUtils.findCommonTruths;
import static org.oakgp.function.RulesEngineUtils.isExactOpposites;
import static org.oakgp.function.RulesEngineUtils.isFalse;
import static org.oakgp.function.RulesEngineUtils.isSubset;
import static org.oakgp.function.RulesEngineUtils.isTrue;
import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.util.NodeComparator.pickBest;

import java.util.Map.Entry;

import org.oakgp.Arguments;
import org.oakgp.function.BooleanFunction;
import org.oakgp.function.RulesEngine;
import org.oakgp.function.RulesEngineUtils;
import org.oakgp.function.Signature;
import org.oakgp.function.classify.IsFalse;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.util.Utils;

/** Determines if at least one of two boolean expressions evaluate to {@code true}. */
public final class Or implements BooleanFunction {
   private static final Signature SIGNATURE = Signature.createSignature(booleanType(), booleanType(), booleanType());
   private static final Or SINGLETON = new Or();

   public static Or getSingleton() {
      return SINGLETON;
   }

   private Or() {
   }

   @Override
   public Object evaluate(Arguments arguments) {
      return (boolean) arguments.first() || (boolean) arguments.second();
   }

   @Override
   public Node simplify(FunctionNode functionNode) {
      Node arg1 = functionNode.getChildren().first();
      Node arg2 = functionNode.getChildren().second();

      // (or true false) / (or false true) / (or true true) -> true
      if (Utils.TRUE_NODE.equals(arg1) || Utils.TRUE_NODE.equals(arg2)) {
         return Utils.TRUE_NODE;
      }

      // (or false v0) -> v0
      if (Utils.FALSE_NODE.equals(arg1)) {
         return arg2;
      }
      // (or v0 false) -> v0
      if (Utils.FALSE_NODE.equals(arg2)) {
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

      RulesEngine engine = buildEngine(arg1, arg2);

      // (or (> v0 v1) (>= v0 v1)) -> (>= v0 v1)
      if (isSubset(engine, arg1, arg2)) {
         return arg2;
      }
      // (or (>= v0 v1) (> v0 v1)) -> (>= v0 v1)
      if (isSubset(engine, arg2, arg1)) {
         return arg1;
      }

      // (or (>= v0 v1) (> v1 v0)) -> true
      if (isExactOpposites(engine, arg1, arg2)) {
         return Utils.TRUE_NODE;
      }

      // (or (> v0 v1) (> v1 v0)) -> (!= v1 v0)
      return findCommonFact(functionNode, arg1, arg2);
   }

   private Node findCommonFact(FunctionNode functionNode, Node arg1, Node arg2) {
      Node result = null;

      for (Entry<Node, Boolean> commonFact : findCommonTruths(arg1, arg2).entrySet()) {
         if (RulesEngineUtils.isEqual(commonFact.getKey(), functionNode)) {
            result = pickBest(result, commonFact.getKey());
         } else if (RulesEngineUtils.isExactOpposites(commonFact.getKey(), functionNode)) {
            result = pickBest(result, IsFalse.negate(commonFact.getKey()));
         }
      }

      return result;
   }

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }

   @Override
   public RulesEngine getEngine(FunctionNode fn) {
      Node arg1 = fn.getChildren().first();
      Node arg2 = fn.getChildren().second();

      final RulesEngine engine = buildEngine(arg1, arg2);

      engine.addRule(fn, (_e, _fact, _value) -> {
         if (_value) {
            // if "or" node is true then if one child is false then know the other child must be true
            _e.addRule(arg1, (e, fact, value) -> {
               if (!value) {
                  e.addFact(arg2, true);
               }
            });
            _e.addRule(arg2, (e, fact, value) -> {
               if (!value) {
                  e.addFact(arg1, true);
               }
            });
         } else {
            // find what facts must be true if both children are false
            RulesEngine copy1 = buildEngine(arg1, false);
            RulesEngine copy2 = buildEngine(arg2, false);
            for (Entry<Node, Boolean> commonFact : copy1.getCommonFacts(copy2).entrySet()) {
               if (!_e.hasFact(commonFact.getKey())) {
                  _e.addFact(commonFact.getKey(), commonFact.getValue());
               }
            }

            // if "or" node is false then both children must be false
            _e.addFact(arg1, false);
            _e.addFact(arg2, false);
         }
      });
      // if know that outcome of either child is "true", or outcome of both children is "false", then can determine outcome of "or" node
      engine.addRule(arg1, (e, fact, value) -> {
         if (value) {
            e.addFact(fn, true);
         } else if (e.hasFact(arg2) && !e.getFact(arg2)) {
            e.addFact(fn, false);
         }
      });
      engine.addRule(arg2, (e, fact, value) -> {
         if (value) {
            e.addFact(fn, true);
         } else if (e.hasFact(arg1) && !e.getFact(arg1)) {
            e.addFact(fn, false);
         }
      });

      // find what facts must be true if either child is true
      for (Entry<Node, Boolean> commonFact : findCommonTruths(arg1, arg2).entrySet()) {
         if (commonFact.getValue()) {
            engine.addRule(fn, (_e, _fact, _value) -> {
               _e.addFact(commonFact.getKey(), commonFact.getValue() == _value);
            });
         } else {
            RulesEngine eee = buildEngine(commonFact.getKey(), true);
            if (eee.hasFact(arg1) && eee.hasFact(arg2) && !eee.getFact(arg1) && !eee.getFact(arg2)) {
               engine.addRule(commonFact.getKey(), (_e, _fact, _value) -> {
                  _e.addFact(fn, commonFact.getValue() == _value);
               });
               engine.addRule(fn, (_e, _fact, _value) -> {
                  _e.addFact(commonFact.getKey(), commonFact.getValue() == _value);
               });
            }
         }
      }

      return engine;
   }
}
