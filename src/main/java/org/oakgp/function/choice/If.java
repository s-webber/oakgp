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

import java.util.function.Predicate;

import org.oakgp.Arguments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.walk.NodeWalk;
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
public final class If implements Function {
   private static final int TRUE_IDX = 1;
   private static final int FALSE_IDX = 2;

   private final Signature signature;

   /** Constructs a selection operator that returns values of the specified type. */
   public If() {
      Type type = Types.generic("Type");
      signature = Signature.createSignature(type, booleanType(), type, type);
   }

   @Override
   public Object evaluate(Arguments arguments) {
      int index = getOutcomeArgumentIndex(arguments.first());
      return arguments.getArg(index);
   }

   @Override
   public Signature getSignature() {
      return signature;
   }

   @Override
   public Node simplify(FunctionNode functionNode) {
      Type returnType = functionNode.getType();
      ChildNodes children = functionNode.getChildren();
      Node trueBranch = children.second();
      Node falseBranch = children.third();
      if (trueBranch.equals(falseBranch)) {
         return trueBranch;
      }

      Node condition = children.first();
      if (isConstant(condition)) {
         int index = getOutcomeArgumentIndex(children.first().evaluate(null));
         return index == TRUE_IDX ? trueBranch : falseBranch;
      }

      Predicate<Node> criteria = n -> n.equals(condition);
      Node simplifiedTrueBranch = NodeWalk.replaceAll(trueBranch, criteria, n -> Utils.TRUE_NODE);
      Node simplifiedFalseBranch = NodeWalk.replaceAll(falseBranch, criteria, n -> Utils.FALSE_NODE);
      if (trueBranch != simplifiedTrueBranch || falseBranch != simplifiedFalseBranch) {
         return new FunctionNode(this, returnType, condition, simplifiedTrueBranch, simplifiedFalseBranch);
      } else {
         return null;
      }
   }

   private int getOutcomeArgumentIndex(boolean result) {
      return result ? TRUE_IDX : FALSE_IDX;
   }
}
