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
package org.oakgp.function.choice;

import static org.oakgp.node.NodeType.isConstant;
import static org.oakgp.type.CommonTypes.booleanType;

import org.oakgp.Assignments;
import org.oakgp.function.BooleanFunctionUtils;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.function.bool.And;
import org.oakgp.function.bool.Or;
import org.oakgp.function.classify.IsFalse;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;
import org.oakgp.util.Utils;

/**
 * A selection operator that uses a boolean expression to determine which code to evaluate.
 * <p>
 * Expects three arguments:
 * <ol>
 * <li>Conditional statement.</li>
 * <li>Value to evaluate to if the conditional statement is {@code true}.</li>
 * <li>Value to evaluate to if the conditional statement is {@code false}.</li>
 * </ol>
 */
public final class If implements Function { // TODO make singleton
   private static final int TRUE_IDX = 1;
   private static final int FALSE_IDX = 2;

   private final Signature signature;

   /** Constructs a selection operator that returns values of the specified type. */
   public If() {
      Type type = Types.generic("IfReturnType");
      signature = Signature.createSignature(type, booleanType(), type, type);
   }

   @Override
   public Object evaluate(ChildNodes arguments, Assignments assignments) {
      int index = getOutcomeArgumentIndex(arguments.first().evaluate(assignments));
      return arguments.getNode(index).evaluate(assignments);
   }

   @Override
   public Signature getSignature() {
      return signature;
   }

   @Override
   public Node simplify(FunctionNode functionNode) {
      ChildNodes children = functionNode.getChildren();
      Node condition = children.first();
      Node trueBranch = children.second();
      Node falseBranch = children.third();

      // (if v0 v1 v1) -> v1
      if (trueBranch.equals(falseBranch)) {
         return trueBranch;
      }

      // (if true v0 v1) -> v0
      // (if false v0 v1) -> v1
      if (isConstant(condition)) {
         int index = getOutcomeArgumentIndex(condition.evaluate(null));
         return index == TRUE_IDX ? trueBranch : falseBranch;
      }

      // (if v0 true false) -> v0
      if (trueBranch.equals(Utils.TRUE_NODE) && falseBranch.equals(Utils.FALSE_NODE)) {
         return condition;
      }

      // (if v0 false true) -> (false? v0)
      if (trueBranch.equals(Utils.FALSE_NODE) && falseBranch.equals(Utils.TRUE_NODE)) {
         return IsFalse.negate(condition);
      }

      // (if v0 true v1) -> (or v0 v1)
      if (trueBranch.equals(Utils.TRUE_NODE)) {
         return new FunctionNode(Or.getSingleton(), functionNode.getType(), ChildNodes.createChildNodes(condition, falseBranch));
      }

      // (if v0 v1 false) -> (and v0 v1)
      if (falseBranch.equals(Utils.FALSE_NODE)) {
         return new FunctionNode(And.getSingleton(), functionNode.getType(), ChildNodes.createChildNodes(condition, trueBranch));
      }

      // (if v0 false v1) -> (or v0 v1)
      if (trueBranch.equals(Utils.FALSE_NODE)) {
         return new FunctionNode(And.getSingleton(), functionNode.getType(), ChildNodes.createChildNodes(IsFalse.negate(condition), falseBranch));
      }

      // (if v0 v1 true) -> (and v0 v1)
      if (falseBranch.equals(Utils.TRUE_NODE)) {
         return new FunctionNode(Or.getSingleton(), functionNode.getType(), ChildNodes.createChildNodes(IsFalse.negate(condition), trueBranch));
      }

      Node simplifiedTrueBranch = BooleanFunctionUtils.replace(BooleanFunctionUtils.getFactsWhenTrue(condition), trueBranch);
      Node simplifiedFalseBranch = BooleanFunctionUtils.replace(BooleanFunctionUtils.getFactsWhenFalse(condition), falseBranch);
      if (trueBranch != simplifiedTrueBranch || falseBranch != simplifiedFalseBranch) {
         return new FunctionNode(functionNode, ChildNodes.createChildNodes(condition, simplifiedTrueBranch, simplifiedFalseBranch));
      }

      return null;
   }

   private int getOutcomeArgumentIndex(boolean result) {
      return result ? TRUE_IDX : FALSE_IDX;
   }
}
