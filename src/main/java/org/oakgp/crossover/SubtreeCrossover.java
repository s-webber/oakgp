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
package org.oakgp.crossover;

import java.util.function.Predicate;

import org.oakgp.GeneticOperator;
import org.oakgp.Type;
import org.oakgp.node.Node;
import org.oakgp.select.NodeSelector;
import org.oakgp.util.Random;
import org.oakgp.util.Utils;

/**
 * Performs subtree crossover.
 * <p>
 * A subtree is selected at random from each of the parents. The subtree of the first is replaced by the subtree of the second.
 * </p>
 * <p>
 * Also known as one-point crossover.
 * </p>
 */
public final class SubtreeCrossover implements GeneticOperator {
   private final Random random;

   public SubtreeCrossover(Random random) {
      this.random = random;
   }

   @Override
   public Node evolve(NodeSelector selector) {
      Node parent1 = selector.next();
      Node parent2 = selector.next();
      int to = Utils.selectSubNodeIndex(random, parent1);
      return parent1.replaceAt(to, t -> {
         Type toType = t.getType();
         Predicate<Node> treeWalkerStrategy = n -> n.getType() == toType;
         int nodeCount = parent2.getNodeCount(treeWalkerStrategy);
         if (nodeCount == 0) {
            return t;
         } else {
            int from = random.nextInt(nodeCount);
            return parent2.getAt(from, treeWalkerStrategy);
         }
      });
   }
}
