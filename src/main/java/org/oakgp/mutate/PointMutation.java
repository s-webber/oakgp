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
package org.oakgp.mutate;

import static org.oakgp.node.NodeType.isFunction;

import org.oakgp.GeneticOperator;
import org.oakgp.PrimitiveSet;
import org.oakgp.function.Function;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.select.NodeSelector;
import org.oakgp.util.Random;
import org.oakgp.util.Utils;

/**
 * Randomly changes a point (node) in the parent.
 * <p>
 * A node in the parent is selected at random and replaced with another primitive of the same type and arity.
 * </p>
 * <p>
 * Also known as node replacement mutation.
 * </p>
 */
public final class PointMutation implements GeneticOperator {
   private final Random random;
   private final PrimitiveSet primitiveSet;

   public PointMutation(Random random, PrimitiveSet primitiveSet) {
      this.random = random;
      this.primitiveSet = primitiveSet;
   }

   @Override
   public Node evolve(NodeSelector selector) {
      Node root = selector.next();
      int mutationPoint = Utils.selectSubNodeIndex(random, root);
      return root.replaceAt(mutationPoint, node -> {
         if (isFunction(node)) {
            FunctionNode functionNode = (FunctionNode) node;
            Function function = primitiveSet.nextAlternativeFunction(functionNode.getFunction());
            return new FunctionNode(function, functionNode.getArguments());
         } else {
            return primitiveSet.nextAlternativeTerminal(node);
         }
      });
   }
}
