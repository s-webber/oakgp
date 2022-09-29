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
package org.oakgp.evolve.mutate;

import static org.oakgp.node.NodeType.isFunction;

import org.oakgp.generate.TreeGenerator;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.walk.NodeWalk;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.primitive.PrimitiveSet;
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
public final class PointMutation implements MutateOperator {

   @Override
   public Node mutate(Node input, PrimitiveSet primitiveSet, TreeGenerator treeGenerator, Random random) {
      int mutationPoint = Utils.selectSubNodeIndex(random, input);
      return NodeWalk.replaceAt(input, mutationPoint, node -> {
         if (isFunction(node)) {
            FunctionNode functionNode = (FunctionNode) node;
            FunctionSet.Key key = primitiveSet.nextAlternativeFunction(functionNode);
            if (key.getReturnType() != node.getType()) {
               // should never get here
               throw new RuntimeException();
            }
            return new FunctionNode(key.getFunction(), node.getType(), functionNode.getChildren());
         } else {
            return primitiveSet.nextAlternativeTerminal(node);
         }
      });
   }
}
