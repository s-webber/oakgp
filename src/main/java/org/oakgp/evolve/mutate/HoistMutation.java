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

import java.util.function.Predicate;

import org.oakgp.evolve.GeneticOperator;
import org.oakgp.node.Node;
import org.oakgp.node.walk.StrategyWalk;
import org.oakgp.select.NodeSelector;
import org.oakgp.util.Random;
import org.oakgp.util.Utils;

/**
 * Selects a subtree of the parent as a new offspring.
 * <p>
 * The resulting offspring will be smaller than the parent.
 */
public final class HoistMutation implements GeneticOperator {
   private final Random random;

   /** Creates a {@code HoistMutation} that uses the given {@code Random} to select subtrees as new offspring. */
   public HoistMutation(Random random) {
      this.random = random;
   }

   @Override
   public Node evolve(NodeSelector selector) {
      Node root = selector.next();
      Predicate<Node> treeWalkerStrategy = n -> n.getType() == root.getType();
      int nodeCount = StrategyWalk.getNodeCount(root, treeWalkerStrategy);
      if (nodeCount == 1) {
         // if node count == 1 then that indicates that the only node with the same return type
         // as the root node is the root node itself
         return root;
      } else {
         int index = Utils.selectSubNodeIndex(random, nodeCount);
         return StrategyWalk.getAt(root, index, treeWalkerStrategy);
      }
   }
}
